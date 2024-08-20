package org.devWork;


import org.exec.TaskExecutor;

public class Main {

    public static void main(String[] args) {
        ProjectCreation projet = ProjectCreation.createDevTask();

        TaskExecutor t = new TaskExecutor();
        t.init(projet.taskList(), projet.resourceList());
    }

}
