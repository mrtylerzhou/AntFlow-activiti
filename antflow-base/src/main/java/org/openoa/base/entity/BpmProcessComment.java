package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程沟通表.
 * 按流程实例(processNumber)一条会话, 支持二级回复(parentId/rootId 扁平存).
 */
@Data
@TableName("t_bpm_process_comment")
public class BpmProcessComment {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 流程实例编号(会话锚点) */
    @TableField("process_number")
    private String processNumber;

    /** 回复哪条消息(根消息为 null) */
    @TableField("parent_id")
    private Long parentId;

    /** 所属根消息 id(根=自身; 回复归到根, 二级分组用, 避免递归回溯) */
    @TableField("root_id")
    private Long rootId;

    /** 消息正文 */
    @TableField("content")
    private String content;

    /** 图片/附件 url JSON 数组(仅预留字段, v1 不做上传) */
    @TableField("attachment")
    private String attachment;

    /** @提及 JSON [{userId,userName}] */
    @TableField("mentions")
    private String mentions;

    /** 回复目标人 userId(回复消息时填) */
    @TableField("reply_to_user")
    private String replyToUser;

    /** 回复目标人姓名快照 */
    @TableField("reply_to_user_name")
    private String replyToUserName;

    /** 发起人 empId */
    @TableField("create_user")
    private String createUser;

    /** 发起人姓名快照 */
    @TableField("create_user_name")
    private String createUserName;

    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;

    /** 租户ID */
    @TableField("tenant_id")
    private String tenantId;

    /** 0 正常 1 已撤回 */
    @TableField("is_deleted")
    private Integer isDeleted;
}
