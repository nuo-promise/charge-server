package net.suparking.chargeserver.car.repository;

import net.suparking.chargeserver.car.Protocol;
import net.suparking.chargeserver.car.ProtocolRepository;
import net.suparking.chargeserver.repository.BasicRepositoryImpl;
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

@Order(180)
@Repository("ProtocolRepositoryImpl")
public class ProtocolRepositoryImpl extends BasicRepositoryImpl implements ProtocolRepository, CommandLineRunner {

    private final Map<String, List<Protocol>> protocolMap = new ConcurrentHashMap<String, List<Protocol>>(10);

    private static final Logger log = LoggerFactory.getLogger(ProtocolRepositoryImpl.class);

    @Autowired
    public ProtocolRepositoryImpl(@Qualifier("MongoTemplate")MongoTemplate template) {
        super(template);
    }

    @Override
    public synchronized void reloadAll() {
        List<Protocol> protocols = template.findAll(Protocol.class);
        for (Protocol protocol: protocols) {
            log.info(protocol.toString());
            reload(protocol);
        }
    }

    @Override
    public synchronized void reload(Protocol protocol) {
        if (protocol.validate()) {
            unloadById(protocol.projectNo, protocol.id);
            load(protocol);
        } else {
            log.error("Protocol " + protocol.id.toString() + " validate failed");
        }
    }

    @Override
    public synchronized void unloadById(String projectNo, ObjectId id) {
        List<Protocol> protocols = protocolMap.get(projectNo);
        if (Objects.nonNull(protocols)) {
            protocols.removeIf(protocol -> protocol.id.equals(id));
        }
    }

    @Override
    public synchronized Protocol findById(String projectNo, ObjectId id) {
        List<Protocol> protocols = protocolMap.get(projectNo);
        if (Objects.nonNull(protocols)) {
            for (Protocol protocol: protocols) {
                if (protocol.id.equals(id)) {
                    return protocol;
                }
            }
        }

        Protocol protocol = template.findById(id, Protocol.class);
        if (protocol != null) {
            if (protocol.validate()) {
                load(protocol);
            } else {
                log.error("Protocol " + protocol.id.toString() + " validate failed");
                log.warn("There is no Protocol entity for id " + id);
                return null;
            }
        }
        return protocol;
    }

    @Override
    public synchronized List<Protocol> findAll(final String projectNo) {
        reloadAll();
        return protocolMap.get(projectNo);
    }

    @Override
    public synchronized void reloadByProjectNo(String projectNo) {
        List<Protocol> protocols = findByProjectNo(projectNo);
        log.info("+++++++++++++ 项目编号: " + projectNo + " 协议重新加载: ++++++++++++++++");
        for (Protocol protocol: protocols) {
            if (protocol.projectNo.equals(projectNo)) {
                reload(protocol);
            }
        }
    }

    private List<Protocol> findByProjectNo(String projectNo) {
        Query query = new Query(Criteria.where("projectNo").is(projectNo));
        return template.find(query, Protocol.class);
    }

    @Override
    public synchronized void run(String... arg) {
        log.info("Protocol init ...");
        reloadAll();
        log.info("Protocol init finished: " + protocolMap);
    }

    private void load(Protocol protocol) {
        if (!protocolMap.containsKey(protocol.projectNo)) {
           List<Protocol> newProtocols = new ArrayList<>(1);
           newProtocols.add(protocol);
           protocolMap.put(protocol.projectNo, newProtocols);
        } else {
           protocolMap.get(protocol.projectNo).add(protocol);
        }
    }
}
