package org.exec;


import org.exec.resource.Resource;
import org.exec.resource.ResourcePerishable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ResourceManager {
    private final ReentrantLock lock = new ReentrantLock();
    protected List<Resource> initialResources = new ArrayList<>();
    protected Map<Resource, Double> actualResourceQuantity = new HashMap<>();
    protected Map<Resource, Map<Resource, Double>> occupiedResourcesPerResourceFromTask = new HashMap<>();

    public void setAvailableResources(List<Resource> availableResources) {
        this.initialResources.clear();
        this.initialResources.addAll(availableResources);
        for (Resource r : availableResources) {
            this.actualResourceQuantity.put(r, r.getQuantity());
        }
    }

    /**
     * return true if the resources can be acquired and acquire them
     **/
    public boolean reserveResourceIfAvailable(Resource resource) {
        float quantity = 0;
        for (Resource actualResource : actualResourceQuantity.keySet()) {
            if (resource.getClass().isAssignableFrom(actualResource.getClass())) {
                if (actualResourceQuantity.get(actualResource) >= resource.getQuantity() || quantity + actualResourceQuantity.get(actualResource) >= resource.getQuantity()) {
                    //acquireResource(resource);
                    return true;
                } else {
                    quantity += actualResourceQuantity.get(actualResource);
                }
            }
        }
        return false;

    }

    public void acquireResource(Resource resource) {
        Double quantityToAcquire = resource.getQuantity();
        for (Resource availableResource : actualResourceQuantity.keySet()) {
            if (resource.getClass().isAssignableFrom(availableResource.getClass())) {
                if (quantityToAcquire > resource.getQuantity()) {
                    actualResourceQuantity.put(availableResource, 0.);
                    if (!occupiedResourcesPerResourceFromTask.containsKey(resource)) {
                        occupiedResourcesPerResourceFromTask.put(resource, new HashMap<>());
                    }
                    occupiedResourcesPerResourceFromTask.get(resource).put(availableResource, availableResource.getQuantity());
                    quantityToAcquire -= availableResource.getQuantity();
                } else {
                    actualResourceQuantity.put(availableResource, availableResource.getQuantity() - resource.getQuantity());
                    if (!occupiedResourcesPerResourceFromTask.containsKey(resource)) {
                        occupiedResourcesPerResourceFromTask.put(resource, new HashMap<>());
                    }
                    occupiedResourcesPerResourceFromTask.get(resource).put(availableResource, resource.getQuantity());
                    break;
                }
            }
        }

    }

    public void releasedResource(Resource resource) {
        boolean isLockAcquired;
        try {
            isLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (isLockAcquired) {
            try {
                Map<Resource, Double> reservedResources = occupiedResourcesPerResourceFromTask.get(resource);
                for (Resource reservedResource : reservedResources.keySet()) {
                    if (resource instanceof ResourcePerishable) {
                        actualResourceQuantity.put(reservedResource, actualResourceQuantity.get(reservedResource));
                    } else {
                        actualResourceQuantity.put(reservedResource, actualResourceQuantity.get(reservedResource)
                                + reservedResources.get(reservedResource));
                    }
                }
                occupiedResourcesPerResourceFromTask.remove(resource);
            } finally {
                lock.unlock();
            }
        }
    }

    public void displayCurrentResources() {
        actualResourceQuantity.forEach((resource, quantity) -> System.out.print(resource.getClass().getSimpleName() + ": " + quantity + " ; "));
        System.out.println();
    }

    public boolean reserveResourceIfAvailable2(List<Resource> resources) {
        boolean isLockAcquired;
        try {
            isLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (isLockAcquired) {
            try {
                boolean isAllResourcesAvailable = true;
                for (Resource resource : resources) {
                    if (!resource.isAvailable(this)) {
                        isAllResourcesAvailable = false;
                    }
                }
                if (isAllResourcesAvailable) {
                    resources.forEach(resource -> resource.acquireResource(this));
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }
}