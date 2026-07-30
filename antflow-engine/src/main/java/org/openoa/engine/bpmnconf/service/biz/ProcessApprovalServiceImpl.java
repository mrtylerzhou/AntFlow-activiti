package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.openoa.base.entity.jsonconf.JsonConfUtil;
import org.openoa.base.util.NodeUtil;
import org.openoa.base.constant.enums.*;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.openoa.engine.bpmnconf.common.ConfigFlowButtonContans;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.mapper.ProcessApprovalMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessForwardBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableSignUpBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.ProcessApprovalService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeService;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;
import org.openoa.engine.factory.ButtonPreOperationService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.ProcessOperationEnum.BUTTON_TYPE_JP;
import static org.openoa.base.constant.enums.ProcessOperationEnum.BUTTON_TYPE_APPOINT_NEXT_NODE_APPROVER;
import static org.openoa.base.constant.enums.ProcessOperationEnum.BUTTON_TYPE_PICK_CONDITION;
import static org.openoa.base.constant.enums.ProcessStateEnum.END_STATE;
import static org.openoa.base.constant.enums.ProcessStateEnum.REJECT_STATE;

/**
 * @Classname ProcessApprovalServiceImpl
 * @Description TODO
 * @Date 2021-11-08 22:54
 * @Created by AntOffice
 */
@Service
@Slf4j
public class ProcessApprovalServiceImpl extends ServiceImpl<ProcessApprovalMapper, TaskMgmtVO> implements ProcessApprovalService {
    @Autowired
    private ButtonPreOperationService buttonPreOperationService;

    @Autowired
    private BpmnConfBizService bpmnConfCommonService;
    @Autowired
    private BpmProcessForwardBizService processForwardBizService;
    @Autowired
    private BpmnConfMapper bpmnConfMapper;
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private ProcessBusinessContans businessContans;
    @Autowired
    private ConfigFlowButtonContans configFlowButtonContans;
    @Autowired
    private BpmVariableSignUpBizService bpmVariableSignUpBizService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;
    @Autowired
    private BpmnNodeService bpmnNodeService;

    /**
     * button operation
     *
     * @param params
     * @param formCode
     * @return
     */
    @Override
    public BusinessDataVo buttonsOperation(String params, String formCode) {
        BusinessDataVo vo = buttonPreOperationService.buttonsPreOperation(params, formCode);
        return vo;
    }


    /**
     * processes related record list on the pc
     *
     * @param pageDto
     * @param vo
     * @return
     * @throws AFBizException
     */
    @Override
    public ResultAndPage<TaskMgmtVO> findPcProcessList(PageDto pageDto, TaskMgmtVO vo) throws AFBizException {

        LinkedHashMap<String, SortTypeEnum> orderFieldMap = Maps.newLinkedHashMap();

        // 1、use mybatis-plus's paging plugin,mybatis is a a very popular orm framework,especially in China
        Page<TaskMgmtVO> page = PageUtils.getPageByPageDto(pageDto, orderFieldMap);
        // ===========================>>to build the query conditions<<============================

        vo.setApplyUser(SecurityUtils.getLogInEmpIdStr());
        switch (vo.getType()) {
            // view process record
            case 1:
                // get the records that current logged in user has access right
                //todo to be implemented
                break;
            // mornitor current processes
            case 2:
                page.setRecords(this.getBaseMapper().viewPcProcessList(page, vo));
                break;
            // recently build task
            case 3:
                if (!ObjectUtils.isEmpty(vo.getProcessType())) {
                    vo.setProcessKeyList(bpmnConfMapper.formCodeListByConfId(Long.parseLong(vo.getProcessType())));
                }
                page.setRecords(this.getBaseMapper().viewPcpNewlyBuildList(page, vo));

                break;
            // already finished tasks
            case 4:
                if (!ObjectUtils.isEmpty(vo.getProcessType())) {
                    vo.setProcessKeyList(bpmnConfMapper.formCodeListByConfId(Long.parseLong(vo.getProcessType())));
                }
                page.setRecords(this.getBaseMapper().viewPcAlreadyDoneList(page, vo));

                break;
            // running tasks
            case 5:
                if (!ObjectUtils.isEmpty(vo.getProcessType())) {
                    vo.setProcessKeyList(bpmnConfMapper.formCodeListByConfId(Long.parseLong(vo.getProcessType())));
                }
                page.setRecords(this.getBaseMapper().viewPcToDoList(page, vo));


                break;
            // my draft
            case 6:
                page.setRecords(this.getBaseMapper().allProcessList(page, vo));
                break;
            // processes that back to me
            case 7:
                page.setRecords(this.getBaseMapper().backToModifyList(page, vo));
                break;
            //for administrator to view all the processes
            case 8:
                page.setRecords(this.getBaseMapper().allProcessList(page, vo));
                break;
            //转发流程
            case 9:
                page.setRecords(this.baseMapper.viewPcForwardList(page,vo));
                //todo tobe implemented
                break;
        }
        if (!ObjectUtils.isEmpty(page.getRecords())) {
            if (vo.getType().equals(ProcessTypeEnum.ENTRUST_TYPE.getCode()) || vo.getType().equals(ProcessTypeEnum.ADMIN_TYPE.getCode())) {
                processForwardBizService.loadProcessForward(SecurityUtils.getLogInEmpId());
                processForwardBizService.loadTask(SecurityUtils.getLogInEmpId());
            }
            this.getPcProcessData(page, vo.getType());
        }
        return PageUtils.getResultAndPage(page);
    }

