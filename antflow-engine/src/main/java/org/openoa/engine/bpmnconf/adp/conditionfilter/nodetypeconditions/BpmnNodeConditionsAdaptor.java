package org.openoa.engine.bpmnconf.adp.conditionfilter.nodetypeconditions;

import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;

/**
 * used to set response condition
 * @author AntFlow
 * @since 0.5
 */
public abstract class BpmnNodeConditionsAdaptor {

    /**
     * set resp params
     *
     * @param bpmnNodeConditionsConfBaseVo
     */
    public abstract void setConditionsResps(BpmnNodeConditionsConfBaseVo bpmnNodeConditionsConfBaseVo);

}
