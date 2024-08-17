package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Classname BpmnTemplateVo
 * @Description notice template vo
 * @since 0.5
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnTemplateVo implements Serializable {
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
     * event
     */
    private Integer event;
    private String eventValue;
    /**
     * who to inform
     */
    private String informs;
    private List<Long> informIdList;
    private List<BaseIdTranStruVo> informList;
    /**
     * specified employee
     */
    private String emps;
    private List<Long> empIdList;
    private List<BaseIdTranStruVo> empList;
    /**
     * specified roles
     */
    private String roles;
    private List<Long> roleIdList;
    private List<BaseIdTranStruVo> roleList;
    /**
     * specified functions
     */
    private String funcs;
    private List<Long> funcIdList;
    private List<BaseIdTranStruVo> funcList;
    /**
     * template id
     */
    private Long templateId;
    private String templateName;
    /**
     * 0 for not deleted, 1 for deleted
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
