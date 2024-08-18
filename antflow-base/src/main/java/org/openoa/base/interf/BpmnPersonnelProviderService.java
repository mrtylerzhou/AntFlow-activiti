package org.openoa.base.interf;

import org.openoa.base.vo.BpmnNodeParamsAssigneeVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

import java.util.List;

/**
 * used to provide methods to query approvers
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-04-30 19:59
 * @Param
 * @return
 * @Version 1.0
 */
public interface BpmnPersonnelProviderService {
    /**
     * 单人的也返回集合,取的时候再做处理
     * @param bpmnNodeVo
     * @return
     */
    public List<BpmnNodeParamsAssigneeVo> getAssigneeList(BpmnNodeVo bpmnNodeVo, BpmnStartConditionsVo startConditionsVo);
}
