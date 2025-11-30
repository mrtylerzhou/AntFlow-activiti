package org.openoa.engine.bpmnconf.adp.bpmnnodeadp;

import org.openoa.base.interf.AdaptorService;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.PersonnelRuleVO;

/**
 * this abstract class is used to adapt bpmn node
 * @author AntFlow
 * @since 0.5
 */
public interface BpmnNodeAdaptor extends AdaptorService {

    /**
     * format BpmnNodeVo
     *
     * @param bpmnNodeVo
     * @return
     */
    void formatToBpmnNodeVo(BpmnNodeVo bpmnNodeVo);
    //此方法转时未用起来,用户实现时留空即可
    PersonnelRuleVO formaFieldAttributeInfoVO();
    /**
     * edit bpmn node info
     *
     * @param bpmnNodeVo
     */
    void editBpmnNode(BpmnNodeVo bpmnNodeVo);

}