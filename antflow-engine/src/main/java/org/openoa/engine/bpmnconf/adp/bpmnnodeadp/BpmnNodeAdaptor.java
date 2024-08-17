package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import org.openoa.base.interf.AdaptorService;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.PersonnelRuleVO;

/**
 * this abstract class is used to adapt bpmn node
 * @author AntFlow
 * @since 0.5
 */
public abstract class BpmnNodeAdaptor implements AdaptorService {

    /**
     * format BpmnNodeVo
     *
     * @param bpmnNodeVo
     * @return
     */
    public abstract BpmnNodeVo formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo);
    public abstract PersonnelRuleVO formaFieldAttributeInfoVO();
    /**
     * edit bpmn node info
     *
     * @param bpmnNodeVo
     */
    public abstract void editBpmnNode(BpmnNodeVo bpmnNodeVo);

}