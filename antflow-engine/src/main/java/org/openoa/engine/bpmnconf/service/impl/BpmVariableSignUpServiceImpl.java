package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import org.openoa.engine.bpmnconf.confentity.BpmVariable;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.engine.bpmnconf.confentity.BpmVariableSignUp;
import org.openoa.engine.bpmnconf.mapper.BpmVariableSignUpMapper;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVariableSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BpmVariableSignUpServiceImpl extends ServiceImpl<BpmVariableSignUpMapper, BpmVariableSignUp> implements BpmVariableSignUpService {


    @Autowired
    @Lazy
    private BpmVariableServiceImpl bpmVariableService;

    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;

    /**
     * check whether a node is sign up by processNumber and nodeId
     *
     * @param processNumber
     * @param nodeId
     * @return
     */
    public Boolean checkNodeIsSignUp(String processNumber, String nodeId) {

        if (Strings.isNullOrEmpty(processNumber) || Strings.isNullOrEmpty(nodeId)) {
            return false;
        }

        if (isMoreNode(processNumber, nodeId)) {
            return false;
        }

        BpmVariable bpmVariable = bpmVariableService.getBaseMapper().selectOne(new QueryWrapper<BpmVariable>()
                .eq("process_num", processNumber)
                .eq("is_del", 0));

        long count = this.getBaseMapper().selectCount(new QueryWrapper<BpmVariableSignUp>()
                .eq("variable_id", bpmVariable.getId())
                .eq("element_id", nodeId));

        return count > 0;
    }

    public List<BpmVariableSignUp> getSignUpList(String processNumber) {
        if (Strings.isNullOrEmpty(processNumber)) {
            return null;
        }
        BpmVariable bpmVariable = bpmVariableService.getBaseMapper().selectOne(new QueryWrapper<BpmVariable>()
                .eq("process_num", processNumber)
                .eq("is_del", 0));

        return this.getBaseMapper().selectList(new QueryWrapper<BpmVariableSignUp>()
                .eq("variable_id", bpmVariable.getId()));
    }
    /**
     * to check whether the node has more than one assignees
     *
     * @param processNum
     * @param elementId
     * @return
     */
    private boolean isMoreNode(String processNum, String elementId) {
        List<BpmVariableMultiplayer> list = bpmVariableMultiplayerService.isMoreNode(processNum, elementId);
        //if it is a multiple assignees node,and the task has not been undertaken yet,and the number of people is more than one,return true
        if (list != null && list.size() > 1 && list.get(0).getSignType() == 2) {
            return true;
        }
        return false;
    }

}