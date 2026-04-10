package org.openoa.engine.bpmnconf.adp.processoperation;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.ProcessOperationEnum;
import org.openoa.base.constant.enums.ProcessSubmitStateEnum;
import org.openoa.base.dto.NodeExtraInfoDTO;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.ProcessOperationAdaptor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeLabelVO;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfBizServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnProcessMigrationServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessNodeSubmitBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableSignUpPersonnelBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVerifyAttachmentService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openoa.base.constant.enums.ProcessOperationEnum.*;
import static org.openoa.base.constant.enums.ProcessSubmitStateEnum.PROCESS_SIGN_UP;

/**
 * @Author JimuOffice
 * @Description submit/approve
 * @Date 2022-04-28 15:55
 * @Param
 * @return
 * @Version 1.0
 */
@Component
public class ResubmitProcessImpl implements ProcessOperationAdaptor {
    @Autowired
    private FormFactory formFactory;
    @Autowired
    private BpmBusinessProcessService bpmBusinessProcessService;
    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoBizService;
    @Autowired
    private BpmVerifyAttachmentService bpmVerifyAttachmentService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BpmProcessNodeSubmitBizService processNodeSubmitBizService;

    @Autowired
    private BpmVariableSignUpPersonnelBizService bpmVariableSignUpPersonnelBizService;
    @Autowired
    private BpmnProcessMigrationServiceImpl bpmnProcessMigrationService;
    @Autowired
    private BpmnConfBizServiceImpl bpmnConfCommonService;

    @Autowired
    private BpmnConfBizService bpmnConfBizService;

    private static final LoadingCache<Long, BpmnConfVo> cache = CacheBuilder.newBuilder()
            .maximumSize(1000) // 最大缓存数量
            .expireAfterWrite(24, TimeUnit.HOURS) // 写入后24小时后过期
            .build(new CacheLoader<Long, BpmnConfVo>() {
                @Override
                public BpmnConfVo load(Long key) throws Exception {
                    // 实现自动加载逻辑，例如从数据库加载数据
                    return new BpmnConfVo();
                }
            });

    private BpmnConfVo getBpmnConfVoDetail(Long key) {
        BpmnConfVo bpmnConfVo = null;

        try {
            bpmnConfVo = cache.get(key);
            if(null == bpmnConfVo || null == bpmnConfVo.getId()) {
                bpmnConfVo = this.bpmnConfBizService.detail(key);
                if(null != bpmnConfVo && null != bpmnConfVo.getId()) {
                    cache.put(key, bpmnConfVo);
                }
            }
        } catch (Exception e) {
            return null;
        }

        return bpmnConfVo;
    }