    private void getPcProcessData(Page<TaskMgmtVO> page, Integer type) {
        List<String> formCodes = page.getRecords().stream().map(TaskMgmtVO::getProcessKey).distinct().collect(Collectors.toList());

        List<BpmnConf> bpmnConfs = bpmnConfCommonService.getBpmnConfByFormCodeBatch(formCodes);
        Map<String, BpmnConf> bpmnConfMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(bpmnConfs)) {
            bpmnConfMap = bpmnConfs
                    .stream()
                    .collect(Collectors.toMap(BpmnConf::getFormCode, o -> o, (k1, k2) -> k2));


            for (TaskMgmtVO record : page.getRecords()) {
                BpmnConf bpmnConf = bpmnConfMap.get(record.getProcessKey());
                if(bpmnConf!=null){
                    record.setIsOutSideProcess(Objects.equals(1,bpmnConf.getIsOutSideProcess()));
                    record.setIsLowCodeFlow(Objects.equals(1,bpmnConf.getIsLowCodeFlow()));
                    record.setConfId(bpmnConf.getId());
                }
                Integer applyUserId = record.getApplyUserId();
                //todo get the actual user info from db
                record.setActualName(SecurityUtils.getLogInEmpName());

                // set current record's state
                record.setTaskState(ProcessStateEnum.getDescByCode(record.getProcessState()));

                if (type.equals(ProcessTypeEnum.ENTRUST_TYPE.getCode())) {
                    // to check whether the forwarded record can process in batch
                    record.setIsForward(processForwardBizService.isForward(record.getProcessInstanceId()));
                    if (!ObjectUtils.isEmpty(record.getTaskName())) {
                        record.setIsBatchAgree(this.isBatchOperatable(record.getProcessKey(), record.getTaskName(), bpmnConf));
                        record.setNodeType(ProcessNodeEnum.getCodeByDesc(record.getTaskName()));
                    }
                }
                if (type.equals(ProcessTypeEnum.ADMIN_TYPE.getCode())) {
                    if (!ObjectUtils.isEmpty(record.getTaskName())) {
                        record.setNodeType(ProcessNodeEnum.getCodeByDesc(record.getTaskName()));
                    }
                }
                if (!ObjectUtils.isEmpty(record.getProcessKey())) {
                    BpmProcessVo bpmProcessVo = bpmnConfMapper.getBpmProcessVoByFormCode(record.getProcessKey());
                    if (bpmProcessVo != null && !ObjectUtils.isEmpty(bpmProcessVo.getProcessKey())) {
                        record.setProcessTypeName(bpmProcessVo.getProcessName());
                        record.setProcessCode(bpmProcessVo.getProcessKey());
                    }
                }
            }
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessDataVo getBusinessInfo(String params, String formCode) {
        BusinessDataVo vo = formFactory.dataFormConversion(params,formCode);
        return getBusinessInfo(vo);
    }
    @Override
    public BusinessDataVo getBusinessInfo(BusinessDataVo vo){
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        if(ObjectUtils.isEmpty(bpmBusinessProcess)){
            throw  new AFBizException(String.format("processNumber%s,its data not in existence!",vo.getProcessNumber()));
        }
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());


        if(!vo.getIsOutSideAccessProc()||Objects.equals(vo.getIsLowCodeFlow(),1)){
            FormOperationAdaptor formAdaptor = formFactory.getFormAdaptor(vo);
            formAdaptor.queryData(vo);
        }else{

        }

        //set the businessId
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());

