package org.eclipse.om2m.commons.resource;

/**
 *The Content attribute is the real opaque content of an instance. This may for
 * example be an image taken by a security camera, or a temperature measurement
 * taken by a temperature sensor.
 *
 */

public class Content extends Resource {

    private Base64Binary value;

    /**
     * Gets the value
     * @return the Base64Binary value
     */
    public Base64Binary getValue() {
        return value;
    }

    /**
     * Sets the value
     * @param the value to set
     */
    public void setValue(Base64Binary value) {
        this.value = value;
    }

    public String toString() {
        return "Content [value=" + value + "]";
    }

}
