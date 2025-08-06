package org.activiti.engine.impl.cfg.multitenant;

public interface TenantIdHolder {

    /**
     * Sets the current tenant identifier.
     */
    void setCurrentTenantId(String tenantId);

    /**
     * Returns the current tenant identifier.
     */
    String getCurrentTenantId();

    /**
     * Clears the current tenant identifier settings.
     */
    void clearCurrentTenantId();
}