        // set some other important information
        vo.setFormCode(vo.getFormCode());
        vo.setProcessNumber(vo.getProcessNumber());

        // checking process right,and set some information that from business table
        vo.setProcessRecordInfo(businessContans.processInfo(bpmBusinessProcess));
        vo.setProcessKey(bpmBusinessProcess.getBusinessNumber());
        vo.setProcessState(!bpmBusinessProcess.getProcessState().equals(END_STATE.getCode()) && !bpmBusinessProcess.getProcessState().equals(REJECT_STATE.getCode()));

        boolean flag = vo.getProcessRecordInfo().getStartUserId().equals(SecurityUtils.getLogInEmpIdStr());

        boolean isJurisdiction=false;//todo not implemented at the moment
        // set operating buttons

        vo.getProcessRecordInfo().setPcButtons(configFlowButtonContans.getButtons(bpmBusinessProcess.getBusinessNumber(),
                vo.getProcessRecordInfo().getNodeId(),vo.getProcessRecordInfo().getViewNodeIds(), isJurisdiction, flag));


        //check whether current node is a signup node and set the property
        String nodeId = vo.getProcessRecordInfo().getNodeId();
        Boolean nodeIsSignUp = bpmVariableSignUpBizService.checkNodeIsSignUp(vo.getProcessNumber(), nodeId);
        vo.setIsSignUpNode(nodeIsSignUp);
        //add a "choose a verifier" button if it is a signup node
        if (nodeIsSignUp) {
            //set the add approver button
            addApproverButton(vo);
        }
        //上一节点指定审批人:当前节点贴有 appoint_next_node_approver 标签时,渲染[指定下一节点审批人]按钮
        if (hasAppointNextNodeApproverLabel(vo)) {
            addAppointNextNodeApproverButton(vo);
        }
        //选择条件:当前节点贴有 pick_condition 标签时,渲染[选择分支]按钮并返回可选分支列表
        if (hasPickConditionLabel(vo)) {
            addPickConditionButtonAndBranches(vo);
        }
        if(!vo.getIsOutSideAccessProc() && Objects.equals(vo.getIsLowCodeFlow(),0)){
            return vo;
        }
        else if(!vo.getIsOutSideAccessProc()||Objects.equals(vo.getIsLowCodeFlow(),1)){
            if (!(vo instanceof UDLFApplyVo)) {
                return vo;
            }
            UDLFApplyVo udlfApplyVo = (UDLFApplyVo) vo;
            List<LFFieldControlVO> lfFieldControlVOs = udlfApplyVo.getProcessRecordInfo().getLfFieldControlVOs();
            Map<String, Object> lfFields = udlfApplyVo.getLfFields();
            if(!CollectionUtils.isEmpty(lfFields)){
                for (String key : lfFields.keySet()) {
                    if(CollectionUtils.isEmpty(lfFieldControlVOs)){
                        continue;
                    }
                    LFFieldControlVO lfFieldControlVO = lfFieldControlVOs.stream().filter(a -> key.equals(a.getFieldId())).findFirst().orElse(null);
                    if(lfFieldControlVO!=null&& StringConstants.HIDDEN_FIELD_PERMISSION.equals(lfFieldControlVO.getPerm())){
                        lfFields.put(key,null);
                    }
                }
            }
        }
        return vo;
    }
    /**
     * some statics about my tobe done list,my new process,etc regarding today
     */
    @Override
    public TaskMgmtVO processStatistics() {

        // set value
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(SecurityUtils.getLogInEmpIdStr()).list();
        return TaskMgmtVO.builder().todoCount(taskList.size())
                .doneTodayCount(this.getBaseMapper().doneTodayProcess(SecurityUtils.getLogInEmpIdStr()))
                .doneCreateCount(this.getBaseMapper().doneCreateProcess(SecurityUtils.getLogInEmpIdStr()))
                .title(null).build();
    }
    /**
     * set the add approver button
     *
     * @param businessDataVo
     */
    private void addApproverButton(BusinessDataVo businessDataVo) {
        //set the approver button
        ProcessActionButtonVo addApproverButton = ProcessActionButtonVo
                .builder()
                .buttonType(BUTTON_TYPE_JP.getCode())
                .name(BUTTON_TYPE_JP.getDesc())
                .build();

        //set add approver button on the pc
        Map<String, List<ProcessActionButtonVo>> pcButtons = businessDataVo.getProcessRecordInfo().getPcButtons();
        List<ProcessActionButtonVo> pcProcButtons = pcButtons.get(ButtonPageTypeEnum.AUDIT.getName());
        if (!pcProcButtons.stream().anyMatch(a->BUTTON_TYPE_JP.getCode().equals(a.getButtonType()))) {
            pcProcButtons.add(addApproverButton);
        }
        businessDataVo.getProcessRecordInfo().setPcButtons(pcButtons);
    }

    /**
     * 检查当前节点是否需要渲染[指定下一节点审批人]按钮
     * 从 ProcessRecordInfoVo.formKey 读取标签
     */
    private boolean hasAppointNextNodeApproverLabel(BusinessDataVo businessDataVo) {
        try {
            if (businessDataVo == null || businessDataVo.getProcessRecordInfo() == null) {
                return false;
            }
            String formKey = businessDataVo.getProcessRecordInfo().getFormKey();
            if (org.apache.commons.lang3.StringUtils.isEmpty(formKey)) {
                return false;
            }
            NodeExtraInfoDTO extraInfoDTO = com.alibaba.fastjson2.JSON.parseObject(formKey, NodeExtraInfoDTO.class);
            return NodeUtil.nodeLabelContainsAny(extraInfoDTO, StringConstants.AF_SYSLABEL_APPOINT_NEXT_NODE_APPROVER);
        } catch (Exception e) {
            log.warn("hasAppointNextNodeApproverLabel check failed", e);
            return false;
        }
    }

    /**
     * 添加[指定下一节点审批人]按钮
     */
    private void addAppointNextNodeApproverButton(BusinessDataVo businessDataVo) {
        ProcessActionButtonVo button = ProcessActionButtonVo
                .builder()
                .buttonType(BUTTON_TYPE_APPOINT_NEXT_NODE_APPROVER.getCode())
                .name(BUTTON_TYPE_APPOINT_NEXT_NODE_APPROVER.getDesc())
                .build();
        Map<String, List<ProcessActionButtonVo>> pcButtons = businessDataVo.getProcessRecordInfo().getPcButtons();
        List<ProcessActionButtonVo> pcProcButtons = pcButtons.get(ButtonPageTypeEnum.AUDIT.getName());
        if (pcProcButtons != null && !pcProcButtons.stream().anyMatch(a -> BUTTON_TYPE_APPOINT_NEXT_NODE_APPROVER.getCode().equals(a.getButtonType()))) {
            pcProcButtons.add(button);
        }
    }

    /**
     * 检查当前节点是否贴有选择条件标签
     */
    private boolean hasPickConditionLabel(BusinessDataVo businessDataVo) {
        try {
            if (businessDataVo == null || businessDataVo.getProcessRecordInfo() == null) {
                return false;
            }
            String formKey = businessDataVo.getProcessRecordInfo().getFormKey();
            if (org.apache.commons.lang3.StringUtils.isEmpty(formKey)) {
                return false;
            }
            NodeExtraInfoDTO extraInfoDTO = com.alibaba.fastjson2.JSON.parseObject(formKey, NodeExtraInfoDTO.class);
            return NodeUtil.nodeLabelContainsAny(extraInfoDTO, StringConstants.AF_SYSLABEL_PICK_CONDITION);
        } catch (Exception e) {
            log.warn("hasPickConditionLabel check failed", e);
            return false;
        }
    }

    /**
     * 选择条件:添加[选择分支]按钮并查询可选分支列表(排除默认条件分支)
     */
    private void addPickConditionButtonAndBranches(BusinessDataVo businessDataVo) {
        //添加按钮
        ProcessActionButtonVo button = ProcessActionButtonVo
                .builder()
                .buttonType(BUTTON_TYPE_PICK_CONDITION.getCode())
                .name(BUTTON_TYPE_PICK_CONDITION.getDesc())
                .build();
        Map<String, List<ProcessActionButtonVo>> pcButtons = businessDataVo.getProcessRecordInfo().getPcButtons();
        List<ProcessActionButtonVo> pcProcButtons = pcButtons.get(ButtonPageTypeEnum.AUDIT.getName());
        if (pcProcButtons != null && pcProcButtons.stream().noneMatch(a -> BUTTON_TYPE_PICK_CONDITION.getCode().equals(a.getButtonType()))) {
            pcProcButtons.add(button);
        }
        //查询可选分支
        try {
            String elementId = businessDataVo.getProcessRecordInfo().getNodeId();
            BpmnConf bpmnConf = bpmnConfCommonService.getBpmnConfByFormCode(businessDataVo.getFormCode());
            if (bpmnConf == null || elementId == null) return;
            //elementId(taskDefKey)转换为bpmn_node表的node_id(UUID):先通过BpmVariableMultiplayer拿到主键id,再查bpmn_node获取node_id
            String currentNodeId = null;
            List<org.openoa.common.entity.BpmVariableMultiplayer> multiplayers = bpmVariableMultiplayerMapper.isMoreNode(businessDataVo.getProcessNumber(), elementId);
            if (!CollectionUtils.isEmpty(multiplayers) && multiplayers.get(0).getNodeId() != null) {
                BpmnNode currentNode = bpmnNodeService.getById(Long.valueOf(multiplayers.get(0).getNodeId()));
                if (currentNode != null) {
                    currentNodeId = currentNode.getNodeId();
                }
            }
            if (currentNodeId == null) return;
            Long confId = bpmnConf.getId();
            //找到当前审批人节点下级的动态条件网关
            List<BpmnNode> gateways = bpmnNodeService.lambdaQuery()
                    .eq(BpmnNode::getConfId, confId)
                    .eq(BpmnNode::getNodeFrom, currentNodeId)
                    .eq(BpmnNode::getIsDynamicCondition, true)
                    .eq(BpmnNode::getIsDel, 0)
                    .list();
            if (CollectionUtils.isEmpty(gateways)) return;
            String gatewayNodeId = gateways.get(0).getNodeId();
            //找到网关下的条件节点(nodeType=3)
            List<BpmnNode> conditionNodes = bpmnNodeService.lambdaQuery()
                    .eq(BpmnNode::getConfId, confId)
                    .eq(BpmnNode::getNodeFrom, gatewayNodeId)
                    .eq(BpmnNode::getNodeType, NodeTypeEnum.NODE_TYPE_CONDITIONS.getCode())
                    .eq(BpmnNode::getIsDel, 0)
                    .list();
            //过滤默认条件分支,构建可选分支列表
            List<BaseIdTranStruVo> branches = conditionNodes.stream()
                    .filter(node -> !isDefaultConditionNode(node))
                    .map(node -> BaseIdTranStruVo.builder().id(node.getNodeId()).name(node.getNodeName()).build())
                    .collect(Collectors.toList());
            businessDataVo.setPickConditionBranches(branches);
        } catch (Exception e) {
            log.warn("addPickConditionButtonAndBranches query branches failed", e);
        }
    }

    /**
     * 判断条件节点是否为默认条件(解析nodeConfigJson中的conditionsConf)
     */
    private boolean isDefaultConditionNode(BpmnNode node) {
        try {
            if (org.apache.commons.lang3.StringUtils.isEmpty(node.getNodeConfigJson())) return false;
            BpmnNodeConfigJson configJson = JsonConfUtil.parseNodeConfig(node.getNodeConfigJson());
            if (configJson == null || configJson.getConditionsConf() == null) return false;
            List<org.openoa.base.entity.jsonconf.BpmnNodeConditionsConfJson.ConditionGroup> groups = configJson.getConditionsConf().getConditionGroups();
            if (CollectionUtils.isEmpty(groups)) return false;
            return Integer.valueOf(1).equals(groups.get(0).getIsDefault());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * check whether current node allows batch approval
     * Reads batchStatus from t_bpmn_node: 0=prohibited, null/1=allowed(default)
     *
     * @param processKey form code
     * @param taskName   current task definition key (element id)
     * @param bpmnConf   bpmn conf (nullable, will query if null)
     * @return true if batch approval is allowed
     */
    private Boolean isBatchOperatable(String processKey, String taskName, BpmnConf bpmnConf) {
        if (bpmnConf == null) {
            bpmnConf = bpmnConfCommonService.getBpmnConfByFormCode(processKey);
            if (bpmnConf == null || bpmnConf.getBpmnCode() == null) {
                return true;
            }
        }
        List<BpmnNode> nodes = bpmVariableMultiplayerMapper.getNodeByElementId(bpmnConf.getBpmnCode(), taskName);
        if (nodes.isEmpty()) {
            return true;
        }
        BpmnNode node = nodes.get(0);
        // batchStatus: 0=prohibited, null or 1=allowed
        return !Integer.valueOf(0).equals(node.getBatchStatus());
    }
    //todo some process approval access right check
}