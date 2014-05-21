package org.eclipse.om2m.commons.resource;

/**
 * Resources represents a collection of {@link Resource} objects.
 */

import java.util.ArrayList;
import java.util.List;

public class Resources {
    List<Resource> resources;

    /**
     * Gets the value of resources
     * @return list of the resources
     */
    public List<Resource> getResources() {
        if (resources == null) {
            resources = new ArrayList<Resource>();
        }
        return this.resources;
    }

    /**
     * Sets the value of resources
     * @param resources
     */
    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
