package org.openoa.engine.bpmnconf.service.flowcontrol;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.TaskActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.el.FixedValue;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

import java.util.List;

public class ActivitiDynamicMultiInstanceUtil {

    /**
     * 普通用户任务节点 → 多实例节点
     */
    public static void convertToMultiInstance(
            ProcessEngine processEngine,
            String processDefinitionId,
            String activityId,
            boolean sequential,
            String collectionVariable,
            String elementVariable) {

        ProcessDefinitionEntity processDefinition =
                (ProcessDefinitionEntity) processEngine
                        .getRepositoryService()
                        .getProcessDefinition(processDefinitionId);


        ActivityImpl activity = processDefinition.findActivity(activityId);

        if (activity == null) {
            throw new RuntimeException("activity not found: " + activityId);
        }

        if (!(activity.getActivityBehavior() instanceof UserTaskActivityBehavior)) {
            throw new RuntimeException("activity is not a userTask");
        }

        UserTaskActivityBehavior inner =
                (UserTaskActivityBehavior) activity.getActivityBehavior();

        MultiInstanceActivityBehavior multiInstance;

        if (sequential) {
            multiInstance = new SequentialMultiInstanceBehavior(activity, inner);
        } else {
            multiInstance = new ParallelMultiInstanceBehavior(activity, inner);
        }

        // 设置多实例参数
        multiInstance.setCollectionExpression(new FixedValue(collectionVariable));
        multiInstance.setCollectionElementVariable(elementVariable);

        activity.setActivityBehavior(multiInstance);

        activity.setScope(true);
        activity.setProperty("multiInstance", sequential ? "sequential" : "parallel");
    }

    /**
     * 修改某个多实例节点的执行方式
     *
     * @param processEngine
     * @param processDefinitionId
     * @param activityId
     * @param sequential true=顺序会签 false=并行会签
     */
    public static void changeMultiInstanceSequential(
            ProcessEngine processEngine,
            String processDefinitionId,
            String activityId,
            boolean sequential) {

        ProcessDefinitionEntity processDefinition =
                (ProcessDefinitionEntity) processEngine
                        .getRepositoryService()
                        .getProcessDefinition(processDefinitionId);

        ActivityImpl activity =
                processDefinition.findActivity(activityId);

        if (activity == null) {
            throw new RuntimeException("activity not found: " + activityId);
        }

        if (!(activity.getActivityBehavior() instanceof MultiInstanceActivityBehavior)) {
            throw new RuntimeException("activity is not multi-instance: " + activityId);
        }

        MultiInstanceActivityBehavior oldBehavior =
                (MultiInstanceActivityBehavior) activity.getActivityBehavior();

        TaskActivityBehavior innerBehavior =
                (TaskActivityBehavior) oldBehavior.getInnerActivityBehavior();

        MultiInstanceActivityBehavior newBehavior;

        if (sequential) {
            newBehavior = new SequentialMultiInstanceBehavior(activity, innerBehavior);
        } else {
            newBehavior = new ParallelMultiInstanceBehavior(activity, innerBehavior);
        }

        // 复制原多实例配置
        newBehavior.setCollectionExpression(oldBehavior.getCollectionExpression());
        newBehavior.setCollectionElementVariable(oldBehavior.getCollectionElementVariable());
        newBehavior.setLoopCardinalityExpression(oldBehavior.getLoopCardinalityExpression());
        newBehavior.setCompletionConditionExpression(oldBehavior.getCompletionConditionExpression());

        // 替换 behavior
        activity.setActivityBehavior(newBehavior);

        // 设置属性
        activity.setProperty("multiInstance", sequential ? "sequential" : "parallel");

    }
}