package org.exec;


import org.exec.resource.Resource;
import org.exec.task.ObserverTaskDependenciesOver;
import org.exec.task.Task;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * This class is used to launch task, it will first start with every task that has no dependencies
 * and every time a task is done, it will tell all the other task that she is done so that they can
 * remove them from their dependencies, and so updating the 'rootTask' list in this class
 * (wich is the list of the task free of dependencies and just needs their resources to be
 * checked
 **/

public class TaskExecutor implements ObserverTaskDependenciesOver {
    ExecutorService executorService = Executors.newCachedThreadPool();

    ResourceManager resourceManager = new ResourceManager();

    CopyOnWriteArrayList<Task> rootTasks = new CopyOnWriteArrayList<>();


    public void init(List<Task> tasks, List<Resource> resources) {
        resourceManager.setAvailableResources(resources);
        rootTasks.addAll(checkDependency(tasks));

        //If there is no task with no dependencies, then we can't start
        if (rootTasks.isEmpty()) {
            System.out.println("Cannot exec project : no root Task were found");
            return;
        }
        resourceManager.displayCurrentResources();

        execRootTask();


        executorService.shutdown();
    }

    public void execRootTask() {
        try {
            CompletableFuture.allOf(
                    rootTasks.stream()
                            .map(task -> (Supplier<Boolean>) () -> executeTask(task))
                            .map(s -> CompletableFuture.supplyAsync(s, executorService).thenAcceptAsync(hasRun -> {
                                if (hasRun) {
                                    execRootTask();
                                }
                            }))
                            .toArray(CompletableFuture[]::new)).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean executeTask(Task task) {
        try {
            if (task.reserveResourcesAvailable(resourceManager)) {
                rootTasks.remove(task);
                task.run(resourceManager);
                return true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }


    /**
     * return all the task with no dependencies
     **/
    public CopyOnWriteArrayList<Task> checkDependency(List<Task> tasks) {
        CopyOnWriteArrayList<Task> rootTasks = new CopyOnWriteArrayList<>();
        for (Task task : tasks) {
            task.addObserverTaskDependenciesOver(this);
            if (task.getDependencies().isEmpty()) {
                rootTasks.add(task);
            }
        }
        return rootTasks;
    }


    /**
     * receive a notification that a task is now free of dependencies
     **/
    @Override
    public void notifyTaskDependenciesOver(Task o) {
        rootTasks.add(o);
    }
}
