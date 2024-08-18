package org.openoa.base.adp;

/**
 * used to set bean's order in case that you should process something in order
 */
public interface OrderedBean {
    Integer order();
}
