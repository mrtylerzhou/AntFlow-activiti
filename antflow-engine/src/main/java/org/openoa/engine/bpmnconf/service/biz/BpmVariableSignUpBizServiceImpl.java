package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import org.openoa.base.entity.BpmVariable;
import org.openoa.base.entity.BpmVariableSignUp;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableSignUpBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BpmVariableSignUpBizServiceImpl implements BpmVariableSignUpBizService {
    @Autowired
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
    @Override
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

        long count = this.getMapper().selectCount(new QueryWrapper<BpmVariableSignUp>()
                .eq("variable_id", bpmVariable.getId())
                .eq("element_id", nodeId));

        return count > 0;
    }

    @Override
    public List<BpmVariableSignUp> getSignUpList(String processNumber) {
        if (Strings.isNullOrEmpty(processNumber)) {
            return null;
        }
        BpmVariable bpmVariable = bpmVariableService.getBaseMapper().selectOne(new QueryWrapper<BpmVariable>()
                .eq("process_num", processNumber)
                .eq("is_del", 0));

        return this.getMapper().selectList(new QueryWrapper<BpmVariableSignUp>()
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
