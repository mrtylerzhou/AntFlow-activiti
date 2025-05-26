package org.openoa.engine.bpmnconf.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmProcessDefinition;
import org.activiti.engine.task.Task;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.entity.BpmVariableMultiplayerPersonnel;
import org.openoa.common.entity.BpmVariableSingle;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.common.service.BpmVariableSingleServiceImpl;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.confentity.BpmVariable;
import org.openoa.engine.bpmnconf.confentity.BpmVariableSignUpPersonnel;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableSignUpPersonnelServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: jwz
 * @Date: 2025/4/28 0:57
 * @Description:
 * @Version: 1.0.0
 */
@Component
public class BpmnUtils implements ApplicationContextAware {


    private static ActivitiAdditionalInfoServiceImpl activitiAdditionalInfoService;

    private static BpmVariableServiceImpl bpmVariableService;

    private static BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;

    private static BpmVariableSignUpPersonnelServiceImpl bpmVariableSignUpPersonnelService;

    private static BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService;

    private static BpmVariableSingleServiceImpl bpmVariableSingleService;

    private static BpmBusinessProcessServiceImpl bpmBusinessProcessService;

    private static Environment environment;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        activitiAdditionalInfoService = applicationContext.getBean(ActivitiAdditionalInfoServiceImpl.class);
        bpmVariableService = applicationContext.getBean(BpmVariableServiceImpl.class);
        bpmVariableMultiplayerService = applicationContext.getBean(BpmVariableMultiplayerServiceImpl.class);
        bpmVariableSignUpPersonnelService = applicationContext.getBean(BpmVariableSignUpPersonnelServiceImpl.class);
        bpmBusinessProcessService = applicationContext.getBean(BpmBusinessProcessServiceImpl.class);
        bpmVariableMultiplayerPersonnelService = applicationContext.getBean(BpmVariableMultiplayerPersonnelServiceImpl.class);
        bpmVariableSingleService = applicationContext.getBean(BpmVariableSingleServiceImpl.class);
        environment = applicationContext.getEnvironment();
    }

    /**
     * 获取下一个节点
     *
     * @param elementId         当前节点id
     * @param processInstanceId 实例id
     * @return 下一个节点
     */
    public static PvmActivity getNextElement(String elementId, String processInstanceId) {
        return activitiAdditionalInfoService.getNextElement(elementId, processInstanceId);
    }

    public static PvmActivity getNextElement(Task currentTask) {
        return getNextElement(currentTask.getTaskDefinitionKey(), currentTask.getProcessInstanceId());
    }


    /**
     * 获取下一个节点审批人
     *
     * @param currentTask 当前任务
     * @return 审批人列表
     */
    public static List<String> getNextNodeApprovers(Task currentTask) {
        String elementId = currentTask.getTaskDefinitionKey();
        String processInstanceId = currentTask.getProcessInstanceId();

        PvmActivity nextElement = getNextElement(elementId, processInstanceId);
        return getNextNodeApprovers(nextElement);
    }


    /**
     * 获取下一个节点审批人
     *
     * @param nextElement 当前任务
     * @return 审批人列表
     */
    public static List<String> getNextNodeApprovers(PvmActivity nextElement) {
        PvmProcessDefinition processDefinition = nextElement.getProcessDefinition();
        String processId = processDefinition.getKey();
        List<BpmVariable> bpmVariables = bpmVariableService.getBaseMapper().
                selectList(new QueryWrapper<BpmVariable>().eq("process_num", processId));
        BpmVariable bpmVariable = null;
        if (!ObjectUtils.isEmpty(bpmVariables)) {
            bpmVariable = bpmVariables.get(0);
        }
        if (!ObjectUtils.isEmpty(nextElement)) {
            if (!"endEvent".equals(nextElement.getProperty("type"))) {
                List<String> nextNodeApproveds = getNextNodeApproveds(bpmVariable.getId(), nextElement.getId());


                return nextNodeApproveds;
            }

        }

        return Collections.EMPTY_LIST;

    }


    private static List<String> getNextNodeApproveds(Long variableId, String nextElementId) {

        //query to check whether sign variable has parameter,if yes then return;
        if (bpmVariableSingleService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableSingle>()
                .eq("variable_id", variableId)
                .eq("element_id", nextElementId)) > 0) {
            List<String> nextNodeApproveds = Lists.newArrayList();
            BpmVariableSingle bpmVariableSingle = bpmVariableSingleService.getBaseMapper().selectOne(new QueryWrapper<BpmVariableSingle>()
                    .eq("variable_id", variableId)
                    .eq("element_id", nextElementId));
            nextNodeApproveds.add(bpmVariableSingle.getAssignee());
            return nextNodeApproveds;
        }


        //query to check whether multiplayer variables have parameter,if yes then return;
        if (bpmVariableMultiplayerService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableMultiplayer>()
                .eq("variable_id", variableId)
                .eq("element_id", nextElementId)) > 0) {
            List<String> nextNodeApproveds = Lists.newArrayList();
            BpmVariableMultiplayer bpmVariableMultiplayer = bpmVariableMultiplayerService.getBaseMapper().selectOne(new QueryWrapper<BpmVariableMultiplayer>()
                    .eq("variable_id", variableId)
                    .eq("element_id", nextElementId));
            nextNodeApproveds.addAll(bpmVariableMultiplayerPersonnelService.getBaseMapper().selectList(new QueryWrapper<BpmVariableMultiplayerPersonnel>()
                            .eq("variable_multiplayer_id", bpmVariableMultiplayer.getId()))
                    .stream()
                    .map(BpmVariableMultiplayerPersonnel::getAssignee)
                    .collect(Collectors.toList()));
            return nextNodeApproveds;
        }


        //query to check whether sign up node has parameters,if yes,then query and return data
        if (bpmVariableSignUpPersonnelService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableSignUpPersonnel>()
                .eq("variable_id", variableId)
                .eq("element_id", nextElementId)) > 0) {
            List<String> nextNodeApproveds = Lists.newArrayList();
            nextNodeApproveds.addAll(bpmVariableSignUpPersonnelService.getBaseMapper().selectList(new QueryWrapper<BpmVariableSignUpPersonnel>()
                            .eq("variable_id", variableId)
                            .eq("element_id", nextElementId))
                    .stream()
                    .map(BpmVariableSignUpPersonnel::getAssignee)
                    .collect(Collectors.toList()));
            return nextNodeApproveds;
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * 判断是否为多节点
     *
     * @param pvmActivity
     * @return
     */
    public static boolean isMultiNode(PvmActivity pvmActivity) {
        PvmProcessDefinition processDefinition = pvmActivity.getProcessDefinition();
        String processId = processDefinition.getKey();

        return isMoreNode(processId, pvmActivity.getId());

    }

    /**
     * 判断是否为多节点(会签节点)
     *
     * @param processNum
     * @param elementId
     * @return
     */
    public static boolean isMoreNode(String processNum, String elementId) {
        List<BpmVariableMultiplayer> list = bpmVariableMultiplayerService.isMoreNode(processNum, elementId);
        System.out.println("list:" + list.size());
        //if it is a multiple assignees node,and the task has not been undertaken yet,and the number of people is more than one,return true
        if (list != null && list.size() > 1 && (list.get(0).getSignType() == 1)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为外部流程
     *
     * @param processNum
     * @return
     */
    public static boolean isOutSide(String processNum) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getOne(Wrappers.<BpmBusinessProcess>lambdaQuery()
                .eq(BpmBusinessProcess::getProcInstId, processNum), false);
        if (bpmBusinessProcess != null && bpmBusinessProcess.getIsOutSideProcess() == 1) {
            return true;
        }
        return false;
    }


    public static String getEnvironmentValue(String  key) {

        return environment.getProperty(key);
    }

}
