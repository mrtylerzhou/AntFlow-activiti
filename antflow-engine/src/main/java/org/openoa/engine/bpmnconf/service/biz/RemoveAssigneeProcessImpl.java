package org.openoa.engine.bpmnconf.service.biz;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.flowcontrol.MultiInstanceSignOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoveAssigneeProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private MultiInstanceSignOffService multiInstanceSignOffService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        String processNumber = vo.getProcessNumber();
        String taskDefKey = vo.getTaskDefKey();
        List<BaseIdTranStruVo> userInfos = vo.getUserInfos();
        if(userInfos==null){
            throw new JiMuBizException("请选择要减签的人员");
        }
        if(userInfos.size()>1){
            throw new JiMuBizException("减签只能选择一个人");
        }
        if(!StringUtils.isEmpty(processNumber)){
            throw new JiMuBizException("请指定流程编号");
        }
        if(StringUtils.isEmpty(taskDefKey)){
            throw new JiMuBizException("请指定审任务节点");
        }
        multiInstanceSignOffService.removeAssignee(processNumber,taskDefKey,userInfos.get(0).getId(),userInfos.get(0).getName());
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_REMOVE_ASSIGNEE);
    }
}
