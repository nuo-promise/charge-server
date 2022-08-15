package net.suparking.chargeserver.project.repository;

import net.suparking.chargeserver.car.repository.ProtocolRepositoryImpl;
import net.suparking.chargeserver.project.MatchStrategy;
import net.suparking.chargeserver.project.ParkingConfig;
import net.suparking.chargeserver.project.Project;
import net.suparking.chargeserver.project.ProjectRepository;
import net.suparking.chargeserver.project.SubAreaStrategy;
import net.suparking.chargeserver.repository.BasicRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Order(100)
@Repository("ProjectRepositoryImpl")
public class ProjectRepositoryImpl extends BasicRepositoryImpl implements ProjectRepository, CommandLineRunner {

    private final Map<String, Project> projectMap = new ConcurrentHashMap<String, Project>(10);

    private static final Logger log = LoggerFactory.getLogger(ProtocolRepositoryImpl.class);

    @Autowired
    public ProjectRepositoryImpl(@Qualifier("MongoTemplate") MongoTemplate template) {
        super(template);
    }

    @Override
    public synchronized void reloadAll() {
        List<Project> projects = template.findAll(Project.class);
        if (!projects.isEmpty()) {
            projects.forEach(item -> {
                if (!item.validate()) {
                    log.error(item.projectNo + ":" + item.projectName + " validate failed");
                    Project project = new Project();
                    project.projectNo = "null";
                    projectMap.put(item.projectNo, project);
                } else {
                    projectMap.put(item.projectNo, item);
                }
            });
        }
        projectMap.forEach((key, value) -> {
            if (value.projectNo.equals("null")) {
                Project project = new Project();
                project.projectName = "default";
                ParkingConfig parkingConfig = new ParkingConfig();
                MatchStrategy matchStrategy = new MatchStrategy();
                matchStrategy.useDupMatch = false;
                matchStrategy.useRecogMatch = true;
                matchStrategy.useSubAreaStrictMatch = true;
                matchStrategy.subAreaStrategy = SubAreaStrategy.ENTER;
                parkingConfig.matchStrategy = matchStrategy;
                parkingConfig.minParkingSecond = 3;
                parkingConfig.minIntervalForDupRecog = 3;
                parkingConfig.txTTL = 5;
                project.parkingConfig = parkingConfig;
                projectMap.put(key, project);
            }
        });
    }

    @Override
    public synchronized void reloadByProjectNo(final String projectNo) {
        Project project = findByProjectNo(projectNo);
        log.info("+++++++++++++ 项目编号: " + projectNo + " 项目信息重新加载 +++++++++++++");
        reload(project);
    }

    public synchronized void reload(final Project project) {
        if (Objects.isNull(project)) {
            log.error("project is null");
            return;
        }
        if (project.validate()) {
            projectMap.put(project.projectNo, project);
        }
    }

    @Override
    public synchronized Project reload(final String projectNo) {
       Query query = new Query(Criteria.where("projectNo").is(projectNo));
       Project project = template.findOne(query, Project.class);
       if (Objects.nonNull(project)) {
           projectMap.put(project.projectNo, project);
           return projectMap.get(projectNo);
       }
       return null;
    }

    @Override
    public synchronized Project getProject(final String projectNo) {
        return Objects.isNull(projectMap.get(projectNo)) ? reload(projectNo) : projectMap.get(projectNo);
    }

    private Project findByProjectNo(String projectNo) {
        Query query = new Query(Criteria.where("projectNo").is(projectNo));
        return template.findOne(query, Project.class);
    }

    @Override
    public synchronized void run(String... args) {
        log.info("Projects init ...");
        reloadAll();
        log.info("Projects reloadAll finished: " + projectMap.toString());
    }
}
