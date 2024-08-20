package org.school;


import org.exec.TaskExecutor;

public class Main {
    public static void main(String[] args) {
        ProjectCreation projet = ProjectCreation.createTaskList();

        TaskExecutor t = new TaskExecutor();
        t.init(projet.taskList(), projet.resourceList());
    }
}
