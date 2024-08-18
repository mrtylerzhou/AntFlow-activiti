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
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_flowruninfo")
public class BpmFlowruninfo {


    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * process instance id
     */
    private Long runinfoid;
    /**
     * create user id
     */
    @TableField("create_UserId")
    private Long createUserId;
    /**
     * entity key
     */
    private String entitykey;
    /**
     * entity class
     */
    private String entityclass;
    /**
     * entity type
     */
    private String entitykeytype;
    /**
     * created by
     */
    private String createactor;
    /**
     * creator department
     */
    private String createdepart;
    /**
     * create date
     */
    private Date createdate;

}