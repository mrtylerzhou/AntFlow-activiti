package org.openoa.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * verify info vo
 * @author AntFlow
 * @since 0.0.1
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BpmVerifyInfoVo implements Serializable {

    private Long id;
    /**
     * process instance id
     */
    private String runInfoId;
    /**
     * verify user id
     */
    private String verifyUserId;
    private List<String>verifyUserIds;
    /**
     * verify user name
     */
    private String verifyUserName;


    /**
     * verify status
     */
    private Integer verifyStatus;
    /**
     * approver's comment
     */
    private String verifyDesc;
    /**
     * verify time
     */
    private Date verifyDate;
    /**
     * task name
     */
    private String taskName;
    /**
     * business type
     */
    private Integer businessType;
    /**
     * business id
     */
    private String businessId;
    private String verifyStatusName;
    /**
     * originalId approver
     */
    private String originalId;
    /**
     * original approver name
     */
    private String originalName;
    /**
     * process number
     */
    private String processCode;

    private List<String> processCodeList;

    /**
     * node element id
     */
    private String elementId;

    /**
     *sort
     */
    private Integer sort;
}
