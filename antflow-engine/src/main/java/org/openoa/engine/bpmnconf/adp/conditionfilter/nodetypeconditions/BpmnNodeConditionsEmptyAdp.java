package org.openoa.engine.bpmnconf.adp.conditionfilter.nodetypeconditions;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * this is a demo, you can delete it,you can also refer to it to write your onw ones
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class BpmnNodeConditionsEmptyAdp extends BpmnNodeConditionsAdaptor {
    @Override
    public void setConditionsResps(BpmnNodeConditionsConfBaseVo bpmnNodeConditionsConfBaseVo) {

    }
}
