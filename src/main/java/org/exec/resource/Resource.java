package org.exec.resource;

import org.exec.ResourceManager;

public interface Resource {
    Double getQuantity();

    boolean isAvailable(ResourceManager resourceManager);

    void acquireResource(ResourceManager resourceManager);

    void release(ResourceManager resourceManager);

    String getName();
}
