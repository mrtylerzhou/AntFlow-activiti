package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname BpmnNodeConditionsConfBaseVo
 * @Description node conditions conf
 * @Date 2021-10-31 10:09
 * @since 0.5
 * @Created by AntOffice
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnNodeConditionsConfBaseVo {
    /**
     * condition param
     */
    private List<Integer> conditionParamTypes;

    /**
     * is default condition 0 for no 1 for yes
     */
    private Integer isDefault;

    /**
     * condtion priority
     */
    private Integer sort;

    /**
     * condition template mark
     */
    private List<BaseIdTranStruVo> templateMarksList;

    private BaseIdTranStruVo jobLevelVo;

    private Double parkArea;

    /**
     * 三方账号申请 业务流程 判断需要字段
     */
    private List<Integer> accountType;
    private List<BaseIdTranStruVo> accountTypeList;

    /**
     * 采购申请 业务流程 判断需要字段
     */
    private Double planProcurementTotalMoney;
    private List<Integer> purchaseType;
    private List<BaseIdTranStruVo> purchaseTypeList;

    /**
     * total money
     */
    private String totalMoney;

    /**
     * out total money
     */
    private String outTotalMoney;


    /**
     * 请假表单 业务流程 判断需要字段
     */
    private Double leaveHour;

    /**
     * total money operator
     */
    private Integer numberOperator;
    private String extJson;

    //===============>>third paryt process<<===================

    /**
     * 接入方配置条件Json字符串
     * condition json
     */
    private String outSideConditionsJson;

    /**
     * condition id
     */
    private String outSideConditionsId;

    /**
     * condition url
     */
    private String outSideConditionsUrl;

    /**
     * is third party condition matched
     */
    private Boolean outSideMatched;

    /**
     * template mark
     */
    private List<Integer> templateMarks;

}
