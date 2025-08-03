package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.openoa.base.entity.BpmVariable;
import org.openoa.base.service.BpmVariableService;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.mapper.BpmVariableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * bpm variable serivce
 *
 */
@Service("bpmVariableService")
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
}
