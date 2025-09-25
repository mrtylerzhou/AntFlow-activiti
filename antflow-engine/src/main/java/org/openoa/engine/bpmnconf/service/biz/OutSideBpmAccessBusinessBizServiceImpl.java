package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.ButtonTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.*;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.mapper.UserMapper;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmAccessBusinessMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmVerifyInfoBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.OutSideBpmAccessBusinessBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmBusinessPartyService;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmConditionsTemplateService;
import org.openoa.engine.lowflow.vo.UDLFApplyVo;
import org.openoa.engine.vo.OutSideBpmAccessBusinessVo;
import org.openoa.engine.vo.OutSideBpmAccessProcessRecordVo;
import org.openoa.engine.vo.OutSideBpmAccessRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.NumberConstants.BPMN_FLOW_TYPE_OUTSIDE;

@Service
public class OutSideBpmAccessBusinessBizServiceImpl implements OutSideBpmAccessBusinessBizService {
    @Autowired
    private OutSideBpmAccessBusinessMapper outSideBpmAccessBusinessMapper;

    @Autowired
    private OutSideBpmBusinessPartyService outSideBpmBusinessPartyService;

    @Autowired
    private ButtonOperationServiceImpl processApprovalService;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BpmVerifyInfoBizService bpmVerifyInfoNewService;

    @Autowired
    @Lazy
    private BpmnConfBizService bpmnConfCommonService;
    @Autowired
    private BpmnConfService bpmnConfService;

    @Autowired
    private OutSideBpmConditionsTemplateService outSideBpmConditionsTemplateService;
    @Autowired
    private BpmnEmployeeInfoProviderService bpmnEmployeeInfoProviderService;

