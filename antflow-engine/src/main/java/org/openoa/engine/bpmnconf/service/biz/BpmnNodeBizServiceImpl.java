package org.openoa.engine.bpmnconf.service.biz;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnNodeBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class BpmnNodeBizServiceImpl implements BpmnNodeBizService {
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;

    @Override
    public BpmnNodeVo getBpmnNodeVoByTaskDefKey(String processNumber,String taskDefKey){
        if(!StringUtils.hasText(taskDefKey)){
            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL,"参数taskDefKey不能为空!");
        }
        String nodeIdByElementId = bpmVariableMultiplayerMapper.getNodeIdByElementId(processNumber, taskDefKey);
        return getBpmnNodeVoByNodeId(processNumber,nodeIdByElementId);
    }
    @Override
    public BpmnNodeVo getBpmnNodeVoByNodeId(String processNumber,String nodeId){
        if(!StringUtils.hasText(nodeId)){
            log.error("未能根据流程运行时节点找到流程设计时节点信息,流程编号:{},节点id:{}",processNumber,nodeId);
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR,"未能根据流程运行时节点找到流程设计时节点信息!");
        }
        BpmnNode bpmnNode = getMapper().selectById(nodeId);
        if(bpmnNode==null){
            log.error("未能找到流程节点信息:{},节点taskDefKey:{},节点Id:{}",processNumber,nodeId);
            throw  new AFBizException(BusinessErrorEnum.STATUS_ERROR,"未能找到流程节点信息!");
        }
        BpmnConfBizServiceImpl bpmnConfBizService = SpringBeanUtils.getBean(BpmnConfBizServiceImpl.class);
        List<BpmnNodeVo> bpmnNodeVoList = bpmnConfBizService.getBpmnNodeVoList(Lists.newArrayList(bpmnNode), null);
        return bpmnNodeVoList.get(0);
    }
}
