package org.openoa.engine.bpmnconf.adp.processoperation;

import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessDraftBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveDraftImpl implements ProcessOperationAdaptor {
    @Autowired
    private BpmProcessDraftBizService bpmProcessDraftBizService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        bpmProcessDraftBizService.saveBusinessDraft(vo);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_SAVE_DRAFT);
    }
}