    /**
     * third party process start
     *
     * @param vo
     */
    @Override
    public OutSideBpmAccessRespVo accessBusinessStart(OutSideBpmAccessBusinessVo vo) {

        OutSideBpmAccessBusiness outSideBpmAccessBusiness = this.getMapper().selectById(vo.getId());
        if (outSideBpmAccessBusiness!=null) {
            BeanUtils.copyProperties(vo, outSideBpmAccessBusiness);
            outSideBpmAccessBusiness.setUpdateUser(SecurityUtils.getLogInEmpId());
            outSideBpmAccessBusiness.setUpdateTime(new Date());
            this.getService().updateById(outSideBpmAccessBusiness);
        } else {
            String formCode = vo.getFormCode();
            Wrapper<BpmnConf> qryWrapper=new QueryWrapper<BpmnConf>()
                    .eq("form_code",formCode)
                    .eq("effective_status",1)
                    .eq("is_del",0);
            BpmnConf effectiveConfByFormCode = bpmnConfService.getOne(qryWrapper);
            if(effectiveConfByFormCode==null){
                throw new AFBizException(String.format("未能根据流程编号%s找到有效的流程配置,请检查同业入参",formCode));
            }
            vo.setBpmnConfId(effectiveConfByFormCode.getId());
            outSideBpmAccessBusiness = new OutSideBpmAccessBusiness();
            BeanUtils.copyProperties(vo, outSideBpmAccessBusiness);
            if(!CollectionUtils.isEmpty(vo.getTemplateMarks())){
                String templateMarksJoin = String.join(",", vo.getTemplateMarks());
                outSideBpmAccessBusiness.setTemplateMark(templateMarksJoin);
            }
            //set business party's id
            outSideBpmAccessBusiness.setBusinessPartyId(effectiveConfByFormCode.getBusinessPartyId());
            outSideBpmAccessBusiness.setCreateUser(SecurityUtils.getLogInEmpIdSafe());
            outSideBpmAccessBusiness.setCreateTime(new Date());
            outSideBpmAccessBusiness.setUpdateUser(SecurityUtils.getLogInEmpIdSafe());
            outSideBpmAccessBusiness.setUpdateTime(new Date());
            this.getService().save(outSideBpmAccessBusiness);
        }
        //set form code,business,etc
        BusinessDataVo businessDataVo = new BusinessDataVo();
        if(vo.getIsLowCodeFlow()!=null&&Boolean.TRUE.equals(vo.getIsLowCodeFlow())){
            businessDataVo=new UDLFApplyVo();
            ((UDLFApplyVo)businessDataVo).setLfFields(vo.getLfFields());
        }
        businessDataVo.setFormCode(vo.getFormCode());
        businessDataVo.setOperationType(ButtonTypeEnum.BUTTON_TYPE_SUBMIT.getCode());
        businessDataVo.setBusinessId(outSideBpmAccessBusiness.getId().toString());
        businessDataVo.setOutSideType(BPMN_FLOW_TYPE_OUTSIDE);
        businessDataVo.setApproversList(vo.getApproversList());
        businessDataVo.setIsLowCodeFlow(Boolean.TRUE.equals(vo.getIsLowCodeFlow())?1:0);
        businessDataVo.setApprovalEmpls(vo.getApprovalEmpls());

        //to check whether start user id empty
        if (ObjectUtils.isEmpty(vo.getUserId())) {
            throw new AFBizException("发起人用户名为空，无法发起流程！");
        }

        //set start user id
        businessDataVo.setStartUserId(SecurityUtils.getLogInEmpIdSafe());
        businessDataVo.setStartUserName(SecurityUtils.getLogInEmpNameSafe());

        businessDataVo.setEmpId(SecurityUtils.getLogInEmpIdSafe());
        businessDataVo.setSubmitUser(SecurityUtils.getLogInEmpNameSafe());
        //set embed nodes
        businessDataVo.setEmbedNodes(reSetEmbedNodes(vo.getEmbedNodes()));
        //call common service to start the process
        processApprovalService.buttonsOperationTransactional(businessDataVo);
        //query inserted data
        OutSideBpmAccessBusiness outSideBpmAccessBusinessResult = this.getMapper().selectById(outSideBpmAccessBusiness.getId());

        //return to caller
        return OutSideBpmAccessRespVo
                .builder()
                .processNumber(outSideBpmAccessBusinessResult.getProcessNumber())
                .businessId(outSideBpmAccessBusinessResult.getId().toString())
                .processRecord(getProcessRecord(outSideBpmAccessBusinessResult.getProcessNumber()))
                .build();

    }
    /**
     * 获取OutSide FormCode Page List 模板列表使用
     * @param pageDto
     * @param vo
     * @return
     */
    @Override
    public ResultAndPage<BpmnConfVo> selectOutSideFormCodePageList(PageDto pageDto, BpmnConfVo vo) {
        Page<BpmnConfVo> page = PageUtils.getPageByPageDto(pageDto);
        List<BpmnConfVo> bpmnConfVos = bpmnConfService.getMapper().selectOutSideFormCodeList(page, vo);
        if (bpmnConfVos==null) {
            return PageUtils.getResultAndPage(page);
        }
        page.setRecords(bpmnConfVos);
        return PageUtils.getResultAndPage(page);
    }



    /**
     * get process access record
     *
     * @param processNumber
     * @return
     */
    @Override
    public List<OutSideBpmAccessProcessRecordVo> outSideProcessRecord(String processNumber) {
        return getProcessRecord(processNumber);
    }


