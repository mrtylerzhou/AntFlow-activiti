package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmVerifyInfoVo;
import org.openoa.engine.bpmnconf.common.ActivitiAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.confentity.*;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.engine.bpmnconf.service.impl.*;
import org.openoa.base.vo.BpmnConfCommonElementVo;
import org.openoa.common.entity.BpmVariableMultiplayer;
import org.openoa.common.entity.BpmVariableMultiplayerPersonnel;
import org.openoa.common.entity.BpmVariableSingle;
import org.openoa.common.service.BpmVariableMultiplayerPersonnelServiceImpl;
import org.openoa.common.service.BpmVariableMultiplayerServiceImpl;
import org.openoa.common.service.BpmVariableSingleServiceImpl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.ProcessNodeEnum.START_TASK_KEY;
import static org.openoa.base.constant.enums.ProcessStateEnum.*;

/**
 * @Author JimuOffice
 * @Description verify info biz service
 * @Param
 * @return
 * @Version 0.5
 * BpmVerifyInfoNewServiceImpl
 */
@Service
public class BpmVerifyInfoBizServiceImpl extends BizServiceImpl<BpmVerifyInfoServiceImpl> {
    @Autowired
    private BpmBusinessProcessServiceImpl bpmBusinessProcessService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private BpmVariableServiceImpl bpmVariableService;
    @Autowired
    private ActivitiAdditionalInfoServiceImpl activitiAdditionalInfoService;
    @Autowired
    private BpmVariableSignUpServiceImpl bpmVariableSignUpService;
    @Autowired
    private BpmVariableSignUpPersonnelServiceImpl bpmVariableSignUpPersonnelService;
    @Autowired
    private BpmVariableSingleServiceImpl bpmVariableSingleService;
    @Autowired
    private BpmVariableMultiplayerServiceImpl bpmVariableMultiplayerService;
    @Autowired
    private BpmVariableMultiplayerPersonnelServiceImpl bpmVariableMultiplayerPersonnelService;
    @Autowired
    private BpmnEmployeeInfoProviderService employeeInfoProvider;
    @Resource
    private BpmFlowrunEntrustServiceImpl bpmFlowrunEntrustService;

    public List<BpmVerifyInfoVo> getVerifyInfoList(String processCode) {
        List<BpmVerifyInfoVo> bpmVerifyInfoVos = service.verifyInfoList(processCode);
        return bpmVerifyInfoVos;
    }

    /**
     * get verify path for a process
     *
     * @param processNumber process Number
     * @param finishFlag    to indicate whether a process is finished true for finished and false for not finished yet
     * @return verify path include finished and unfinished nodes
     */
    public List<BpmVerifyInfoVo> getBpmVerifyInfoVos(String processNumber, boolean finishFlag) {
        List<BpmVerifyInfoVo> bpmVerifyInfoVos = Lists.newArrayList();

        //query business process info
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBaseMapper().selectOne(new QueryWrapper<BpmBusinessProcess>().eq("BUSINESS_NUMBER", processNumber));


        if (ObjectUtils.isEmpty(bpmBusinessProcess)) {
            return bpmVerifyInfoVos;
        }


        //add start node for process
        bpmVerifyInfoVos.add(BpmVerifyInfoVo.builder().taskName("发起").verifyStatus(1).verifyUserIds(Lists.newArrayList(bpmBusinessProcess.getCreateUser())).verifyUserName(bpmBusinessProcess.getUserName()).verifyDate(bpmBusinessProcess.getCreateTime()).verifyStatusName("提交").build());


        //query and then append process record
        List<BpmVerifyInfoVo> searchBpmVerifyInfoVos = service.verifyInfoList(processNumber, bpmBusinessProcess.getProcInstId());

        //sort verify info by verify date in ascending order
        searchBpmVerifyInfoVos = searchBpmVerifyInfoVos.stream().sorted(Comparator.comparing(BpmVerifyInfoVo::getVerifyDate)).collect(Collectors.toList());
        bpmVerifyInfoVos.addAll(searchBpmVerifyInfoVos);


        //query process's historic process instance
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).singleResult();

