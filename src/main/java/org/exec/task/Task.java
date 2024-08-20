package org.exec.task;


import org.exec.ResourceManager;
import org.exec.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Task implements ObserverTaskCompleted {
    private final String name;
    private final int duration; // durée en sec
    private final List<Task> dependencies;
    private final List<Resource> resources;
    private final List<Task> dependenciesOver = new ArrayList<>();
    List<ObserverTaskCompleted> observerTaskCompleted = new ArrayList<>();
    List<ObserverTaskDependenciesOver> rootObserver = new ArrayList<>();
    private boolean isCompleted = false;


    public Task(String name, int duration) {
        this.name = name;
        this.duration = duration;
        this.dependencies = new ArrayList<>();
        this.resources = new ArrayList<>();
    }

    public void addObserverTaskCompleted(ObserverTaskCompleted task) {
        this.observerTaskCompleted.add(task);
    }

    public void addObserverTaskDependenciesOver(ObserverTaskDependenciesOver taskExecutor) {
        this.rootObserver.add(taskExecutor);
    }

    public void addDependency(Task dependency) {
        this.dependencies.add(dependency);
        dependency.addObserverTaskCompleted(this);
    }

    public void addResource(Resource resource) {
        this.resources.add(resource);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
        for (ObserverTaskCompleted o : observerTaskCompleted) {
            o.notifyFinishedTask(this);
        }
    }

    public int getDuration() {
        return duration;
    }


    public List<Task> getDependencies() {
        return dependencies;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        String resourceNames = resources.stream()
                .map(Resource::toString)
                .collect(Collectors.joining(", "));

        return "Task{name='" + name +
                ", resources=[" + resourceNames + "]}";
    }

    public void run(ResourceManager manager) throws InterruptedException {
        System.out.println("Executing task: " + this + " on thread: " + Thread.currentThread().getName());
        TimeUnit.MILLISECONDS.sleep(this.getDuration() * 100L);
        System.out.println("Completed task: " + this + " on thread: " + Thread.currentThread().getName());
        releaseResources(manager);
        this.setCompleted(true);
    }

    @Override
    public void notifyFinishedTask(Task t) {
        dependenciesOver.add(t);
        if (dependenciesOver.size() == dependencies.size()) {
            for (ObserverTaskDependenciesOver o : rootObserver) {
                o.notifyTaskDependenciesOver(this);
            }
        }
    }

    public boolean reserveResourcesAvailable(ResourceManager resourceManager) {
        return resourceManager.reserveResourceIfAvailable2(resources);
    }

    public void releaseResources(ResourceManager resourceManager) {
        for (Resource r : resources) {
            r.release(resourceManager);
        }
    }
}
