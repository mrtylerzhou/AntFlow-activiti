package org.openoa.engine.bpmnconf.common;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.engine.bpmnconf.service.impl.BpmVariableSignUpPersonnelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    private HistoryService historyService;
    @Autowired
    private BpmnEmployeeInfoProviderService employeeInfoProvider;
    @Autowired
    private BpmVariableSignUpPersonnelServiceImpl bpmVariableSignUpPersonnelService;
    /**
     * get a list of activiti by a historic process instance
     *
     * @param historicProcessInstance
     * @return
     */
    public List<ActivityImpl> getActivitiList(HistoricProcessInstance historicProcessInstance) {


        // get current process's defination entity by process definition id.then get all activities
        ProcessDefinitionEntity def = (ProcessDefinitionEntity) repositoryService
                .getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());
        List<ActivityImpl> activitiList = def.getActivities();

        return activitiList;
    }
    /**
     * get historic variable instance map
     *
     * @param procInstId
     * @return
     */
    public Multimap<String, HistoricVariableInstance> getVariableInstanceMap(String procInstId) {
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(procInstId).list();

        Multimap<String, HistoricVariableInstance> listMultimap = ArrayListMultimap.create();
        for (HistoricVariableInstance variableInstance : variableInstances) {
            listMultimap.put(variableInstance.getVariableName(), variableInstance);
        }

        return listMultimap;
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
    /**
     * get assignees from activity engine
     *
     * @param elementId
     * @param signUpNodeCollectionNameMap
     * @param variableInstanceMap
     * @return
     */
    public String getVerifyUserNameFromHis(String elementId, Map<String, String> signUpNodeCollectionNameMap, Multimap<String, HistoricVariableInstance> variableInstanceMap,Long variableId) {

        String verifyUserName = StringUtils.EMPTY;

        String collectionName = signUpNodeCollectionNameMap.get(elementId);
        if (!ObjectUtils.isEmpty(collectionName)) {

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

        }

        return verifyUserName;
    }
}