        //query from the process engine to get the last approval record
        HistoricTaskInstance lastHistoricTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(historicProcessInstance.getId()).orderByHistoricTaskInstanceEndTime().desc().list().get(0);

        //begin to sort the verify info
        int sort = 0;

        //iterate through the verify info
        List<BpmVerifyInfoVo> bpmVerifyInfoSortVos = Lists.newArrayList();
        for (BpmVerifyInfoVo bpmVerifyInfoVo : bpmVerifyInfoVos) {
            if (bpmVerifyInfoVo.getVerifyStatus() == 3 || bpmVerifyInfoVo.getVerifyStatus() == 6) {
                bpmVerifyInfoVo.setTaskName(lastHistoricTaskInstance.getName());

                bpmVerifyInfoVo.setVerifyStatusName("审批拒绝");
            }

            if (bpmVerifyInfoVo.getVerifyStatus() == 5) {
                //todo 待实现

                String lastAssignee = lastHistoricTaskInstance.getAssignee();
                String lastAssigneeName = lastHistoricTaskInstance.getAssigneeName();

                BpmVerifyInfoVo vo = new BpmVerifyInfoVo();
                BeanUtils.copyProperties(bpmVerifyInfoVo, vo);
                vo.setTaskName("发起人");
                vo.setVerifyUserIds(Lists.newArrayList(bpmBusinessProcess.getCreateUser()));
                vo.setVerifyUserName(bpmBusinessProcess.getUserName());
                vo.setVerifyDate(vo.getVerifyDate());
                vo.setSort(sort);
                sort++;
                bpmVerifyInfoSortVos.add(vo);


                bpmVerifyInfoVo.setTaskName(lastHistoricTaskInstance.getName());
                bpmVerifyInfoVo.setVerifyUserId(lastAssignee);
                if (!StringUtils.isEmpty(lastAssigneeName)) {
                    bpmVerifyInfoVo.setVerifyUserName(lastAssigneeName);
                } else {
                    Map<String, String> provideEmployeeInfo = employeeInfoProvider.provideEmployeeInfo(Lists.newArrayList(lastAssignee));
                    bpmVerifyInfoVo.setVerifyUserName(provideEmployeeInfo.get(lastAssignee));
                }

                bpmVerifyInfoVo.setVerifyDate(null);
                bpmVerifyInfoVo.setVerifyDesc(StringUtils.EMPTY);
                bpmVerifyInfoVo.setVerifyStatus(0);
                bpmVerifyInfoVo.setVerifyStatusName(StringUtils.EMPTY);
            }

            bpmVerifyInfoVo.setSort(sort);
            bpmVerifyInfoSortVos.add(bpmVerifyInfoVo);
            sort++;
        }

        bpmVerifyInfoVos.clear();
        bpmVerifyInfoVos.addAll(bpmVerifyInfoSortVos);


        //query to do task info
        List<BpmVerifyInfoVo> taskInfo = service.findTaskInfo(bpmBusinessProcess);

        BpmVerifyInfoVo taskVo;

