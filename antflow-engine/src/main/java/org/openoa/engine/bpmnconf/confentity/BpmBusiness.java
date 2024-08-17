package org.openoa.engine.bpmnconf.confentity;

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
 * @author AntFlow
 * @since 0.0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_business")
public class BpmBusiness {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * business id
     */
    @TableField("business_id")
    private Long businessId;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * process number
     */
    @TableField("process_code")
    private String processCode;
    /**
     * create user
     */
    @TableField("create_user_name")
    private String createUserName;

    /**
     * create user id
     */
    @TableField("create_user")
    private Long createUser;
    /**
     * process key
     */
    @TableField("process_key")
    private String processKey;

    @TableField("is_del")
    public Integer isDel;
}