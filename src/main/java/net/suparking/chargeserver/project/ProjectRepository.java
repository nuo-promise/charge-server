package net.suparking.chargeserver.project;

import java.util.List;

public interface ProjectRepository {
    void reloadAll();
    Project reload(String projectNo);
    Project getProject(String projectNo);

    void reloadByProjectNo(String projectNo);
}
