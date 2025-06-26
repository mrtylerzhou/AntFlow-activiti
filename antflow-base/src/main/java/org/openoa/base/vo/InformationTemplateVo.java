package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author AntFlow
 * @since 0.0.5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationTemplateVo implements Serializable {

    private Long id;
    /**
     * name
     */
    private String name;
    /**
     * number
     */
    private String num;
    /**
     * system title
     */
    private String systemTitle;
    /**
     * content
     */
    private String systemContent;
    /**
     * email title
     */
    private String mailTitle;
    /**
     * email content
     */
    private String mailContent;
    /**
     * sms content
     */
    private String noteContent;
    /**

     * jump url 1、process approval page2、process detial page3 process todo page
     */
    private Integer jumpUrl;
    private String jumpUrlValue;
    /**
     * remark
     */
    private String remark;
    /**
     * status 0 for enabled 1 for disabled
     */
    private Integer status;
    private String statusValue;
    private Integer event;
    private String eventName;
    /**
     * 0 for normal 1 for deleted
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

    /**
     * map key:WildcardCharacterEnum code value:
     * {@link org.openoa.base.constant.enums.WildcardCharacterEnum}
     */
    private Map<Integer, String> wildcardCharacterMap;

}
