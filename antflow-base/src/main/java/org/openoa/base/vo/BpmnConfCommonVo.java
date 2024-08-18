package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnConfCommonVo {

    /**
     * bpmnCode
     */
    private String bpmnCode;
    /**
     * formCode
     */
    private String formCode;

    /**
     * process's name
     */
    private String bpmnName;

    /**
     * processNumber
     */
    private String processNum;

    /**
     * process's name
     */
    private String processName;

    /**
     * process description
     */
    private String processDesc;

    /**
     * view page's buttons information
     */
    private BpmnConfViewPageButtonVo viewPageButtons;

    /**
     * process's elements list
     */
    private List<BpmnConfCommonElementVo> elementList;

    /**
     * out of process's notice tempalte
     */
    private List<BpmnTemplateVo> templateVos;

}
