package org.exec;

import org.devWork.resource.*;
import org.exec.resource.Resource;
import org.exec.task.ObserverTaskCompleted;
import org.exec.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.school.resource.Classroom;
import org.school.resource.EnglishTeacher;
import org.school.resource.MathTeacher;
import org.school.resource.Paper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskExecutorTest implements ObserverTaskCompleted {

    private final List<Task> completedTasks = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        completedTasks.clear();
    }


    @Test
    public void test_with_one_task() {
        TaskExecutor taskExecutor = new TaskExecutor();
        Analyst analyste = new Analyst();
        List<Resource> resources = List.of(analyste);

        Task analyseBesoins = new Task("Analyse des besoins", 10);
        analyseBesoins.addResource(new Analyst());
        analyseBesoins.addObserverTaskCompleted(this);

        List<Task> tasks = List.of(analyseBesoins);

        taskExecutor.init(tasks, resources);

        assertTrue(analyseBesoins.isCompleted());
        assertEquals(1, completedTasks.size());
        assertEquals(analyseBesoins, completedTasks.getFirst());

    }

    @Test
    public void test_with_one_task_but_wrong_resource() {
        TaskExecutor taskExecutor = new TaskExecutor();

        List<Resource> resources = List.of(new Architect());

        Task analyseBesoins = new Task("Analyse des besoins", 5);
        analyseBesoins.addResource(new Analyst());
        analyseBesoins.addObserverTaskCompleted(this);

        List<Task> tasks = List.of(analyseBesoins);

        taskExecutor.init(tasks, resources);


        assertFalse(analyseBesoins.isCompleted());
        assertEquals(0, completedTasks.size());

    }

    @Test
    public void test_resources_are_freed() {
        TaskExecutor taskExecutor = new TaskExecutor();
        Analyst analyste = new Analyst();
        List<Resource> resources = List.of(analyste);

        Task analyseBesoins = new Task("Analyse des besoins", 8);
        analyseBesoins.addResource(new Analyst());
        analyseBesoins.addObserverTaskCompleted(this);


        Task analyseDeux = new Task("Analyse 2", 3);
        analyseDeux.addResource(new Analyst());
        analyseDeux.addObserverTaskCompleted(this);


        List<Task> tasks = List.of(analyseBesoins, analyseDeux);

        taskExecutor.init(tasks, resources);


        assertTrue(analyseBesoins.isCompleted());
        assertTrue(analyseDeux.isCompleted());
        assertEquals(2, completedTasks.size());
        assertEquals(analyseBesoins, completedTasks.getFirst());
        assertEquals(analyseDeux, completedTasks.getLast());
    }

    @Test
    public void test_with_two_task_in_parallel() {
        TaskExecutor taskExecutor = new TaskExecutor();

        Analyst analyste = new Analyst();
        Architect architecte = new Architect();
        List<Resource> resources = List.of(analyste, architecte);

        Task analyseBesoins = new Task("Analyse des besoins", 5);
        analyseBesoins.addResource(new Analyst());
        analyseBesoins.addObserverTaskCompleted(this);

        Task conceptionArchitecture = new Task("Conception de l'architecture", 7);
        conceptionArchitecture.addResource(new Architect());
        conceptionArchitecture.addObserverTaskCompleted(this);

        List<Task> tasks = List.of(analyseBesoins, conceptionArchitecture);

        taskExecutor.init(tasks, resources);

        assertTrue(analyseBesoins.isCompleted());
        assertTrue(conceptionArchitecture.isCompleted());
        assertEquals(2, completedTasks.size());
        assertEquals(analyseBesoins, completedTasks.getFirst());
        assertEquals(conceptionArchitecture, completedTasks.getLast());
    }

    //Second task needs the first to get started
    @Test
    public void test_with_two_task_with_dependencies() {
        TaskExecutor taskExecutor = new TaskExecutor();
        Analyst analyste = new Analyst();
        Architect architecte = new Architect();
        List<Resource> resources = List.of(analyste, architecte);

        Task analyseBesoins = new Task("Analyse des besoins", 5);
        analyseBesoins.addResource(new Analyst());
        analyseBesoins.addObserverTaskCompleted(this);


        Task conceptionArchitecture = new Task("Conception de l'architecture", 7);
        conceptionArchitecture.addDependency(analyseBesoins);
        conceptionArchitecture.addResource(new Architect());
        conceptionArchitecture.addObserverTaskCompleted(this);

        List<Task> tasks = List.of(analyseBesoins, conceptionArchitecture);

        taskExecutor.init(tasks, resources);


        assertTrue(analyseBesoins.isCompleted());
        assertTrue(conceptionArchitecture.isCompleted());
        assertEquals(2, completedTasks.size());
        assertEquals(analyseBesoins, completedTasks.getFirst());
        assertEquals(conceptionArchitecture, completedTasks.getLast());
    }

    @Test
    public void test_with_third_task_needs_two_task_done() {
        TaskExecutor taskExecutor = new TaskExecutor();

        Analyst analyste = new Analyst();
        Architect architecte = new Architect();
        Developer developpeur1 = new Developer();
        List<Resource> resources = List.of(analyste, architecte, developpeur1);

        Task analyseBesoins = new Task("Analyse des besoins", 5);
        analyseBesoins.addResource(new Analyst());
        analyseBesoins.addObserverTaskCompleted(this);

        Task conceptionArchitecture = new Task("Conception de l'architecture", 7);
        conceptionArchitecture.addResource(new Architect());
        conceptionArchitecture.addObserverTaskCompleted(this);

        Task miseEnPlaceEnvironnement = new Task("Mise en place de l'environnement de développement", 5);
        miseEnPlaceEnvironnement.addDependency(conceptionArchitecture);
        miseEnPlaceEnvironnement.addDependency(analyseBesoins);
        miseEnPlaceEnvironnement.addResource(new Developer());
        miseEnPlaceEnvironnement.addObserverTaskCompleted(this);

        List<Task> tasks = List.of(analyseBesoins, conceptionArchitecture, miseEnPlaceEnvironnement);

        taskExecutor.init(tasks, resources);

        assertTrue(analyseBesoins.isCompleted());
        assertTrue(conceptionArchitecture.isCompleted());
        assertTrue(miseEnPlaceEnvironnement.isCompleted());
        assertEquals(3, completedTasks.size());
        assertEquals(analyseBesoins, completedTasks.getFirst());
        assertEquals(conceptionArchitecture, completedTasks.get(1));
    }


    @Test
    public void test_with_two_task_that_needs_the_same_resource_with_engouh_resource() {
        TaskExecutor taskExecutor = new TaskExecutor();

        Analyst analyste = new Analyst();
        Analyst analyste2 = new Analyst();
        Architect architecte = new Architect();
        Developer developpeur1 = new Developer();
        List<Resource> resources = List.of(analyste, architecte, developpeur1, analyste2);

        Task analyseBesoins = new Task("Analyse des besoins", 5);
        analyseBesoins.addResource(new Analyst());
        analyseBesoins.addObserverTaskCompleted(this);

        Task analyseDeux = new Task("Analyse 2", 15);
        analyseDeux.addResource(new Analyst());
        analyseDeux.addObserverTaskCompleted(this);

        List<Task> tasks = List.of(analyseBesoins, analyseDeux);

        taskExecutor.init(tasks, resources);


        assertTrue(analyseBesoins.isCompleted());
        assertTrue(analyseDeux.isCompleted());
        assertEquals(2, completedTasks.size());
        assertEquals(analyseBesoins, completedTasks.getFirst());
        assertEquals(analyseDeux, completedTasks.getLast());
    }

    @Test
    public void test_with_devops_sub_resource() {
        TaskExecutor taskExecutor = new TaskExecutor();

        DevOps devOps = new DevOps();
        DevOps devOps2 = new DevOps();
        PcSpecialDevOps pcSpecialDevOps = new PcSpecialDevOps();
        List<Resource> resources = List.of(devOps, devOps2, pcSpecialDevOps);

        Task task = new Task("devops 1", 7);
        task.addResource(new DevOps());
        task.addObserverTaskCompleted(this);


        Task task2 = new Task("devops 2", 7);
        task2.addResource(new DevOps());
        task2.addObserverTaskCompleted(this);


        List<Task> tasks = List.of(task, task2);

        taskExecutor.init(tasks, resources);

        assertTrue(task.isCompleted());
        assertTrue(task2.isCompleted());
        assertEquals(2, completedTasks.size());
        assertEquals(task, completedTasks.getFirst());
        assertEquals(task2, completedTasks.getLast());
    }

    @Test
    public void test_with_perishable_resource() {
        TaskExecutor taskExecutor = new TaskExecutor();

        MathTeacher mathTeacher = new MathTeacher();
        EnglishTeacher englishTeacher = new EnglishTeacher();

        List<Resource> resources = List.of(mathTeacher, englishTeacher, new Classroom(1.), new Paper(1));

        Task englishClass = new Task("English Class", 5);
        englishClass.addResource(new Classroom(1.));
        englishClass.addResource(new EnglishTeacher());
        englishClass.addResource(new Paper(1));
        englishClass.addObserverTaskCompleted(this);

        Task mathClass = new Task("Math Class", 5);
        mathClass.addResource(new Classroom(1.));
        mathClass.addResource(new MathTeacher());
        mathClass.addResource(new Paper(1));
        mathClass.addDependency(englishClass);
        mathClass.addObserverTaskCompleted(this);

        List<Task> tasks = List.of(mathClass, englishClass);

        taskExecutor.init(tasks, resources);

        assertTrue(englishClass.isCompleted());
        assertFalse(mathClass.isCompleted());
        assertEquals(1, completedTasks.size());
        assertEquals(englishClass, completedTasks.getFirst());
    }


    @Test
    public void test_handles_empty_task_list_gracefully() {
        TaskExecutor taskExecutor = new TaskExecutor();
        List<Task> tasks = new ArrayList<>();
        List<Resource> resources = new ArrayList<>();

        taskExecutor.init(tasks, resources);

        assertTrue(taskExecutor.rootTasks.isEmpty());
    }

    @Test
    public void test_resource_acquire_normaly() {
        TaskExecutor taskExecutor = new TaskExecutor();

        Analyst analyste = new Analyst();
        Architect architecte = new Architect();
        Developer developpeur1 = new Developer();
        List<Resource> resources = List.of(analyste, architecte, developpeur1);

        Task analyseBesoins = new Task("Analyse des besoins", 5);
        analyseBesoins.addResource(new Analyst());
        analyseBesoins.addObserverTaskCompleted(this);

        Task analyseDeux = new Task("Analyse  + architect", 15);
        analyseDeux.addResource(new Architect());
        analyseDeux.addResource(new Analyst());
        analyseDeux.addObserverTaskCompleted(this);

        Task architect = new Task(" architect", 8);
        architect.addResource(new Architect());
        architect.addObserverTaskCompleted(this);

        List<Task> tasks = List.of(analyseBesoins, analyseDeux, architect);

        taskExecutor.init(tasks, resources);

        assertEquals(3, completedTasks.size());
        assertEquals(analyseBesoins, completedTasks.getFirst());
        assertEquals(architect, completedTasks.get(1));
    }

    @Override
    public void notifyFinishedTask(Task o) {
        completedTasks.add(o);
    }
}