package org.openoa.engine.bpmnconf.service.biz;

import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BaseInfoTranStructVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.common.TaskMgmtServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmFlowrunEntrustServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.stream.Collectors;

@Component
public class ChangeFutrueAssigneeProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private TaskMgmtServiceImpl taskMgmtService;
    @Autowired
    private BpmFlowrunEntrustServiceImpl flowrunEntrustService;

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        String processNumber = vo.getProcessNumber();
        String nodeId = vo.getNodeId();
        List<BaseIdTranStruVo> userInfos = vo.getUserInfos();
        if(StringUtils.isEmpty(processNumber)){
            throw new JiMuBizException("流程编号不能为空");
        }
        if(StringUtils.isEmpty(nodeId)){
            throw new JiMuBizException("节点id不能为空");
        }
        if(CollectionUtils.isEmpty(userInfos)){
            throw new JiMuBizException("审批人不能为空");
        }
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(processNumber);
        if(null==bpmBusinessProcess){
            throw new JiMuBizException("未能根据流程编号找到流程信息:"+processNumber);
        }
        List<BaseInfoTranStructVo> assignees = bpmVariableMultiplayerMapper.getAssigneeAndVariableByNodeId(processNumber, nodeId);
        if(userInfos.size()!=assignees.size()){
            throw new JiMuBizException("审批人数量和流程中审批人数量不一致");
        }
        Map<BaseIdTranStruVo,BaseIdTranStruVo> changedAssignees=new HashMap<>();
        for (int i = 0; i < assignees.size(); i++) {
            BaseInfoTranStructVo currentAssignee = assignees.get(i);
            BaseIdTranStruVo mayChangedAssignee = userInfos.get(i);
            List<BaseIdTranStruVo> sameAssignees = userInfos.stream().filter(a -> currentAssignee.getId().equals(a.getId())).collect(Collectors.toList());
            //未能获取到前端传入的和当前审批人相同的人员,说明发生了变更
            if(CollectionUtils.isEmpty(sameAssignees)){
                changedAssignees.put(currentAssignee,mayChangedAssignee);
            }
        }
        if(CollectionUtils.isEmpty(changedAssignees)){
           throw  new JiMuBizException("当前审批人未发生变更!勿需操作!");
        }
        String varName = assignees.get(0).getVarName();
        List<String> assigneeIds = userInfos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        taskMgmtService.changeFutureAssignees(bpmBusinessProcess.getProcInstId(), varName, assigneeIds);
        //由于是未来节点,审批任务还没有生成,因此获取不到taskId,这里记录的是nodeId
        for (Map.Entry<BaseIdTranStruVo, BaseIdTranStruVo> old2newAssignees : changedAssignees.entrySet()) {
            BaseIdTranStruVo oldAssignee = old2newAssignees.getKey();
            BaseIdTranStruVo newAssignee = old2newAssignees.getValue();
            flowrunEntrustService.addFlowrunEntrust(newAssignee.getId(),newAssignee.getName(),oldAssignee.getId(),oldAssignee.getName(),
                    nodeId,0,bpmBusinessProcess.getProcInstId(),vo.getProcessKey());
        }
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(ProcessOperationEnum.BUTTON_TYPE_CHANGE_FUTURE_ASSIGNEE);
    }
}
