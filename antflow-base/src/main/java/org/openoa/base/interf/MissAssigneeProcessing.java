package org.openoa.base.interf;

import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;

import java.util.List;

public interface MissAssigneeProcessing {
    BaseIdTranStruVo processMissAssignee(Integer processingWay);
}
