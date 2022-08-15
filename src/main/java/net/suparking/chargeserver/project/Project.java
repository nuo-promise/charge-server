package net.suparking.chargeserver.project;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "online_project")
public class Project extends FieldValidator {
    @Id
    public ObjectId id;
    public String projectNo;
    public String projectName;

    public String addressSelect;

    public String projectId;
    public String helpline;
    @ParamNotNull
    public ParkingConfig parkingConfig;

    private List<String> openTime;

    private Location location;

    private List<Double> locations = new ArrayList<Double>();

    private Integer freePark;

    public String remark;

    public String chargeContent;

    public String perCharge;

    private String orgNo;

    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    private static ProjectRepository projectRepository = ChargeServerApplication.getBean(
            "ProjectRepositoryImpl", ProjectRepository.class);


    public static void reloadAll() {
        projectRepository.reloadAll();
    }

    public static Project getProject(final String projectNo) {
        return projectRepository.getProject(projectNo);
    }

    public static ParkingConfig getParkingConfig(final String projectNo) {
        return projectRepository.getProject(projectNo).parkingConfig;
    }

    public static void reload(Project project) {
        projectRepository.reload(project.projectNo);
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        return parkingConfig.validate();
    }

    @Override
    public String toString() {
        return "Project{" + "id=" + id + ", projectNo='" + projectNo + '\'' + ", projectName='" + projectName + '\'' +
               ", address='" + addressSelect + '\'' + ", helpline='" + helpline + '\'' + ", parkingConfig=" + parkingConfig +
                ",openTime='" + openTime + '\'' + ",location='" + location + '\'' + ",locations='" + locations + '\'' +
                ",freePark='" + freePark + '\'' + ",chargeContent='" + chargeContent + '\'' + ",perCharge='" + perCharge + '\'' +
                ",orgNo='" + orgNo + '\'' + ", remark='" + remark + '\'' + ", creator='" + creator + '\'' +
                ", createTime=" + createTime + ", modifier='" + modifier + '\'' + ", modifyTime=" + modifyTime + '}';
    }
}
