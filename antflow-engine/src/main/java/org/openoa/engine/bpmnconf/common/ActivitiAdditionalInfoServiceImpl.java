package org.openoa.engine.bpmnconf.common;

import com.google.common.collect.Lists;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.dto.ParallelPair;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableSignUpPersonnelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * a helper class to get additional information from activiti engine
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-05 11:18
 * @Param
 * @return
 * @Version 0.5
 */
@Service
public class ActivitiAdditionalInfoServiceImpl {
    @Autowired
    private RepositoryServiceImpl repositoryService;
    @Autowired
    private BpmnEmployeeInfoProviderService employeeInfoProvider;
    @Autowired
    private BpmVariableSignUpPersonnelServiceImpl bpmVariableSignUpPersonnelService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;



    public List<ActivityImpl> getActivitiList(String procDefId){
        // get current process's defination entity by process definition id.then get all activities
        ProcessDefinitionEntity def = (ProcessDefinitionEntity) repositoryService
                .getDeployedProcessDefinition(procDefId);
        List<ActivityImpl> activitiList = def.getActivities();

        return activitiList;
    }

    public PvmActivity getNextElement(String elementId, List<ActivityImpl> activitiList) {
        for (ActivityImpl activity : activitiList) {
            if (elementId.equals(activity.getId())) {
                List<PvmTransition> outTransitions = activity.getOutgoingTransitions();//get all outgoing transitions from this activity
                for (PvmTransition tr : outTransitions) {
                    PvmActivity ac = tr.getDestination(); // get the destination activity
                    return ac;
                }
                break;
            }
        }
        return null;
    }
    public List<PvmActivity> getNextElementList(String elementId, List<ActivityImpl> activitiList) {
        String[] elementIs=elementId.split(",");

        Set<PvmActivity> pvmActivityList = new HashSet<>();
        for (ActivityImpl activity : activitiList) {
            if (ArrayUtils.contains(elementIs, activity.getId())) {
                List<PvmTransition> outTransitions = activity.getOutgoingTransitions();//get all outgoing transitions from this activity
                for (PvmTransition tr : outTransitions) {
                    PvmActivity ac = tr.getDestination(); // get the destination activity
                    pvmActivityList.add(ac);
                }
                break;
            }
        }
        return Lists.newArrayList(pvmActivityList);
    }
    public PvmActivity getNextElement(String elementId,String procInstId){
        if(StringUtils.isAnyBlank(elementId,procInstId)){
            throw new AFBizException("获取流程下一节点失败,elementId或procInstId值为空!");
        }
        String procDefIdByInstId = taskMgmtMapper.findProcDefIdByInstId(procInstId);
        if(StringUtils.isBlank(procDefIdByInstId)){
            throw new AFBizException("未能根据流程实例id查找到流程定义id,请检查逻辑!");
        }
        List<ActivityImpl> activitiList = getActivitiList(procDefIdByInstId);
        return getNextElement(elementId,activitiList);
    }


    /**
     * get assignees from activity engine
     *
     * @param elementId
     * @return
     */
    public String getVerifyUserNameFromHis(String elementId, Long variableId) {

        String verifyUserName = StringUtils.EMPTY;
        List<BaseIdTranStruVo> assigneeMap = bpmVariableSignUpPersonnelService.getSignUpInfoByVariableAndElementId(variableId, elementId);
        if(!CollectionUtils.isEmpty(assigneeMap)){
            verifyUserName= StringUtils.join(assigneeMap.stream().map(BaseIdTranStruVo::getName).collect(Collectors.toList()), ',');
            return verifyUserName;
        }
        //String collectionName = signUpNodeCollectionNameMap.get(elementId);
       /* if (!ObjectUtils.isEmpty(collectionName)) {

            List<String> emplIdsStr = Lists.newArrayList();

            Collection<HistoricVariableInstance> historicVariableInstances = variableInstanceMap.get(collectionName);
            if (!ObjectUtils.isEmpty(historicVariableInstances)) {
                for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {

                    Object value = historicVariableInstance.getValue();

                    if (ObjectUtils.isEmpty(value)) {
                        continue;
                    }

                    if (historicVariableInstance.getVariableTypeName().equals("serializable")) {
                        List<String> emplIds = (List<String>) value;
                        emplIdsStr.addAll(emplIds);
                    }
                }
            }

            if (!ObjectUtils.isEmpty(emplIdsStr)) {
                Map<String, String> employeeInfo = employeeInfoProvider.provideEmployeeInfo(emplIdsStr);
                verifyUserName=StringUtils.join(employeeInfo.values(),",");
            }

        }*/

        return verifyUserName;
    }

    boolean isParallelFork(ActivityImpl act) {
        return "parallelGateway".equals(act.getProperty("type"))
                && act.getIncomingTransitions().size() == 1
                && act.getOutgoingTransitions().size() > 1;
    }
    boolean isParallelJoin(ActivityImpl act) {
        return "parallelGateway".equals(act.getProperty("type"))
                && act.getIncomingTransitions().size() > 1
                && act.getOutgoingTransitions().size() == 1;
    }

    /**
     * 判断当前节点是否处于并行网关之中
     * @param node
     * @param def
     * @return
     */
    public ParallelPair findNearestParallelGateway(ActivityImpl node, ProcessDefinitionEntity def) {

        List<ActivityImpl> activities = def.getActivities();

        // 找出流程中所有 parallelGateway 节点
        List<ActivityImpl> forks = new ArrayList<>();
        List<ActivityImpl> joins = new ArrayList<>();

        for (ActivityImpl act : activities) {
            if ("parallelGateway".equals(act.getProperty("type"))) {
                int in = act.getIncomingTransitions().size();
                int out = act.getOutgoingTransitions().size();

                if (in == 1 && out > 1) {
                    // fork
                    forks.add(act);
                } else if (in > 1 && out == 1) {
                    // join
                    joins.add(act);
                }
            }
        }


        for (ActivityImpl fork : forks) {
            for (ActivityImpl join : joins) {


                if (!canReach(fork, node)) {
                    continue;
                }


                if (!canReach(node, join)) {
                    continue;
                }


                return new ParallelPair(fork, join);
            }
        }

        return null; // 不在并行网关内部
    }
    public boolean canReach(ActivityImpl source, ActivityImpl target) {
        Set<ActivityImpl> visited = new HashSet<>();
        return dfs(source, target, visited);
    }

    private boolean dfs(ActivityImpl current, ActivityImpl target, Set<ActivityImpl> visited) {
        if (current == target) {
            return true;
        }

        visited.add(current);

        for (PvmTransition t : current.getOutgoingTransitions()) {
            ActivityImpl next = (ActivityImpl) t.getDestination();
            if (!visited.contains(next)) {
                if (dfs(next, target, visited)) {
                    return true;
                }
            }
        }
        return false;
    }


}