        if (!ObjectUtils.isEmpty(taskInfo) && !finishFlag) {

            //append to do task info
            taskVo = taskInfo.get(0);
            taskVo.setSort(sort);
            taskVo.setVerifyStatus(99);
            taskVo.setVerifyStatusName("处理中");
            bpmVerifyInfoVos.add(taskVo);

            BpmFlowrunEntrust flowrunEntrust = bpmFlowrunEntrustService.getOne(
                    Wrappers.
                            <BpmFlowrunEntrust>lambdaQuery()
                            .eq(BpmFlowrunEntrust::getRuntaskid, taskVo.getId()));
            if(flowrunEntrust!=null){
                String actual = flowrunEntrust.getActual();
                if(taskVo.getVerifyUserId().equals(actual)){
                    String actualVerifyUserName = taskVo.getVerifyUserName();
                    String actualName = flowrunEntrust.getActualName();
                    taskVo.setVerifyUserName(actualVerifyUserName+"代"+actualName+"审批");
                }
            }
            sort++;


            // if the task node is start node, then it means the process is returned and the last approval record is appended to the list
            if (taskVo.getElementId().equals(START_TASK_KEY.getDesc())) {
                taskVo = new BpmVerifyInfoVo();
                taskVo.setElementId(lastHistoricTaskInstance.getTaskDefinitionKey());
                taskVo.setTaskName(lastHistoricTaskInstance.getName());
                taskVo.setVerifyStatus(0);
                String lastAssignee = lastHistoricTaskInstance.getAssignee();
                Map<String, String> provideEmployeeInfo = employeeInfoProvider.provideEmployeeInfo(Lists.newArrayList(lastAssignee));
                String lastVerifierName = provideEmployeeInfo.get(lastAssignee);
                taskVo.setVerifyUserId(lastAssignee);
                taskVo.setVerifyUserName(lastVerifierName);
                bpmVerifyInfoVos.add(taskVo);
                sort++;
            }

        } else {
            taskVo = new BpmVerifyInfoVo();
            taskVo.setElementId(lastHistoricTaskInstance.getTaskDefinitionKey());
        }

        Integer processState = bpmBusinessProcess.getProcessState();

        Integer endVerifyStatus = 100;
        if (processState != CRMCEL_STATE.getCode() || processState != END_STATE.getCode()) {
            if (!finishFlag) {
                //追加流程记录
                addBpmVerifyInfoVo(processNumber, sort, bpmVerifyInfoVos, historicProcessInstance, taskVo);
            }
            if (processState == COMLETE_STATE.getCode()) {
                endVerifyStatus = 0;
            }
        }

        bpmVerifyInfoVos.add(BpmVerifyInfoVo.builder().taskName("流程结束").verifyStatus(endVerifyStatus).build());

