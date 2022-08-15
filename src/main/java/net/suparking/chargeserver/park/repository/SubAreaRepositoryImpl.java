package net.suparking.chargeserver.park.repository;

import net.suparking.chargeserver.park.SubArea;
import net.suparking.chargeserver.park.SubAreaRepository;
import net.suparking.chargeserver.repository.BasicRepositoryImpl;
import net.suparking.chargeserver.repository.Restart;
import org.bson.types.ObjectId;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Restart
@Order(110)
@Repository("SubAreaRepositoryImpl")
public class SubAreaRepositoryImpl extends BasicRepositoryImpl implements SubAreaRepository, CommandLineRunner {

    private final Map<String, List<SubArea>> subAreasMap = new ConcurrentHashMap<>(10);

    private static final Logger log = LoggerFactory.getLogger(SubAreaRepositoryImpl.class);

    @Autowired
    public SubAreaRepositoryImpl(@Qualifier("MongoTemplate")MongoTemplate template) {
        super(template);
    }

    @Override
    public synchronized void reloadAll() {
        List<SubArea> subAreas = template.findAll(SubArea.class);
        for (SubArea subArea: subAreas) {
            log.info(subArea.toString());
            reload(subArea);
        }
        log.info("SubAreaRepositoryImpl reloadAll finished :" + subAreasMap);
    }

    @Override
    public synchronized void reload(SubArea subArea) {
        if (subArea.validate()) {
            unloadById(subArea.projectNo, subArea.id);
            load(subArea);
        } else {
            log.error("SubArea " + subArea.id.toString() + " validate failed");
        }
    }

    @Override
    public synchronized void unloadById(String projectNo, ObjectId id) {
        List<SubArea> subAreas = subAreasMap.get(projectNo);
        if (Objects.nonNull(subAreas)) {
            subAreas.removeIf(subArea -> subArea.id.equals(id));
        }
    }

    @Override
    public synchronized SubArea findById(String projectNo, ObjectId id) {
        List<SubArea> subAreas = subAreasMap.get(projectNo);
        if (Objects.isNull(subAreas)) {
            return null;
        }
        for (SubArea subArea: subAreas) {
            if (subArea.id.equals(id)) {
                return subArea;
            }
        }
        SubArea subArea = template.findById(id, SubArea.class);
        if (Objects.nonNull(subArea)) {
            if (subArea.validate()) {
                unloadById(subArea.projectNo, subArea.id);
                load(subArea);
                return subArea;
            } else {
                log.error("SubArea " + subArea.id.toString() + " validate failed");
            }
        }
        log.warn("There is no subArea " + id);
        return null;
    }

    @Override
    public synchronized boolean isMultiSubArea(final String projectNo) {
        List<SubArea> subAreas = subAreasMap.get(projectNo);
        if (Objects.nonNull(subAreas)) {
            return subAreas.size() > 1;
        }
        return false;
    }

    @Override
    public int findTotalParkSpace(final String projectNo) {
        List<SubArea> subAreas = subAreasMap.get(projectNo);
        if (Objects.nonNull(subAreas) && subAreas.size() > 0) {
            int total = 0;
            for (SubArea subArea : subAreas) {
                total += subArea.totalParkingNumber;
            }
            return total;
        }
        return 0;
    }

    @Override
    public synchronized void reloadByProjectNo(final String projectNo) {
        List<SubArea> subAreas = findByProjectNo(projectNo);
        log.info("++++++++++++ 项目编号: " + projectNo + " 项目重新加载区域 +++++++++++++++++++");
        for (SubArea subArea: subAreas) {
            log.info(subArea.toString());
            reload(subArea);
        }
    }

    private List<SubArea> findByProjectNo(final String projectNo) {
        Query query = new Query(Criteria.where("projectNo").is(projectNo));
        return template.find(query, SubArea.class);
    }

    @Override
    public synchronized void run(String... arg) {
        log.info("SubArea init ...");
        reloadAll();
        log.info("SubArea init finished: " + subAreasMap);
    }

    private void load(SubArea subArea) {
        if (!subAreasMap.containsKey(subArea.projectNo)) {
            List<SubArea> subAreas = new ArrayList<SubArea>();
            subAreas.add(subArea);
            subAreasMap.put(subArea.projectNo, subAreas);
        } else {
            subAreasMap.get(subArea.projectNo).add(subArea);
        }
    }
}
