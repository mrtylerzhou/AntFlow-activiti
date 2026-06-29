package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import org.openoa.base.entity.BpmVariable;
import org.openoa.base.entity.jsonconf.VariableConfigJson;
import org.openoa.base.entity.jsonconf.VariableConfigJson.SignUpItem;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableSignUpBizService;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BpmVariableSignUpBizServiceImpl implements BpmVariableSignUpBizService {
    @Autowired
    private BpmVariableServiceImpl bpmVariableService;

    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;

    /**
     * check whether a node is sign up by processNumber and nodeId
     */
    @Override
    public Boolean checkNodeIsSignUp(String processNumber, String nodeId) {
        if (Strings.isNullOrEmpty(processNumber) || Strings.isNullOrEmpty(nodeId)) {
            return false;
        }

        if (isMoreNode(processNumber, nodeId)) {
            return false;
        }

        List<SignUpItem> signUps = getSignUpsFromJson(processNumber);
        if (ObjectUtils.isEmpty(signUps)) {
            return false;
        }

        return signUps.stream().anyMatch(s -> nodeId.equals(s.getElementId()));
    }

    /**
     * get sign up list by process number
     */
    @Override
    public List<SignUpItem> getSignUpList(String processNumber) {
        if (Strings.isNullOrEmpty(processNumber)) {
            return null;
        }
        return getSignUpsFromJson(processNumber);
    }

    /**
     * to check whether the node has more than one assignees
     */
    private boolean isMoreNode(String processNum, String elementId) {
        List<BpmVariableMultiplayer> list = bpmVariableMultiplayerService.isMoreNode(processNum, elementId);
        if (list != null && list.size() > 1 && list.get(0).getSignType() == 2) {
            return true;
        }
        return false;
    }

    private List<SignUpItem> getSignUpsFromJson(String processNumber) {
        BpmVariable bpmVariable = bpmVariableService.getBaseMapper().selectOne(new QueryWrapper<BpmVariable>()
                .eq("process_num", processNumber)
                .eq("is_del", 0));

        if (ObjectUtils.isEmpty(bpmVariable) || ObjectUtils.isEmpty(bpmVariable.getVariableConfigJson())) {
            return Collections.emptyList();
        }

        VariableConfigJson config = JSON.parseObject(bpmVariable.getVariableConfigJson(), VariableConfigJson.class);
        if (config == null || ObjectUtils.isEmpty(config.getSignUps())) {
            return Collections.emptyList();
        }

        return config.getSignUps();
    }
}
