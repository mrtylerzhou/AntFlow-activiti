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
public class BpmnNodeConditionsPurchaseTypeAdp extends BpmnNodeConditionsAdaptor {
    @Override
    public void setConditionsResps(BpmnNodeConditionsConfBaseVo bpmnNodeConditionsConfBaseVo) {
       //todo for demo only
        List<BaseIdTranStruVo>vos=new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            BaseIdTranStruVo vo=new BaseIdTranStruVo();
            vo.setId(String.valueOf(i));
            String name= "";
            switch (i){
                case 0:name="台式机";
                break;
                case 1:name="笔记本";
                break;
                case 2:name="一体机";
            }
            vo.setName(name);
            vos.add(vo);
        }
        bpmnNodeConditionsConfBaseVo.setPurchaseTypeList(vos);
    }
}
