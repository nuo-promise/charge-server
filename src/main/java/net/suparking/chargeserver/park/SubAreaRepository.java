package net.suparking.chargeserver.park;

import org.bson.types.ObjectId;

public interface SubAreaRepository {
    void reloadAll();
    void reload(SubArea subArea);
    void unloadById(String projectNo, ObjectId id);
    SubArea findById(String projectNo, ObjectId id);
    boolean isMultiSubArea(String projectNo);
    int findTotalParkSpace(String projectNo);

    void reloadByProjectNo(String projectNo);
}
