package org.openoa.base.entity;

import lombok.Data;


@Data
public class AFExecutionEntity {
    /**
     * id,executionId
     */
    private String id;
    /**
     * 版本号，默认1
     */
    private Integer revision;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 业务表主键 业务key
     */
    private String businessKey;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 当前流程所在的节点ID
     */
    private String activityId;
    /**
     * 是否处于激活状态（0否，1是） 一般为1
     */
    private Boolean isActive;
    /**
     * 是否处于并发状态（0否，1是） 一般为0
     */
    private Boolean isConcurrent;
    /**
     * 是否是主流程实例（0否，1是）一般为1
     */
    private Boolean isScope;
    /**
     * 是否是事件（0否，1是） 一般为0
     */
    private Boolean isEventScope;
    /**
     * 父id
     */
    private String parentId;
    /**
     * 父运行时流程实例id
     */
    private String superExecutionId;
    /**
     * 挂起状态（1正常,2挂起）
     */
    private Integer suspensionState;
    /**
     * 流程实体的缓冲，取值为0~7，无法确定 可设置为2
     */
    private Integer cachedEntityState;
    /**
     * 租户id
     */
    private Integer tenantId;
    /**
     * 名称
     */
    private String name;

    /**
     * 克隆当前对象
     * @return 克隆后的新对象
     */
    public AFExecutionEntity clone() {
        AFExecutionEntity clone = new AFExecutionEntity();
        clone.setId(this.id);
        clone.setRevision(this.revision);
        clone.setProcessInstanceId(this.processInstanceId);
        clone.setBusinessKey(this.businessKey);
        clone.setProcessDefinitionId(this.processDefinitionId);
        clone.setActivityId(this.activityId);
        clone.setIsActive(this.isActive);
        clone.setIsConcurrent(this.isConcurrent);
        clone.setIsScope(this.isScope);
        clone.setIsEventScope(this.isEventScope);
        clone.setParentId(this.parentId);
        clone.setSuperExecutionId(this.superExecutionId);
        clone.setSuspensionState(this.suspensionState);
        clone.setCachedEntityState(this.cachedEntityState);
        clone.setTenantId(this.tenantId);
        clone.setName(this.name);
        return clone;
    }
}

