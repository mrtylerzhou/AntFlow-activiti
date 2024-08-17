package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author AntFlow
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmnConfViewPageButtonVo {

    /**
     * view page start button config
     */
    private List<BpmnConfCommonButtonPropertyVo> viewPageStart;

    /**
     * view page other button config
     */
    private List<BpmnConfCommonButtonPropertyVo> viewPageOther;

}
