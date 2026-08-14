package org.openoa.engine.bpmnconf.adp.processoperation;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeAutoNodeConfJson;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BpmnNodeConditionsConfVueVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.UDLFApplyVo;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 此抽象类主要是为了维护AntFlow内部的数个demo流程,用户创建DIY流程建议按照文档[1.antflow-activiti快速上手指南一]里面的快速开始一个DIY流程里介绍的方法来,即实现FormOperationAdaptor
 * 如果直接继承本类,确定你已经明白了(或者大概明白了)每个方法做什么的
 * @param <T>
 */
@Slf4j
public abstract class AbstractFormOperationAdaptor<T extends BusinessDataVo>  implements FormOperationAdaptor<T>, ActivitiService {



    @Autowired
    private BpmnNodeService bpmnNodeService;
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;

    @Override
    public void initData(T vo) {

    }


    @Override
    public void queryData(T vo) {

    }

    @Override
    public void submitData(T vo) {

    }

    @Override
    public void consentData(T vo) {

    }

    @Override
    public void backToModifyData(T vo) {

    }

    @Override
    public void cancellationData(T businessDataVo) {

    }

    @Override
    public void onProcessRecover(BusinessDataVo businessData) {

    }

    public  abstract Boolean autoCondition(T vo);
    /**
     * 默认实现: 仅支持低代码流程 (UDLFApplyVo).
     * 从节点配置的 autoNodeConf 中读取条件, 对 lfFields 进行基础评估.
     * 非低代码流程 (DIY) 直接返回 false, 用户必须重写此方法.
     * 如果没有配置条件, 返回 null (无条件执行 automaticAction).
     */
    @Override
    public Boolean automaticCondition(T vo) {
        //如果用户想要自定义自动流转条件,则需要重写autoCondition,并返回非null,值,如果返回了null,则走默认的自动流转条件
        Boolean b = autoCondition(vo);
        if (b != null) {
            return b;
        }
        // DIY/regular flows: users must override this method
        if (!(vo instanceof UDLFApplyVo)) {
            return false;
        }
        try {
            BpmnNodeAutoNodeConfJson autoNodeConf = loadAutoNodeConf(vo);
            if (autoNodeConf == null || CollectionUtils.isEmpty(autoNodeConf.getConditionList())) {
                return null;
            }
            Map<String, Object> lfFields = ((UDLFApplyVo) vo).getLfFields();
            if (CollectionUtils.isEmpty(lfFields)) {
                return false;
            }
            return evaluateConditions(autoNodeConf, lfFields);
        } catch (AFBizException e) {
            throw e;
        } catch (Exception e) {
            log.warn("automaticCondition evaluation failed, returning null: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Load auto node condition config from DB.
     */
    private BpmnNodeAutoNodeConfJson loadAutoNodeConf(BusinessDataVo vo) {

        String processNumber = vo.getProcessNumber();
        String taskDefKey = vo.getTaskDefKey();
        if (!StringUtils.hasText(processNumber) || !StringUtils.hasText(taskDefKey)) {
            return null;
        }
        // Find active BpmnConf by formCode
        BpmnConfBizService bpmnConfBizService = SpringBeanUtils.getBean(BpmnConfBizService.class);
        BpmnConf bpmnConf =bpmnConfBizService.getBpmnConfByFormCode(vo.getFormCode());
        if (bpmnConf == null) {
           throw  new AFBizException("cant not get bpmnconf by formcode+"+vo.getFormCode());
        }
        String nodeId = bpmVariableMultiplayerMapper.getNodeIdByElementId(vo.getProcessNumber(), vo.getTaskDefKey());
        long longId=Long.parseLong(nodeId);
        // Find BpmnNode by confId and nodeId (nodeId is looked up from taskDefKey via getNodeIdByElementId)
        BpmnNode bpmnNode = bpmnNodeService.getOne(
                Wrappers.<BpmnNode>lambdaQuery()
                        .eq(BpmnNode::getConfId, bpmnConf.getId())
                        .eq(BpmnNode::getId, longId)
                        .eq(BpmnNode::getIsDel, 0)
        );
        if (bpmnNode == null || !StringUtils.hasText(bpmnNode.getNodeConfigJson())) {
            return null;
        }
        BpmnNodeConfigJson configJson = JsonConfUtil.parseNodeConfig(bpmnNode.getNodeConfigJson());
        if (configJson == null) {
           return null;
        }
        return configJson.getAutoNodeConf();
    }

    /**
     * Evaluate auto node conditions against form fields.
     * 评估逻辑已抽取至 {@link AutoConditionEvaluator}, 供自动审批设置等场景复用。
     */
    private Boolean evaluateConditions(BpmnNodeAutoNodeConfJson autoNodeConf, Map<String, Object> formFields) {
        return AutoConditionEvaluator.evaluate(autoNodeConf.getConditionList(), autoNodeConf.getGroupRelation(), formFields);
    }

}
