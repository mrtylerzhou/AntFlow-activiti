package org.openoa.engine.bpmnconf.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.mapper.UserMapper;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.base.entity.Employee;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmAccessBusiness;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmBusinessParty;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmConditionsTemplate;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmAccessBusinessMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmVerifyInfoBizServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfCommonServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.ButtonOperationServiceImpl;
import org.openoa.engine.vo.OutSideBpmAccessBusinessVo;
import org.openoa.engine.vo.OutSideBpmAccessProcessRecordVo;
import org.openoa.engine.vo.OutSideBpmAccessRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;



/**
 * third party process service,access service
 * @since 0.5
 */
@Service
public class OutSideBpmAccessBusinessServiceImpl extends ServiceImpl<OutSideBpmAccessBusinessMapper, OutSideBpmAccessBusiness> {

    @Autowired
    private OutSideBpmAccessBusinessMapper outSideBpmAccessBusinessMapper;

    @Autowired
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;

    @Autowired
    private ButtonOperationServiceImpl processApprovalService;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BpmVerifyInfoBizServiceImpl bpmVerifyInfoNewService;

    @Autowired
    private BpmnConfCommonServiceImpl bpmnConfCommonService;
    @Autowired
    private BpmnConfServiceImpl bpmnConfService;

    @Autowired
    private OutSideBpmConditionsTemplateServiceImpl outSideBpmConditionsTemplateService;
    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;
    /**
     * third party process start
     *
     * @param vo
     */
    public OutSideBpmAccessRespVo accessBusinessStart(OutSideBpmAccessBusinessVo vo) {

        OutSideBpmAccessBusiness outSideBpmAccessBusiness = this.getBaseMapper().selectById(vo.getId());

        if (outSideBpmAccessBusiness!=null) {
            BeanUtils.copyProperties(vo, outSideBpmAccessBusiness);
            outSideBpmAccessBusiness.setUpdateUser(SecurityUtils.getLogInEmpId());
            outSideBpmAccessBusiness.setUpdateTime(new Date());
            this.updateById(outSideBpmAccessBusiness);
        } else {


            //query business party by business party mark
            OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getBaseMapper().selectOne(new QueryWrapper<OutSideBpmBusinessParty>()
                    .eq("business_party_mark", vo.getBusinessPartyMark()));

            if(outSideBpmBusinessParty==null){
                throw new JiMuBizException("业务方信息不存在!");
            }
            String formCode = vo.getFormCode();
            Wrapper<BpmnConf> qryWrapper=new QueryWrapper<BpmnConf>()
                    .eq("form_code",formCode)
                    .eq("effective_status",1)
                    .eq("is_del",0);
            BpmnConf effectiveConfByFormCode = bpmnConfService.getOne(qryWrapper);
            if(effectiveConfByFormCode==null){
                throw new JiMuBizException(String.format("未能根据流程编号%s找到有效的流程配置,请检查同业入参",formCode));
            }
            vo.setBpmnConfId(effectiveConfByFormCode.getId());
            outSideBpmAccessBusiness = new OutSideBpmAccessBusiness();
            BeanUtils.copyProperties(vo, outSideBpmAccessBusiness);
            //set business party's id
            outSideBpmAccessBusiness.setBusinessPartyId(outSideBpmBusinessParty.getId());
            outSideBpmAccessBusiness.setCreateUser(SecurityUtils.getLogInEmpIdSafe());
            outSideBpmAccessBusiness.setCreateTime(new Date());
            outSideBpmAccessBusiness.setUpdateUser(SecurityUtils.getLogInEmpIdSafe());
            outSideBpmAccessBusiness.setUpdateTime(new Date());
            this.save(outSideBpmAccessBusiness);
        }



        //set form code,business,etc
        BusinessDataVo businessDataVo = new BusinessDataVo();
        businessDataVo.setFormCode(vo.getFormCode());
        businessDataVo.setOperationType(ButtonTypeEnum.BUTTON_TYPE_SUBMIT.getCode());
        businessDataVo.setBusinessId(outSideBpmAccessBusiness.getId().toString());
        businessDataVo.setOutSideType(vo.getOutSideType());


        //to check whether start user id empty
        if (StringUtil.isEmpty(vo.getUserId())) {
            throw new JiMuBizException("发起人用户名为空，无法发起流程！");
        }
//        Employee employee = getEmployeeByUserId(vo.getUserId());
//
//        if (employee==null) {
//            throw new JiMuBizException("发起人不合法，无法发起流程");
//        }

        //set start user id
        businessDataVo.setStartUserId(SecurityUtils.getLogInEmpIdSafe());
        businessDataVo.setStartUserName(SecurityUtils.getLogInEmpNameSafe());

        //set approval
//        if (!StringUtil.isEmpty(vo.getApprovalUsername())) {
////            Employee approvalEmployee = getEmployeeByUserId(vo.getApprovalUsername());
////            if(approvalEmployee!=null){
////                String id = approvalEmployee.getId();
////                if(!StringUtils.isEmpty(id)){
////                    businessDataVo.setEmplId(id);
////                }
////            }
//
//            businessDataVo.setEmpId(vo.getUserId());
//            businessDataVo.setSubmitUser(SecurityUtils.getLogInEmpNameSafe());
//
//        }
        businessDataVo.setEmpId(SecurityUtils.getLogInEmpIdSafe());
        businessDataVo.setSubmitUser(SecurityUtils.getLogInEmpNameSafe());

        //set embed nodes
        businessDataVo.setEmbedNodes(reSetEmbedNodes(vo.getEmbedNodes()));
        //not implemented at the moment
        //businessDataVo.setOutSideLevelNodes(reFormatOutsideLevelNodes(vo.getOutSideLevelNodes()));
        //call common service to start the process
        processApprovalService.buttonsOperationTransactional(businessDataVo);

        //query inserted data
        OutSideBpmAccessBusiness outSideBpmAccessBusinessResult = this.getBaseMapper().selectById(outSideBpmAccessBusiness.getId());

        //return to caller
        return OutSideBpmAccessRespVo
                .builder()
                .processNumber(outSideBpmAccessBusinessResult.getProcessNumber())
                .businessId(outSideBpmAccessBusinessResult.getId().toString())
                .processRecord(getProcessRecord(outSideBpmAccessBusinessResult.getProcessNumber()))
                .build();

    }

