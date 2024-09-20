package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.vo.UrlParams;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_message")
public class UserMessage {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * user id
     */
    @TableField("user_id")
    private String userId;
    /**
     * title
     */
    private String title;
    /**
     * content
     */
    private String content;
    /**
     * send url
     */
    private String url;

    /**
     * node id
     */
    private String node;

    /**
     * 发送类型
     */
    private String params;

    /**
     * 发送变量
     */
    @TableField(exist = false)
    private UrlParams urlParams;

    /**
     * 0为未读 1为已读
     * 0 unread 1 already read
     */
    @TableField("is_read")
    private Boolean isRead;
    /**
     * 0 for not deleted 1 for deleted
     */
    @TableField("is_del")
    private Boolean del;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("create_user")
    private String createUser;
    @TableField("update_user")
    private String updateUser;
    @TableField("app_url")
    private String appUrl;

    /**
     * message source
     */
    @TableField("source")
    private Integer source;
}