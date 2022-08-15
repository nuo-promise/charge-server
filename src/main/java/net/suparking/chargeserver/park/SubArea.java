package net.suparking.chargeserver.park;

import net.suparking.chargeserver.ChargeServerApplication;
import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;

@Document(collection = "online_subarea")
public class SubArea extends FieldValidator {
    @Id
    public ObjectId id;
    @ParamNotNull
    public String areaName;
    @ParamNotNull
    public Integer totalParkingNumber;
    @ParamNotNull
    public LinkedList<CarQuantityLimitation> carQuantityLimitations;
    public Boolean doNotOpenOnParkFull = false;
    public Boolean strictOnShared = false;
    public Boolean defaultType = false;
    public String projectNo;
    public String creator;
    public Long createTime;
    public String modifier;
    public Long modifyTime;

    public static void reload(SubArea subArea) {
        subAreaRepository.reload(subArea);
    }

    public static void unloadById(String projectNo, ObjectId id) {
        subAreaRepository.unloadById(projectNo, id);
    }

    public static SubArea findById(String projectNo, ObjectId id) {
        return subAreaRepository.findById(projectNo, id);
    }

    public static boolean multiSubArea(final String projectNo) {
        return subAreaRepository.isMultiSubArea(projectNo);
    }

    private static SubAreaRepository subAreaRepository = ChargeServerApplication.getBean(
            "SubAreaRepositoryImpl", SubAreaRepository.class);

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        for (CarQuantityLimitation cql: carQuantityLimitations) {
            if (!cql.validate()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "SubArea{" + "id=" + id + ", areaName='" + areaName + '\'' + ", totalParkingNumber=" +
               totalParkingNumber + ", carQuantityLimitations=" + carQuantityLimitations + ", doNotOpenOnParkFull=" +
               doNotOpenOnParkFull + ", strictOnShared=" + strictOnShared + ", defaultType=" + defaultType +
               ", projectNo='" + projectNo + '\'' + ", creator='" + creator + '\'' + ", createTime=" + createTime +
               ", modifier='" + modifier + '\'' + ", modifyTime=" + modifyTime + "} " + super.toString();
    }
}
