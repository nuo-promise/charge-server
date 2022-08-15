package net.suparking.chargeserver.car;

import org.bson.types.ObjectId;

import java.util.List;

public interface ProtocolRepository {
    void reloadAll();
    void reload(Protocol protocol);
    void unloadById(String projectNo, ObjectId id);
    Protocol findById(String projectNo, ObjectId id);
    List<Protocol> findAll(final String projectNo);

    void reloadByProjectNo(String projectNo);
}
