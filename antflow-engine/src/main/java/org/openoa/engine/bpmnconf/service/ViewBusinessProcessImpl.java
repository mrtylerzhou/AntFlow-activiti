package org.openoa.engine.bpmnconf.service;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ButtonPageTypeEnum;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.util.NodeUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.ProcessActionButtonVo;
import org.openoa.engine.bpmnconf.common.ConfigFlowButtonContans;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableSignUpBizService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

import static org.openoa.base.constant.enums.ProcessOperationEnum.BUTTON_TYPE_JP;
import static org.openoa.base.constant.enums.ProcessOperationEnum.BUTTON_TYPE_VIEW_BUSINESS_PROCESS;
import static org.openoa.base.constant.enums.ProcessOperationEnum.BUTTON_TYPE_APPOINT_NEXT_NODE_APPROVER;
import static org.openoa.base.constant.enums.ProcessStateEnum.END_STATE;
import static org.openoa.base.constant.enums.ProcessStateEnum.REJECT_STATE;

@Slf4j
@Service
public class ViewBusinessProcessImpl  implements ProcessOperationAdaptor {
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private ProcessBusinessContans businessContans;
    @Autowired
    private ConfigFlowButtonContans configFlowButtonContans;
    @Autowired
    private BpmVariableSignUpBizService variableSignUpBizService;
    @Autowired
    private TaskService taskService;

    @Override
    public void doProcessButton(BusinessDataVo businessDataVo) {
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(businessDataVo.getProcessNumber());
        if(ObjectUtils.isEmpty(bpmBusinessProcess)){
            throw  new AFBizException(String.format("processNumber%s,its data not in existence!",businessDataVo.getProcessNumber()));
        }
        businessDataVo.setBusinessId(bpmBusinessProcess.getBusinessId());
        formFactory.getFormAdaptor(businessDataVo).queryData(businessDataVo);

        //set the businessId
        businessDataVo.setBusinessId(bpmBusinessProcess.getBusinessId());



        // checking process right,and set some information that from business table
        businessDataVo.setProcessRecordInfo(businessContans.processInfo(bpmBusinessProcess));
        businessDataVo.setProcessKey(bpmBusinessProcess.getBusinessNumber());
        businessDataVo.setProcessState(!bpmBusinessProcess.getProcessState().equals(END_STATE.getCode()) && !bpmBusinessProcess.getProcessState().equals(REJECT_STATE.getCode()));

        boolean flag = businessDataVo.getProcessRecordInfo().getStartUserId().equals(SecurityUtils.getLogInEmpIdStr());

        boolean isJurisdiction=false;//todo not implemented at the moment
        // set operating buttons

        businessDataVo.getProcessRecordInfo().setPcButtons(configFlowButtonContans.getButtons(bpmBusinessProcess.getBusinessNumber(),
                businessDataVo.getProcessRecordInfo().getNodeId(),businessDataVo.getProcessRecordInfo().getViewNodeIds(), isJurisdiction, flag));


        //check whether current node is a signup node and set the property
        String nodeId = businessDataVo.getProcessRecordInfo().getNodeId();
        Boolean nodeIsSignUp = variableSignUpBizService.checkNodeIsSignUp(businessDataVo.getProcessNumber(), nodeId);
        businessDataVo.setIsSignUpNode(nodeIsSignUp);
        //add a "choose a verifier" button if it is a signup node
        if (nodeIsSignUp) {
            //set the add approver button
            addApproverButton(businessDataVo);
        }
        //上一节点指定审批人:当前节点贴有 appoint_next_node_approver 标签时,渲染[指定下一节点审批人]按钮
        //注意: 该标签由后端 AbstractBpmnPersonnelAdaptor.setNodeParams 在流程发起时动态贴到上一节点(in-memory),
        //不会持久化到 t_bpmn_node.node_config_json, 因此不能用 NodeUtil.hasLabel(读 DB) 检查,
        //而是从当前 Activiti 任务的 formKey(=NodeExtraInfoDTO JSON, 包含运行时 labelList) 读取
        if (hasAppointNextNodeApproverLabel(businessDataVo)) {
            addAppointNextNodeApproverButton(businessDataVo);
        }

    }

    /**
     * 从当前 Activiti 任务的 formKey(=NodeExtraInfoDTO JSON) 中检查是否包含 appoint_next_node_approver 标签
     * 该标签由 AbstractBpmnPersonnelAdaptor.setNodeParams 在流程发起时动态贴到上一节点,
     * 通过 BpmnTaskListener 写入 delegateTask.formKey, 不持久化到 t_bpmn_node
     */
    private boolean hasAppointNextNodeApproverLabel(BusinessDataVo businessDataVo) {
        try {
            String taskId = businessDataVo.getProcessRecordInfo().getTaskId();
            if (StringUtils.isEmpty(taskId)) {
                return false;
            }
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                return false;
            }
            String formKey = task.getFormKey();
            if (StringUtils.isEmpty(formKey)) {
                return false;
            }
            NodeExtraInfoDTO extraInfoDTO = JSON.parseObject(formKey, NodeExtraInfoDTO.class);
            return NodeUtil.nodeLabelContainsAny(extraInfoDTO, StringConstants.AF_SYSLABEL_APPOINT_NEXT_NODE_APPROVER);
        } catch (Exception e) {
            log.warn("hasAppointNextNodeApproverLabel check failed, processNumber={}", businessDataVo.getProcessNumber(), e);
            return false;
        }
    }

    /**
     * 添加[指定下一节点审批人]按钮
     * 用户点击后弹出单选人员选择器,选中后存入 BusinessDataVo.nextNodeApprovers
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
    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(
                BUTTON_TYPE_VIEW_BUSINESS_PROCESS
        );
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(),  BUTTON_TYPE_VIEW_BUSINESS_PROCESS);
    }
}