        return bpmVerifyInfoVos;
    }

    /**
     * append info
     *
     * @param processNumber
     * @param sort
     * @param bpmVerifyInfoVos
     * @param historicProcessInstance
     * @param taskVo
     */
    private void addBpmVerifyInfoVo(String processNumber, Integer sort, List<BpmVerifyInfoVo> bpmVerifyInfoVos, HistoricProcessInstance historicProcessInstance, BpmVerifyInfoVo taskVo) {

        //get all activiti flow nodes list
        List<ActivityImpl> activitiList = activitiAdditionalInfoService.getActivitiList(historicProcessInstance);

        //query process variable info
        BpmVariable bpmVariable = bpmVariableService.getBaseMapper().selectOne(new QueryWrapper<BpmVariable>().eq("process_num", processNumber));

        if (ObjectUtils.isEmpty(bpmVariable)) {
            return;
        }
        //get approvers
        Map<String, List<BaseIdTranStruVo>> nodeApproveds = getNodeApproveds(bpmVariable.getId());

        List<ActivityImpl> collect = activitiList.stream().filter(a -> a.getId().equals(taskVo.getElementId())).collect(Collectors.toList());

        if (collect.size() > 0) {

            ActivityImpl activity = collect.get(0);
            Map<String, Object> properties = activity.getProperties();
            Object multiInstance = properties.get("multiInstance");
            if("sequential".equals(multiInstance)){
                List<BaseIdTranStruVo> baseIdTranStruVos = nodeApproveds.get(taskVo.getElementId());

                List<BpmVerifyInfoVo> verifyInfoVos = bpmVerifyInfoVos.stream().filter(a -> taskVo.getElementId().equals(a.getElementId())).collect(Collectors.toList());

                List<BaseIdTranStruVo> idTranStruVos=new ArrayList<>();

                for (BaseIdTranStruVo baseIdTranStruVo : baseIdTranStruVos) {

                    for (BpmVerifyInfoVo verifyInfoVo : verifyInfoVos) {
                        if(!verifyInfoVo.getVerifyUserIds().contains(baseIdTranStruVo.getId())){
                            idTranStruVos.add(baseIdTranStruVo);
                        }

                    }
                }
                for (BaseIdTranStruVo idTranStruVo : idTranStruVos) {
                    BpmVerifyInfoVo bpmVerifyInfoVo = BpmVerifyInfoVo.builder()
                            .verifyUserId(idTranStruVo.getId())
                            .verifyUserIds(Lists.newArrayList(idTranStruVo.getId()))
                            .elementId(taskVo.getElementId())
                            .taskName(taskVo.getTaskName())
                            .verifyUserName(idTranStruVo.getName())
                            .verifyStatus(0)
                            .sort(sort)
                            .build();
                    bpmVerifyInfoVos.add(bpmVerifyInfoVo);
                    sort++;
                }


            };
        }

        //get signup node's element id and collection name
        Map<String, String> signUpNodeCollectionNameMap = getSignUpNodeCollectionNameMap(bpmVariable.getId());

        //todo 需要实现
        //get employee id map

        //variable name 2 HistoricVariableInstance
        Multimap<String, HistoricVariableInstance> variableInstanceMap = activitiAdditionalInfoService.getVariableInstanceMap(historicProcessInstance.getId());

        //do append record
        doAddBpmVerifyInfoVo(sort, taskVo.getElementId(), activitiList, nodeApproveds, signUpNodeCollectionNameMap, bpmVerifyInfoVos, variableInstanceMap, bpmVariable.getId());    }

    /**
     * to process the signup special node
     *
     * @param variableId
     * @return
     */
    public Map<String, String> getSignUpNodeCollectionNameMap(Long variableId) {

        Map<String, String> signUpNodeCollectionNameMap = Maps.newHashMap();

        List<BpmVariableSignUp> bpmVariableSignUps = bpmVariableSignUpService.getBaseMapper().selectList(new QueryWrapper<BpmVariableSignUp>().eq("variable_id", variableId));

        for (BpmVariableSignUp variableSignUp : bpmVariableSignUps) {
            if (!ObjectUtils.isEmpty(variableSignUp.getSubElements())) {
                List<BpmnConfCommonElementVo> bpmnConfCommonElementVos = JSON.parseArray(variableSignUp.getSubElements(), BpmnConfCommonElementVo.class);
                if (!ObjectUtils.isEmpty(bpmnConfCommonElementVos)) {
                    for (BpmnConfCommonElementVo bpmnConfCommonElementVo : bpmnConfCommonElementVos) {
                        signUpNodeCollectionNameMap.put(bpmnConfCommonElementVo.getElementId(), bpmnConfCommonElementVo.getCollectionName());
                    }
                }
            }
        }

        return signUpNodeCollectionNameMap;
    }

    /**
     * do append verify info
     *
     * @param sort
     * @param elementId
     * @param activitiList
     * @param nodeApproveds
     * @param bpmVerifyInfoVos
     */
    private void doAddBpmVerifyInfoVo(Integer sort, String elementId, List<ActivityImpl> activitiList,
                                      Map<String, List<BaseIdTranStruVo>> nodeApproveds,
                                      Map<String, String> signUpNodeCollectionNameMap,
                                      List<BpmVerifyInfoVo> bpmVerifyInfoVos, Multimap<String,
            HistoricVariableInstance> variableInstanceMap,
            Long variableId) {

        //get the netxt pvm activity element
        PvmActivity nextElement = activitiAdditionalInfoService.getNextElement(elementId, activitiList);

        if (ObjectUtils.isEmpty(nextElement)) {
            return;
        }

        //get next node's approvers
        List<BaseIdTranStruVo> baseIdTranStruVos = nodeApproveds.get(nextElement.getId());
        List<String> empIds = new ArrayList<>();

        List<String> emplNames = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(baseIdTranStruVos)) {
            for (BaseIdTranStruVo empBaseInfo : baseIdTranStruVos) {
                String emplIdStr=empBaseInfo.getId();
                String name = empBaseInfo.getName();
                empIds.add(emplIdStr);
                emplNames.add(name);
            }
        }

        //then assemble them
        String verifyUserName="";
        if (!ObjectUtils.isEmpty(emplNames)) {
            verifyUserName = StringUtils.join(emplNames, ",");
        } else {

            //If can not get the approvers info,then get it from activity engine
            verifyUserName = activitiAdditionalInfoService.getVerifyUserNameFromHis(nextElement.getId(), signUpNodeCollectionNameMap, variableInstanceMap,variableId);
        }

        String taskName = Optional.ofNullable(nextElement.getProperty("name")).map(String::valueOf).orElse(StringUtils.EMPTY);

        BpmVerifyInfoVo bpmVerifyInfoVo = BpmVerifyInfoVo.builder().elementId(nextElement.getId()).taskName(taskName).verifyDesc(StringUtils.EMPTY).verifyStatus(0).verifyUserIds(empIds).verifyUserName(verifyUserName).sort(sort).build();


        //add to verify infos
        if (!ObjectUtils.isEmpty(bpmVerifyInfoVo.getVerifyUserName()) && !bpmVerifyInfoVo.getTaskName().equals("EndEvent")) {
            bpmVerifyInfoVos.add(bpmVerifyInfoVo);
            sort++;
        }


        //get next node's next node,if it still exist,then treat it recursively
        PvmActivity nextNextElement = activitiAdditionalInfoService.getNextElement(nextElement.getId(), activitiList);
        if (!ObjectUtils.isEmpty(nextNextElement)) {
            doAddBpmVerifyInfoVo(sort, nextElement.getId(), activitiList, nodeApproveds, signUpNodeCollectionNameMap, bpmVerifyInfoVos, variableInstanceMap,variableId);
        }
    }

    /**
     * @param variableId
     * @return
     */
    private Map<String, List<BaseIdTranStruVo>> getNodeApproveds(Long variableId) {

        Map<String, List<BaseIdTranStruVo>> nodeApprovedsMap = Maps.newHashMap();


        if (bpmVariableSingleService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableSingle>().eq("variable_id", variableId)) > 0) {
            for (BpmVariableSingle bpmVariableSingle : bpmVariableSingleService.getBaseMapper().selectList(new QueryWrapper<BpmVariableSingle>().eq("variable_id", variableId))) {
                nodeApprovedsMap.put(bpmVariableSingle.getElementId(), Lists.newArrayList(BaseIdTranStruVo.builder().id(bpmVariableSingle.getAssignee()).name(bpmVariableSingle.getAssigneeName()).build()));
            }
        }


        if (bpmVariableMultiplayerService.getBaseMapper().selectCount(new QueryWrapper<BpmVariableMultiplayer>().eq("variable_id", variableId)) > 0) {
            List<BpmVariableMultiplayer> bpmVariableMultiplayers = bpmVariableMultiplayerService.getBaseMapper().selectList(new QueryWrapper<BpmVariableMultiplayer>().eq("variable_id", variableId));
            for (BpmVariableMultiplayer bpmVariableMultiplayer : bpmVariableMultiplayers) {
                List<BpmVariableMultiplayerPersonnel> bpmVariableMultiplayerPersonnels = bpmVariableMultiplayerPersonnelService.getBaseMapper().selectList(new QueryWrapper<BpmVariableMultiplayerPersonnel>().eq("variable_multiplayer_id", bpmVariableMultiplayer.getId()));
                if (!ObjectUtils.isEmpty(bpmVariableMultiplayerPersonnels)) {
                    nodeApprovedsMap.put(bpmVariableMultiplayer.getElementId(), bpmVariableMultiplayerPersonnels.stream().map(a->BaseIdTranStruVo.builder().id(a.getAssignee()).name(a.getAssigneeName()).build()).collect(Collectors.toList()));
                }
            }
        }
        return nodeApprovedsMap;
    }
}
