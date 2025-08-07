package org.openoa.engine.bpmnconf.adp.conditionfilter.nodetypeconditions;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.vo.BpmnNodeConditionsConfBaseVo;
import org.openoa.base.entity.OutSideBpmConditionsTemplate;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmConditionsTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * used for third party process service template mark judge
 * @author AntFlow
 * @since 0.5
 */
@Slf4j
@Component
public class BpmnTemplateMarkAdp extends BpmnNodeConditionsAdaptor {

    @Autowired
    private OutSideBpmConditionsTemplateServiceImpl outSideBpmConditionsTemplateService;

    @Override
    public void setConditionsResps(BpmnNodeConditionsConfBaseVo bpmnNodeConditionsConfBaseVo) {
        if (!CollectionUtils.isEmpty(bpmnNodeConditionsConfBaseVo.getTemplateMarks())) {
            List<OutSideBpmConditionsTemplate> outSideBpmConditionsTemplates = outSideBpmConditionsTemplateService.getBaseMapper().selectBatchIds(bpmnNodeConditionsConfBaseVo.getTemplateMarks());
            if (!CollectionUtils.isEmpty(outSideBpmConditionsTemplates)) {
                List<Integer> collect = outSideBpmConditionsTemplates.stream().map(o -> o.getId().intValue()).collect(Collectors.toList());
                bpmnNodeConditionsConfBaseVo.setTemplateMarks(collect);
            }
        }
    }
}
