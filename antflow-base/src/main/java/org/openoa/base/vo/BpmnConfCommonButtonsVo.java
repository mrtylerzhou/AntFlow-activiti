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
public class BpmnConfCommonButtonsVo {

    /**
     * start page buttons
     */
    private List<BpmnConfCommonButtonPropertyVo> startPage;

    /**
     * approval page buttons
     */
    private List<BpmnConfCommonButtonPropertyVo> approvalPage;

    /**
     * view page buttons
     */
    private List<BpmnConfCommonButtonPropertyVo> viewPage;

}
