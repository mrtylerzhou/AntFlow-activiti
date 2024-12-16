package org.openoa.vo;

import lombok.Builder;
import lombok.Data;
import org.openoa.base.vo.BpmnStartConditionsVo;

@Data
public class BizLeaveTimeBpmnConditionsVo extends BpmnStartConditionsVo {
    private Double leaveHour;
}