    /**
     * get employee by user id
     *
     * @return
     */
    private Employee getEmployeeByUserId(String userName) {
        Map<String, String> stringStringMap = bpmnEmployeeInfoProviderService.provideEmployeeInfo(Lists.newArrayList(userName));
        if(stringStringMap.isEmpty()){
            return null;
        }
        Employee employee=new Employee();
        employee.setId(userName);
        employee.setUsername(stringStringMap.get(userName));
        return employee;
    }

    /**
     * get process access record
     *
     * @param processNumber
     * @return
     */
    public List<OutSideBpmAccessProcessRecordVo> outSideProcessRecord(String processNumber) {
        return getProcessRecord(processNumber);
    }

    /**
     * get process record
     *
     * @param processNumber
     * @return
     */
    private List<OutSideBpmAccessProcessRecordVo> getProcessRecord(String processNumber) {
        List<BpmVerifyInfoVo> bpmVerifyInfoVos = bpmVerifyInfoNewService.getBpmVerifyInfoVos(processNumber, false);

        return bpmVerifyInfoVos
                .stream()
                .map(o -> OutSideBpmAccessProcessRecordVo
                        .builder()
                        .nodeName(o.getTaskName())
                        .approvalTime(Optional.ofNullable(o.getVerifyDate())
                                .map(DateUtil.SDF_DATETIME_PATTERN::format)
                                .orElse(StringUtils.EMPTY))
                        .approvalStatus(o.getVerifyStatus())
                        .approvalStatusName(o.getVerifyStatusName())
                        .approvalUserName(o.getVerifyUserName())
                        .approvalUserId(o.getVerifyUserId()==null?StringUtils.join(o.getVerifyUserIds(),","):o.getVerifyUserId())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * preview
     *
     * @param vo
     * @return
     */
    public List<OutSideBpmAccessProcessRecordVo> accessBusinessPreview(OutSideBpmAccessBusinessVo vo) {


        //check whether business party mark is empty
        if (StringUtil.isEmpty(vo.getBusinessPartyMark())) {
            throw new JiMuBizException("业务方标识为空，无法发起流程！");
        }


        //get business party
        OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getOne(new QueryWrapper<OutSideBpmBusinessParty>()
                .eq("business_party_mark", vo.getBusinessPartyMark()));


        //if the record does not exist,then the mark is invalid
        if (outSideBpmBusinessParty==null) {
            throw new JiMuBizException("业务方不合法，无法预览流程");
        }

        //校验发起人是否为空
        if (StringUtil.isEmpty(vo.getUserId())) {
            throw new JiMuBizException("发起人用户名为空，无法预览流程！");
        }


        Employee employee = !StringUtils.isEmpty(vo.getUserName())?Employee.builder().id(vo.getUserId()).username(vo.getUserName()).build():getEmployeeByUserId(vo.getUserId());


        if (employee==null) {
            throw new JiMuBizException("发起人不合法，无法预览流程");
        }
        //query condition template
        OutSideBpmConditionsTemplate outSideBpmConditionsTemplate=null;
        if(!StringUtils.isEmpty(vo.getTemplateMark())){
            outSideBpmConditionsTemplate= outSideBpmConditionsTemplateService.getOne(new QueryWrapper<OutSideBpmConditionsTemplate>()
                    .eq("is_del", 0)
                    .eq("business_party_id", outSideBpmBusinessParty.getId())
                    .eq("template_mark", vo.getTemplateMark()));


            if (outSideBpmConditionsTemplate==null) {
                throw new JiMuBizException("模板信息不合法，无法预览流程");
            }
        }


        BusinessDataVo dataVo = BusinessDataVo
                .builder()
                .isOutSideAccessProc(true)
                .formCode(vo.getFormCode())
                .startUserId(employee.getId())
                .startUserName(employee.getUsername())
                .templateMark(vo.getTemplateMark())
                .embedNodes(reSetEmbedNodes(vo.getEmbedNodes()))
                .build();
        if(outSideBpmConditionsTemplate!=null&&outSideBpmConditionsTemplate.getId()!=null){
            dataVo.setTemplateMarkId(outSideBpmConditionsTemplate.getId().intValue());
        }

        PreviewNode previewNode = bpmnConfCommonService.startPagePreviewNode(JSON.toJSONString(dataVo));

        if (!CollectionUtils.isEmpty(previewNode.getBpmnNodeList())) {
            List<OutSideBpmAccessProcessRecordVo> previewList = Lists.newArrayList();
            BpmnNodeVo bpmnNodeVo = previewNode.getBpmnNodeList().stream().filter(o -> o.getNodeType().equals(1)).findFirst().orElse(null);
            if (bpmnNodeVo==null) {
                return Collections.EMPTY_LIST;
            }
            setPreviewList(previewNode.getBpmnNodeList(), previewList, bpmnNodeVo);
            return previewList;
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * reset embed node
     *
     * @param embedNodes
     * @return
     */
    private List<OutSideBpmAccessEmbedNodeVo> reSetEmbedNodes(List<OutSideBpmAccessEmbedNodeVo> embedNodes) {

        if (CollectionUtils.isEmpty(embedNodes)) {
            return embedNodes;
        }
        return embedNodes;
    }

    /**
     * assemble preview node list
     *
     * @param bpmnNodeVos
     * @param previewList
     * @param bpmnNodeVo
     */
    private void setPreviewList(List<BpmnNodeVo> bpmnNodeVos, List<OutSideBpmAccessProcessRecordVo> previewList, BpmnNodeVo bpmnNodeVo) {

        BpmnNodeParamsVo params = bpmnNodeVo.getParams();

        Integer paramType = params.getParamType();

        String approvalUserName = StringUtils.EMPTY;

        String nodeName = StringUtils.EMPTY;

        if (paramType.equals(1)) {
            BpmnNodeParamsAssigneeVo assignee = params.getAssignee();
            approvalUserName = assignee.getAssigneeName();
            nodeName = assignee.getElementName();
        } else {
            List<BpmnNodeParamsAssigneeVo> assigneeList = params.getAssigneeList();
            if (!CollectionUtils.isEmpty(assigneeList)) {
                approvalUserName = StringUtils.join(assigneeList
                        .stream()
                        .map(BpmnNodeParamsAssigneeVo::getAssigneeName)
                        .collect(Collectors.toList()), ",");
                nodeName = assigneeList.get(0).getElementName();
            }
        }

        previewList.add(OutSideBpmAccessProcessRecordVo
                .builder()
                .nodeName(nodeName)
                .approvalUserName(approvalUserName)
                .build());

        if (params.getNodeTo()!=null) {
            BpmnNodeVo nextBpmnNodeVo = bpmnNodeVos
                    .stream()
                    .filter(o -> o.getNodeId().equals(params.getNodeTo()))
                    .findFirst()
                    .orElse(null);
            if (nextBpmnNodeVo!=null) {
                setPreviewList(bpmnNodeVos, previewList, nextBpmnNodeVo);
            }
        }
    }

    /**
     * stop a process
     *
     * @param vo
     */
    public void processBreak(OutSideBpmAccessBusinessVo vo) {

        if (StringUtil.isEmpty(vo.getFormCode())) {
            throw new JiMuBizException("表单编号为空，无法终止流程");
        }

        if (StringUtil.isEmpty(vo.getProcessNumber())) {
            throw new JiMuBizException("流程编号为空，无法终止流程");
        }


        //build business data
        BusinessDataVo businessDataVo = new BusinessDataVo();
        businessDataVo.setFormCode(vo.getFormCode());
        businessDataVo.setProcessNumber(vo.getProcessNumber());
        businessDataVo.setOperationType(ButtonTypeEnum.BUTTON_TYPE_ABANDONED.getCode());
        businessDataVo.setApprovalComment(vo.getProcessBreakDesc());
        businessDataVo.setIsOutSideAccessProc(true);
        businessDataVo.setOutSideType(1);


        if (!StringUtil.isEmpty(vo.getProcessBreakUserId())) {
            Map<String, Object> objectMap = Maps.newHashMap();
            vo.setUserId(vo.getProcessBreakUserId());
            Employee employee = getEmployeeByUserId(vo.getUserId());
            if (employee!=null) {
                objectMap.put("employeeId", employee.getId());
                objectMap.put("employeeName", employee.getUsername());
                businessDataVo.setObjectMap(objectMap);
                businessDataVo.setStartUserId(employee.getId().toString());
            }
        }

        //call common service to stop the process
        processApprovalService.buttonsOperationTransactional(businessDataVo);

    }

}
