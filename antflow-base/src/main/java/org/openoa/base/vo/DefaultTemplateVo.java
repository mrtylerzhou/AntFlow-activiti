package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author TylerZhou
 * @since 0.5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefaultTemplateVo implements Serializable {

    private Long id;
    /**
     * event
     */
    private Integer event;
    private String eventValue;
    /**
     * template id
     */
    private Long templateId;
    private String templateName;
    /**
     * 0 for normal, 1 for deleted
     */
    private Integer isDel;
    /**
     * create time
     */
    private Date createTime;
    /**
     * create user
     */
    private String createUser;
    /**
     * update time
     */
    private Date updateTime;
    /**
     * update user
     */
    private String updateUser;


}
