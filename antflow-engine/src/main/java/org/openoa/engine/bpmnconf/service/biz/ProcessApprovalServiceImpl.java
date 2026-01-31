package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.*;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.common.ConfigFlowButtonContans;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.mapper.ProcessApprovalMapper;
import org.openoa.engine.bpmnconf.mapper.TaskMgmtMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessForwardBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVariableSignUpBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.ProcessApprovalService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNameRelevancyService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessNameService;
import org.openoa.engine.factory.ButtonPreOperationService;
import org.openoa.engine.factory.FormFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.ProcessOperationEnum.BUTTON_TYPE_JP;
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
    private BpmProcessNameRelevancyService processNameRelevancyService;
    @Autowired
    private TaskMgmtMapper taskMgmtMapper;
    @Autowired
    private BpmnConfBizService bpmnConfCommonService;
    @Autowired
    private BpmProcessForwardBizService processForwardBizService;
    @Autowired
    private BpmProcessNameService bpmProcessNameService;
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
                    vo.setProcessKeyList(processNameRelevancyService.processKeyList(Long.parseLong(vo.getProcessType())));
                }
                page.setRecords(this.getBaseMapper().viewPcpNewlyBuildList(page, vo));

                break;
            // already finished tasks
            case 4:
                if (!ObjectUtils.isEmpty(vo.getProcessType())) {
                    vo.setProcessKeyList(processNameRelevancyService.processKeyList(Long.parseLong(vo.getProcessType())));
                }
                page.setRecords(this.getBaseMapper().viewPcAlreadyDoneList(page, vo));

                break;
            // running tasks
            case 5:
                if (!ObjectUtils.isEmpty(vo.getProcessType())) {
                    vo.setProcessKeyList(processNameRelevancyService.processKeyList(Long.parseLong(vo.getProcessType())));
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
                        record.setIsBatchSubmit(this.isOperatable(TaskMgmtVO.builder().processKey(record.getProcessKey())
                                .taskName(record.getTaskName()).type(ProcessButtonEnum.VIEW_TYPE.getCode()).build()));
                        record.setNodeType(ProcessNodeEnum.getCodeByDesc(record.getTaskName()));
                    }
                }
                if (type.equals(ProcessTypeEnum.ADMIN_TYPE.getCode())) {
                    if (!ObjectUtils.isEmpty(record.getTaskName())) {
                        record.setNodeType(ProcessNodeEnum.getCodeByDesc(record.getTaskName()));
                    }
                }
                if (!ObjectUtils.isEmpty(record.getProcessKey())) {
                    BpmProcessVo bpmProcessVo = bpmProcessNameService.get(record.getProcessKey());
                    if (!ObjectUtils.isEmpty(bpmProcessVo.getProcessKey())) {
                        record.setProcessTypeName(bpmProcessVo.getProcessName());
                        record.setProcessCode(bpmProcessVo.getProcessKey());
                    }
                }
            }
        }
    }
    @Override
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
     * check whether current node is operatable
     *
     * @param vo
     * @return
     */
    private Boolean isOperatable(TaskMgmtVO vo) {
        return this.getBaseMapper().isOperational(vo) <= 0;

    }
    //todo some process approval access right check
}