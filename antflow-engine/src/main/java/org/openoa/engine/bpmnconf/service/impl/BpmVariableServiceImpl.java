package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmVariable;
import org.openoa.base.service.BpmVariableService;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.entity.BpmVariableMultiplayerPersonnel;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * bpm variable serivce
 *
 */
@Repository("bpmVariableService")
public class BpmVariableServiceImpl extends ServiceImpl<BpmVariableMapper, BpmVariable> implements BpmVariableService {
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;

    @Override
    public Map<String,String> getAssigneeNameByProcessNumAndElementId(String processNumber, String elementId){


        List<BaseIdTranStruVo> assignees =bpmVariableMultiplayerMapper.getAssigneeByElementId(processNumber, elementId);
        Map<String, String> assigneeMap = assignees.stream().collect(Collectors.toMap(BaseIdTranStruVo::getId, BaseIdTranStruVo::getName, (k1, k2) -> k1));
        return assigneeMap;
    }
    @Override
    public String getVarNameByProcessNumberAndElementId(String processNum, String elementId){
        return  bpmVariableMultiplayerMapper.getVarNameByElementId(processNum,elementId);
    }
    @Override
    public void addNodeAssignees(String processNumber, String elementId, List<BaseIdTranStruVo> assignees){
        List<BpmVariableMultiplayer> multiplayers = this.baseMapper.querymultiplayersbyprocesselementid(processNumber, elementId);
        List<BpmVariableMultiplayerPersonnel> bpmVariableMultiplayerPersonnels=new ArrayList<>();
        for (BaseIdTranStruVo assignee : assignees) {
            BpmVariableMultiplayerPersonnel multiplayerPersonnel = BpmVariableMultiplayerPersonnel
                    .builder()
                    .variableMultiplayerId(multiplayers.get(0).getId())
                    .assignee(assignee.getId())
                    .assigneeName(assignee.getName())
                    .undertakeStatus(0)
                    .remark("管理员加签")
                    .build();
            bpmVariableMultiplayerPersonnels.add(multiplayerPersonnel);
        }
        BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService = SpringBeanUtils.getBean(BpmVariableMultiplayerPersonnelServiceImpl.class);
        bpmVariableMultiplayerPersonnelService.saveBatch(bpmVariableMultiplayerPersonnels);
    }
    @Override
    public void  updateAssignee(String processNumber,String elementId,String assignee,BaseIdTranStruVo newAssigneeInfo){
        int updateSingle = this.baseMapper.updateSingle(processNumber, elementId, assignee, newAssigneeInfo.getId(), newAssigneeInfo.getName());
        if(updateSingle<=0){
            int updateMultiPlayer = this.baseMapper.updateMultiPlayer(processNumber, elementId, assignee, newAssigneeInfo.getId(), newAssigneeInfo.getName());
            if(updateMultiPlayer<=0){
                log.warn("单人节点和多人节点变量均更新失败!,请查看条件");
            }
        }
    }
}
