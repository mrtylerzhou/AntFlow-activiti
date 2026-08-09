package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Classname BpmnApproveRemindVo
 * @Description TODO
 * @since 0.5
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnApproveRemindVo implements Serializable {
    private Long id;
    /**
     * conf id
     */
    private Long confId;
    /**
     * node id
     */
    private Long nodeId;
    /**
     * is in use
     */
    private Boolean isInuse;
    /**
     * template id
     */
    private Long templateId;
    private String templateName;
    /**
     * reminder days after timeout (1~7)
     */
    private List<Integer> days;
    /**
     * node standard time limit in minutes
     */
    private Integer standardMinutes;
    /**
     * notice channel codes (MessageSendTypeEnum, incl. IN_SITE=4); empty means in-site only
     */
    private List<Integer> noticeTypes;

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
