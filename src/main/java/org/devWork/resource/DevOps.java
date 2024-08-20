package org.devWork.resource;

import org.exec.ResourceManager;
import org.exec.resource.HumanRessource;


public class DevOps extends HumanRessource {
    private final PcSpecialDevOps specialDevOps = new PcSpecialDevOps();

    @Override
    public boolean isAvailable(ResourceManager resourceManager) {
        return super.isAvailable(resourceManager) && specialDevOps.isAvailable(resourceManager);
    }

    @Override
    public void acquireResource(ResourceManager resourceManager) {
        super.acquireResource(resourceManager);
        specialDevOps.acquireResource(resourceManager);
    }

    @Override
    public void release(ResourceManager resourceManager) {
        super.release(resourceManager);
        specialDevOps.release(resourceManager);
    }
}
