package org.school;

import org.exec.resource.Resource;
import org.exec.task.Task;
import org.school.resource.*;

import java.util.List;

public record ProjectCreation(List<Resource> resourceList, List<Task> taskList) {

    public static ProjectCreation createTaskList() {
        MathTeacher mathTeacher = new MathTeacher();
        EnglishTeacher englishTeacher = new EnglishTeacher();

        List<Resource> resources = List.of(mathTeacher, englishTeacher, new Classroom(1.), new Paper(10));

        Task englishClass = new Task("English Class", 5);
        englishClass.addResource(new Classroom(1.));
        englishClass.addResource(new EnglishTeacher());
        englishClass.addResource(new Paper(1));

        Task mathClass = new Task("Math Class", 5);
        mathClass.addResource(new Classroom(1.));
        mathClass.addResource(new MathTeacher());
        mathClass.addResource(new Paper(1));

        Task replacementClass = new Task("Replacement Class", 5);
        replacementClass.addResource(new Classroom(1.));
        replacementClass.addResource(new Teacher());
        replacementClass.addResource(new Paper(1));
        replacementClass.addDependency(englishClass);
        replacementClass.addDependency(mathClass);

        List<Task> tasks = List.of(mathClass, englishClass, replacementClass);

        return new ProjectCreation(resources, tasks);
    }
}
