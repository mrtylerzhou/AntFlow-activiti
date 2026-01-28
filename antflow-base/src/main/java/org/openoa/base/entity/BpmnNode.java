package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.interf.TenantField;
import org.openoa.base.vo.BpmnNodeLabelVO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * bpmn node
 * @author TylerZhou
 * @since 0.0.1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_bpmn_node")
public class BpmnNode implements TenantField, Serializable {

    /**
     * auto incr id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * conf id
     */
    @TableField("conf_id")
    private Long confId;
    /**
     * node id
     */
    @TableField("node_id")
    private String nodeId;
    /**
     * node type{@link org.openoa.base.constant.enums.NodeTypeEnum}
     */
    @TableField("node_type")
    private Integer nodeType;
    /**
     * node property
     * @see org.openoa.base.constant.enums.NodePropertyEnum
     */
    @TableField("node_property")
    private Integer nodeProperty;
    /**
     * prev node
     */
    @TableField("node_from")
    private String nodeFrom;
    /**
     * can be batch agree 0 for no,1 for yes
     */
    @TableField("batch_status")
    private Integer batchStatus;
    /**
     * approval standard 1 startuser 2 approval
     */
    @TableField("approval_standard")
    private Integer approvalStandard;
    /**
     * node name
     */
    @TableField("node_name")
    private String nodeName;
    /**
     * node display name
     */
    @TableField("node_display_name")
    private String nodeDisplayName;
    /**
     * annotation
     */
    @TableField("annotation")
    private String annotation;
    /**
     * is deduplication 0 for no,1 for yes
     */
    @TableField("is_deduplication")
    private Integer isDeduplication;
    @TableField("deduplicationExclude")
    private boolean deduplicationExclude;
    /**
     * is node sign up 0 for no,1 for yes
     */
    @TableField("is_sign_up")
    private Integer isSignUp;
    @TableField("no_header_action")
    private Integer noHeaderAction;
    @TableField("extra_flags")
    private Integer extraFlags;
    /**
     * remark
     */
    private String remark;
    /**
     * 0 for normal,1 for delete
     */
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
    /**
     * create user
     */
    @TableField("create_user")
    private String createUser;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * update user
     */
    @TableField("update_user")
    private String updateUser;
    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

    @TableField("node_froms")
    private String nodeFroms;
    //0 for no and 1 for yes
    @TableField("is_dynamicCondition")
    private Boolean isDynamicCondition;
    //whether current node is a parallel node 0 for no and 1 for yes
    @TableField("is_parallel")
    private Boolean isParallel;



    @TableField(exist = false)
    private Integer isOutSideProcess;
    @TableField(exist = false)
    private Integer isLowCodeFlow;
    @TableField(exist = false)
    private Integer confExtraFlags;
    @TableField(exist = false)
    private List<BpmnNodeLabelVO> labelList;
}