    @Override
    public void doProcessButton(BusinessDataVo vo) {
        vo.setStartUserId(SecurityUtils.getLogInEmpIdStr());
        vo.setStartUserName(SecurityUtils.getLogInEmpName());
        BpmBusinessProcess bpmBusinessProcess = bpmBusinessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        vo.setBusinessId(bpmBusinessProcess.getBusinessId());
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(bpmBusinessProcess.getProcInstId()).list();
        if (ObjectUtils.isEmpty(tasks)) {
            throw new AFBizException("当前流程已审批！");
        }
        if(tasks.stream().noneMatch(a->a.getAssignee().equals(SecurityUtils.getLogInEmpIdStr()))){
            throw new AFBizException("当前流程已审批！");
        }
        Task task;
        if (!ObjectUtils.isEmpty(vo.getTaskId())) {
            task = tasks.stream().filter(o -> o.getId().equals(vo.getTaskId())&&o.getAssignee().equals(SecurityUtils.getLogInEmpIdStr())).findFirst().orElse(null);
        } else {
            task = tasks.get(0);
            if (StringUtils.isEmpty(task.getAssigneeName())) {
                task.setAssigneeName(SecurityUtils.getLogInEmpNameSafe());
            }
        }
        if (ObjectUtils.isEmpty(task)) {
            throw new AFBizException("当前流程代办已审批或不存在！");
        }
        String formKey = task.getFormKey();
        //实际上存的是label信息
        if (!StringUtils.isEmpty(formKey)) {
            NodeExtraInfoDTO extraInfoDTO = JSON.parseObject(formKey, NodeExtraInfoDTO.class);
            List<BpmnNodeLabelVO> nodeLabelVOS = extraInfoDTO.getNodeLabelVOS();
            if (!CollectionUtils.isEmpty(nodeLabelVOS)) {
                for (BpmnNodeLabelVO nodeLabelVO : nodeLabelVOS) {
                    if (StringConstants.DYNAMIC_CONDITION_NODE.equals(nodeLabelVO.getLabelValue())) {
                        if (tasks.size() == 1) {//只有当前节点到最后一个审批人了才执行迁移
                            boolean conditionsChanged = bpmnConfCommonService.migrationCheckConditionsChange(vo);
                            if(conditionsChanged){
                                bpmnProcessMigrationService.migrateAndJumpToCurrent(task, bpmBusinessProcess, vo, this::executeTaskCompletion);
                                bpmVerifyInfoSupplement(vo, task, bpmBusinessProcess);
                                return;
                            }
                        }
                    }
                }
            }
        } else {
            //diy流程是否含有动态条件判断
            if(null != vo.getBpmnConfVo() && null != vo.getBpmnConfVo().getId()) {
                BpmnConfVo bpmnConfVo = this.getBpmnConfVoDetail(vo.getBpmnConfVo().getId());
                if(null != bpmnConfVo && null != bpmnConfVo.getNodes()) {
                    for (BpmnNodeVo nodeVo : bpmnConfVo.getNodes()) {
                        if(nodeVo.getIsDynamicCondition()) {
                            if (tasks.size() == 1) {//只有当前节点到最后一个审批人了才执行迁移
                                boolean conditionsChanged = bpmnConfCommonService.migrationCheckConditionsChange(vo);
                                if(conditionsChanged){
                                    bpmnProcessMigrationService.migrateAndJumpToCurrent(task, bpmBusinessProcess, vo, this::executeTaskCompletion);
                                    bpmVerifyInfoSupplement(vo, task, bpmBusinessProcess);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (ObjectUtils.isEmpty(task)) {
            throw new AFBizException("当前流程代办已审批！");
        }

        executeTaskCompletion(vo, task, bpmBusinessProcess);
        vo.setStartUserId(bpmBusinessProcess.getCreateUser());//这里主要是为了发消息通知使用
    }

    /**
     * 操作记录补充
     * @param vo
     * @param task
     * @param bpmBusinessProcess
     */
    private void bpmVerifyInfoSupplement(BusinessDataVo vo, Task task, BpmBusinessProcess bpmBusinessProcess) {
        //save process verify info
        BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                .builder()
                .verifyDate(new Date())
                .taskName(task.getName())
                .taskId(task.getId())
                .runInfoId(bpmBusinessProcess.getProcInstId())
                .verifyUserId(task.getAssignee())
                .verifyUserName(vo.getStartUserName())
                .taskDefKey(task.getTaskDefinitionKey())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc(ObjectUtils.isEmpty(vo.getApprovalComment()) ? "同意" : vo.getApprovalComment())
                .processCode(vo.getProcessNumber())
                .build();


        if (vo.getOperationType().intValue() == BUTTON_TYPE_JP.getCode().intValue()) {
            bpmVerifyInfo.setVerifyStatus(PROCESS_SIGN_UP.getCode());
            bpmVerifyInfo.setVerifyDesc(ObjectUtils.isEmpty(vo.getApprovalComment()) ? "加批" : vo.getApprovalComment());
        }

        if(!StringConstants.CURRENT_USER_ALREADY_PROCESSED.equals(bpmVerifyInfo.getVerifyDesc())){
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);
        }
    }

    private void executeTaskCompletion(BusinessDataVo vo, Task task, BpmBusinessProcess bpmBusinessProcess) {
        vo.setTaskId(task.getId());
//        BusinessDataVo businessDataVo = formFactory.getFormAdaptor(vo).consentData(vo);

        if (!vo.getIsOutSideAccessProc()) {
            formFactory.getFormAdaptor(vo).consentData(vo);
        }

        //save process verify info
        BpmVerifyInfo bpmVerifyInfo = BpmVerifyInfo
                .builder()
                .verifyDate(new Date())
                .taskName(task.getName())
                .taskId(task.getId())
                .runInfoId(bpmBusinessProcess.getProcInstId())
                .verifyUserId(task.getAssignee())
                .verifyUserName(vo.getStartUserName())
                .taskDefKey(task.getTaskDefinitionKey())
                .verifyStatus(ProcessSubmitStateEnum.PROCESS_AGRESS_TYPE.getCode())
                .verifyDesc(ObjectUtils.isEmpty(vo.getApprovalComment()) ? "同意" : vo.getApprovalComment())
                .processCode(vo.getProcessNumber())
                .build();

        //if process digest is not empty, then update process digest
        if (!ObjectUtils.isEmpty(vo) && !ObjectUtils.isEmpty(vo.getProcessDigest())) {
            bpmBusinessProcessService.update(BpmBusinessProcess
                    .builder()
                    .processDigest(vo.getProcessDigest())
                    .build(), new QueryWrapper<BpmBusinessProcess>()
                    .eq("BUSINESS_NUMBER", vo.getProcessNumber()));
        }

        if (vo.getOperationType().intValue() == BUTTON_TYPE_JP.getCode().intValue()) {
            bpmVerifyInfo.setVerifyStatus(PROCESS_SIGN_UP.getCode());
            bpmVerifyInfo.setVerifyDesc(ObjectUtils.isEmpty(vo.getApprovalComment()) ? "加批" : vo.getApprovalComment());
        }
        if(!StringConstants.CURRENT_USER_ALREADY_PROCESSED.equals(bpmVerifyInfo.getVerifyDesc())){
            bpmVerifyInfoBizService.addVerifyInfo(bpmVerifyInfo);

            bpmVerifyAttachmentService.addVerifyAttachmentBatch(vo.getVerifyAttachments(), bpmVerifyInfo.getId());
        }


        //process node sign up
        if (!ObjectUtils.isEmpty(vo.getOperationType()) && vo.getOperationType().intValue() == BUTTON_TYPE_JP.getCode()) {
            bpmVariableSignUpPersonnelBizService.insertSignUpPersonnel(taskService, task.getId(), vo.getProcessNumber(), task.getTaskDefinitionKey(), task.getAssignee(), vo.getSignUpUsers());
        }
        //submit process
        processNodeSubmitBizService.processComplete(task);
    }

    @Override
    public void setSupportBusinessObjects() {
        addSupportBusinessObjects(
                BUTTON_TYPE_RESUBMIT,
                BUTTON_TYPE_AGREE,
                BUTTON_TYPE_JP
        );
        addSupportBusinessObjects(ProcessOperationEnum.getOutSideAccessmarker(), BUTTON_TYPE_RESUBMIT,
                BUTTON_TYPE_AGREE,
                BUTTON_TYPE_JP);
    }
}
