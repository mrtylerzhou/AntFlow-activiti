package org.openoa.engine.lowflow.service;

import org.openoa.base.service.AntFlowOrderPostProcessor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.LFFieldControlVO;
import org.openoa.engine.bpmnconf.confentity.BpmnNodeLfFormdataFieldControl;
import org.openoa.engine.bpmnconf.service.impl.BpmnNodeLfFormdataFieldControlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class LFFieldControlPostProcessor implements AntFlowOrderPostProcessor<BpmnConfVo> {
    @Autowired
    private BpmnNodeLfFormdataFieldControlServiceImpl lfFormdataFieldControlService;

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void postProcess(BpmnConfVo confVo) {
        if(confVo==null){
            return;
        }
        Integer isLowCodeFlow = confVo.getIsLowCodeFlow();
        boolean lowCodeFlowFlag=isLowCodeFlow!=null&&isLowCodeFlow==1;
        if(!lowCodeFlowFlag){
            return;
        }
        List<BpmnNodeVo> bpmnNodeVos = confVo.getNodes();
        Long lfFormDataId = confVo.getLfFormDataId();
        List<BpmnNodeLfFormdataFieldControl> fieldControls=new ArrayList<>();
        for (BpmnNodeVo bpmnNodeVo : bpmnNodeVos) {
            List<LFFieldControlVO> lfFieldControlVOs = bpmnNodeVo.getLfFieldControlVOs();
            //todo lf if not specified,set it as default
            if(CollectionUtils.isEmpty(lfFieldControlVOs)){
                continue;
            }
            for (LFFieldControlVO lfFieldControlVO : lfFieldControlVOs) {
                BpmnNodeLfFormdataFieldControl fieldControl=new BpmnNodeLfFormdataFieldControl();
                fieldControl.setFormdataId(lfFormDataId);
                fieldControl.setNodeId(bpmnNodeVo.getId());
                fieldControl.setFieldName(lfFieldControlVO.getFieldName());
                fieldControl.setIsVisible(lfFieldControlVO.getIsVisible());
                fieldControl.setIsReadonly(lfFieldControlVO.getIsReadonly());
                fieldControl.setCreateUser(SecurityUtils.getLogInEmpName());
                fieldControls.add(fieldControl);
            }

        }
        lfFormdataFieldControlService.saveBatch(fieldControls);
    }

}
