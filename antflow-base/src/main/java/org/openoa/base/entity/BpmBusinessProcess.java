package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * this is the main entity for join bpmn and business
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_business_process")
public class BpmBusinessProcess {

    /**
     * process version 0 for old and 1 for new
     */
    public static final Integer VERSION_DEFAULT_0 = 0;
    public static final Integer VERSION_1 = 1;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process key
     */
    @TableField("PROCESSINESS_KEY")
    private String processinessKey;
    /**
     * business Id
     */
    @TableField("BUSINESS_ID")
    private String businessId;
    /**
     * business Number
     */
    @TableField("BUSINESS_NUMBER")
    private String businessNumber;
    /**
     * entry id
     */
    @TableField("ENTRY_ID")
    private String entryId;
    /**
     * process version
     */
    @TableField("VERSION")
    private Integer version;
    /**
     * as its name says
     */
    @TableField("CREATE_TIME")
    private Date createTime;
    /**
     * as its name says
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;
    /**
     * process description
     */
    @TableField("description")
    private String description;
    /**
     * process state 1:approved 2:approving 3:canceled
     */
    @TableField("process_state")
    private Integer processState;

    /**
     * as its name says
     */
    @TableField("create_user")
    private String createUser;
    /**
     * process digest
     */
    @TableField("process_digest")
    private String processDigest;

    /**
     * is del 0 for no and 1 for yes
     */
    @TableField("is_del")
    private Integer isDel;

    /**
     * has no meaning at the moment
     */
    @TableField("data_source_id")
    private Long dataSourceId;

    /**
     * process instance id,business id and process id are two important fields for connecting your business data with activiti process
     */
    @TableField("PROC_INST_ID_")
    private String procInstId;

    /**
     * back to user id
     */
    @TableField("back_user_id")
    private String backUserId;

}