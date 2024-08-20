package org.school.resource;

import org.exec.resource.ResourceAbstract;
import org.exec.resource.ResourcePerishable;

public class Paper extends ResourceAbstract implements ResourcePerishable {
    public Paper(double q) {
        quantity = q;
    }
}
