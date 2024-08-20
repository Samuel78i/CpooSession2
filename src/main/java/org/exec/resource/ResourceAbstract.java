package org.exec.resource;

import org.exec.ResourceManager;

public abstract class ResourceAbstract implements Resource {
    protected Double quantity;

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public Double getQuantity() {
        return quantity;
    }

    public boolean isAvailable(ResourceManager resourceManager) {
        return resourceManager.reserveResourceIfAvailable(this);
    }

    public void acquireResource(ResourceManager resourceManager) {
        resourceManager.acquireResource(this);
    }

    public void release(ResourceManager resourceManager) {
        resourceManager.releasedResource(this);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
