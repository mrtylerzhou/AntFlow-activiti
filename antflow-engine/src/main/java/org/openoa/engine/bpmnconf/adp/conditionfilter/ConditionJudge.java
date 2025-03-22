package org.openoa.engine.bpmnconf.adp.conditionfilter;

import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.vo.BpmnStartConditionsVo;

/**
 * @author AntFlow
 * @since 0.5
 */
public interface ConditionJudge {

   boolean judge(String nodeId, BpmnNodeConditionsConfBaseVo conditionsConf, BpmnStartConditionsVo bpmnStartConditionsVo,int index);

}