    /**
     * preview
     *
     * @param vo
     * @return
     */
    @Override
    public List<OutSideBpmAccessProcessRecordVo> accessBusinessPreview(OutSideBpmAccessBusinessVo vo) {


        //check whether business party mark is empty
        if (ObjectUtils.isEmpty(vo.getBusinessPartyMark())) {
            throw new AFBizException("业务方标识为空，无法发起流程！");
        }


        //get business party
        OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getOne(new QueryWrapper<OutSideBpmBusinessParty>()
                .eq("business_party_mark", vo.getBusinessPartyMark()));


        //if the record does not exist,then the mark is invalid
        if (outSideBpmBusinessParty==null) {
            throw new AFBizException("业务方不合法，无法预览流程");
        }

        //校验发起人是否为空
        if (ObjectUtils.isEmpty(vo.getUserId())) {
            throw new AFBizException("发起人用户名为空，无法预览流程！");
        }


        DetailedUser detailedUser = !StringUtils.isEmpty(vo.getUserName())? DetailedUser.builder().id(vo.getUserId()).username(vo.getUserName()).build():getEmployeeByUserId(vo.getUserId());


        if (detailedUser ==null) {
            throw new AFBizException("发起人不合法，无法预览流程");
        }
        //query condition template
        List<OutSideBpmConditionsTemplate> outSideBpmConditionsTemplates=null;
        if(!CollectionUtils.isEmpty(vo.getTemplateMarks())){
            outSideBpmConditionsTemplates= outSideBpmConditionsTemplateService.list(new QueryWrapper<OutSideBpmConditionsTemplate>()
                    .eq("is_del", 0)
                    .eq("business_party_id", outSideBpmBusinessParty.getId())
                    .in("template_mark", vo.getTemplateMarks()));


            if (outSideBpmConditionsTemplates==null) {
                throw new AFBizException("模板信息不合法，无法预览流程");
            }
        }


        BusinessDataVo dataVo = BusinessDataVo
                .builder()
                .isOutSideAccessProc(true)
                .formCode(vo.getFormCode())
                .startUserId(detailedUser.getId())
                .startUserName(detailedUser.getUsername())
                .templateMarks(vo.getTemplateMarks())
                .embedNodes(reSetEmbedNodes(vo.getEmbedNodes()))
                .build();

        if(!CollectionUtils.isEmpty(outSideBpmConditionsTemplates)){
            List<Integer> templateIds = outSideBpmConditionsTemplates.stream().filter(a -> a.getId() != null).map(a->a.getId().intValue()).collect(Collectors.toList());

            dataVo.setTemplateMarkIds(templateIds);
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
     * stop a process
     *
     * @param vo
     */
    @Override
    public void processBreak(OutSideBpmAccessBusinessVo vo) {

        if (ObjectUtils.isEmpty(vo.getFormCode())) {
            throw new AFBizException("表单编号为空，无法终止流程");
        }

        if (ObjectUtils.isEmpty(vo.getProcessNumber())) {
            throw new AFBizException("流程编号为空，无法终止流程");
        }


        //build business data
        BusinessDataVo businessDataVo = new BusinessDataVo();
        businessDataVo.setFormCode(vo.getFormCode());
        businessDataVo.setProcessNumber(vo.getProcessNumber());
        businessDataVo.setOperationType(ButtonTypeEnum.BUTTON_TYPE_ABANDONED.getCode());
        businessDataVo.setApprovalComment(vo.getProcessBreakDesc());
        businessDataVo.setIsOutSideAccessProc(true);
        businessDataVo.setOutSideType(1);


        if (!ObjectUtils.isEmpty(vo.getProcessBreakUserId())) {
            Map<String, Object> objectMap = Maps.newHashMap();
            vo.setUserId(vo.getProcessBreakUserId());
            DetailedUser detailedUser = getEmployeeByUserId(vo.getUserId());
            if (detailedUser !=null) {
                objectMap.put("employeeId", detailedUser.getId());
                objectMap.put("employeeName", detailedUser.getUsername());
                businessDataVo.setObjectMap(objectMap);
                businessDataVo.setStartUserId(detailedUser.getId().toString());
            }
        }

        //call common service to stop the process
        processApprovalService.buttonsOperationTransactional(businessDataVo);

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
     * get employee by user id
     *
     * @return
     */
    private DetailedUser getEmployeeByUserId(String userName) {
        Map<String, String> stringStringMap = bpmnEmployeeInfoProviderService.provideEmployeeInfo(Lists.newArrayList(userName));
        if(stringStringMap.isEmpty()){
            return null;
        }
        DetailedUser detailedUser =new DetailedUser();
        detailedUser.setId(userName);
        detailedUser.setUsername(stringStringMap.get(userName));
        return detailedUser;
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

}
