package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Classname BpmnNodePropertysVo
 * @Description node property
 * @Date 2021-10-31 10:08
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnNodePropertysVo implements Serializable{
    /**
     * loop end type
     */
    private Integer loopEndType;

    /**
     * loop levels
     */
    private Integer loopNumberPlies;

    /**
     * loop end level
     */
    private Integer loopEndGrade;

    /**
     * loop end person list
     */
    private List<Serializable> loopEndPersonList;

    /**
     * loop end person obj list
     */
    private List<BaseIdTranStruVo> loopEndPersonObjList;


    private Integer assignLevelType;

    /**
     * assigned level
     */
    private Integer assignLevelGrade;

    /**
     * 1 hrbp 2 hrbp leader
     */
    private Integer hrbpConfType;

    /**
     * role id
     */
    private List<String> roleIds;

    /**
     * role list
     */
    private List<BaseIdTranStruVo> roleList;

    /**
     * assigned emp
     */
    private List<String> emplIds;

    /**
     * assigned emp list
     */
    private List<BaseIdTranStruVo> emplList;

    /**
     * sign type 1 for all sign 2 for or sign
     */
    private Integer signType;

    /**
     * condition conf vo
     */
    private BpmnNodeConditionsConfBaseVo conditionsConf;
    private List<List<BpmnNodeConditionsConfVueVo>> conditionList;
    /**
     * config table type
     */
    private Integer configurationTableType;

    /**
     * config table field
     */
    private Integer tableFieldType;

    /**
     * is multi assignee 1 for multi null for single
     */
    private Integer isMultiPeople;

    /**
     * loop sign not participating staff
     */
    private List<Serializable> noparticipatingStaffIds;

    /**
     * loop sign not participating staff list
     */
    private List<BaseIdTranStruVo> noparticipatingStaffs;

    /**
     * function id
     */
    private Long functionId;

    /**
     * function name
     */
    private String functionName;

    /**
     * after sign up way 1 back to sign up person 2 not back to sign up person
     */
    private Integer afterSignUpWay;

    /**
     * sign up way 1 for sequence 2 for no sequence 3 for or
     */
    private Integer signUpType;

    /**
     * node mark
     */
    private String nodeMark;

    /**
     * is default condition 0 for no and 1 for yes
     */
    private Integer isDefault;

    /**
     * condition's priority
     */
    private Integer sort;
}
