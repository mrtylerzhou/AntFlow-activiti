package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.*;
import org.openoa.base.dto.PageDto;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.service.ProcessorFactory;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.*;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.adp.bpmnnodeadp.BpmnNodeAdaptor;
import org.openoa.engine.bpmnconf.common.NodeAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.common.TaskMgmtServiceImpl;
import org.openoa.engine.bpmnconf.confentity.*;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.base.constant.enums.EventTypeEnum;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmNodeLabelsServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmProcessNameServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnViewPageButtonBizServiceImpl;
import org.openoa.engine.factory.IAdaptorFactory;
import org.openoa.engine.utils.MultiTenantIdUtil;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.NumberConstants.BPMN_FLOW_TYPE_OUTSIDE;
import static org.openoa.base.constant.enums.NodeTypeEnum.NODE_TYPE_APPROVER;


/**
 * @Classname BpmnConfServiceImpl
 * @Description bpmn conf service
 * @Date 2021-10-31 10:29
 * @Created by AntOffice
 */
@Service
public class BpmnConfServiceImpl extends ServiceImpl<BpmnConfMapper, BpmnConf> {

    private static final String linkMark = "_";
    @Autowired
    private BpmnNodeServiceImpl bpmnNodeService;
    @Autowired
    private BpmnViewPageButtonServiceImpl bpmnViewPageButtonService;
    @Autowired
    private BpmnNodeToServiceImpl bpmnNodeToService;
    @Autowired
    private BpmnTemplateServiceImpl bpmnTemplateService;
    @Autowired
    private InformationTemplateServiceImpl informationTemplateService;
    @Autowired
    private BpmnNodeButtonConfServiceImpl bpmnNodeButtonConfService;
    @Autowired
    private BpmnNodeSignUpConfServiceImpl bpmnNodeSignUpConfService;
    @Autowired
    private BpmnApproveRemindServiceImpl bpmnApproveRemindService;
    @Autowired
    private BpmnConfNoticeTemplateServiceImpl bpmnConfNoticeTemplateService;
    @Autowired
    private BpmnViewPageButtonBizServiceImpl bpmnViewPageButtonBizService;
    @Autowired
    private BpmProcessNameServiceImpl bpmProcessNameService;
    @Autowired
    private BpmnEmployeeInfoProviderService employeeInfoProvider;
    @Autowired
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;
    @Autowired
    private OutSideBpmCallbackUrlConfServiceImpl outSideBpmCallbackUrlConfService;
    @Autowired
    private ApplicationServiceImpl applicationService;
    @Autowired
    private IAdaptorFactory adaptorFactory;
    @Autowired
    private BpmnNodeLfFormdataFieldControlServiceImpl nodeLfFormdataFieldControlService;
    @Autowired
    private BpmNodeLabelsServiceImpl nodeLabelsService;
    @Autowired
    private BpmProcessAppApplicationServiceImpl bpmProcessAppApplicationService;
    @Autowired
    private TaskMgmtServiceImpl TaskMgmtService;
    @Autowired
    private NodeAdditionalInfoServiceImpl nodeAdditionalInfoService;


    @Transactional
    public void edit(BpmnConfVo bpmnConfVo) {
        String bpmnName = bpmnConfVo.getBpmnName();
        String bpmnCode = getBpmnCode(bpmnName);
        String formCode = bpmnConfVo.getFormCode();
        BpmnConf bpmnConf = new BpmnConf();
        BeanUtils.copyProperties(bpmnConfVo, bpmnConf);
        bpmnConf.setBpmnCode(bpmnCode);
        bpmnConf.setFormCode(formCode);
        bpmnConf.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
        bpmnConf.setCreateTime(new Date());
        bpmnConf.setUpdateUser(SecurityUtils.getLogInEmpNameSafe());
        bpmnConfVo.setUpdateTime(new Date());

        this.getBaseMapper().insert(bpmnConf);
        //effectiveBpmnConf(bpmnConf.getId().intValue());
        //notice template
        bpmnConfNoticeTemplateService.insert(bpmnCode);
        Long confId = bpmnConf.getId();
        if(confId==null){
            throw new JiMuBizException(Strings.lenientFormat("conf id for formcode:%s can not be null",formCode));
        }
        bpmnConfVo.setId(confId);
        bpmnViewPageButtonBizService.editBpmnViewPageButton(bpmnConfVo, confId);

        bpmnTemplateService.editBpmnTemplate(bpmnConfVo, confId);

        Integer isOutSideProcess = bpmnConfVo.getIsOutSideProcess();
        Integer isLowCodeFlow = bpmnConfVo.getIsLowCodeFlow();

        ProcessorFactory.executePreWriteProcessors(bpmnConfVo);
        List<BpmnNodeVo> confNodes = bpmnConfVo.getNodes();
        int hasStartUserChooseModules=0;
        int hasCopy=0;
        int hasLastNodeCopy=0;

        for (BpmnNodeVo bpmnNodeVo : confNodes) {
            if (bpmnNodeVo.getNodeType().intValue() == NODE_TYPE_APPROVER.getCode()
                    && ObjectUtils.isEmpty(bpmnNodeVo.getNodeProperty())) {
                throw new JiMuBizException("apporver node has no property,can not be saved！");
            }

            if(NodePropertyEnum.NODE_PROPERTY_CUSTOMIZE.getCode().equals(bpmnNodeVo.getNodeProperty())){
                hasStartUserChooseModules=BpmnConfFlagsEnum.HAS_STARTUSER_CHOOSE_MODULES.getCode();
            }
            if(NodeTypeEnum.NODE_TYPE_COPY.getCode().equals(bpmnNodeVo.getNodeType())){
                hasCopy=BpmnConfFlagsEnum.HAS_COPY.getCode();;
            }
            bpmnNodeVo.setIsOutSideProcess(isOutSideProcess);
            bpmnNodeVo.setIsLowCodeFlow(isLowCodeFlow);

            //if the node has no property,the node property default is "1-no property"
            bpmnNodeVo.setNodeProperty(Optional.ofNullable(bpmnNodeVo.getNodeProperty())
                    .orElse(1));

            BpmnNode bpmnNode = new BpmnNode();
            BeanUtils.copyProperties(bpmnNodeVo, bpmnNode);
            bpmnNode.setConfId(confId);
            bpmnNode.setCreateTime(new Date());
            bpmnNode.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            bpmnNodeService.getBaseMapper().insert(bpmnNode);

            Long bpmnNodeId = bpmnNode.getId();
            if(bpmnNodeId==null){
                throw new JiMuBizException("can not get bpmn node id!");
            }

            //edit node to
            bpmnNodeToService.editNodeTo(bpmnNodeVo, bpmnNodeId);

            //edit node's button conf
            bpmnNodeButtonConfService.editButtons(bpmnNodeVo, bpmnNodeId);

            //edit node sign up
            bpmnNodeSignUpConfService.editSignUpConf(bpmnNodeVo, bpmnNodeId);


            bpmnNodeVo.setId(bpmnNodeId);
            bpmnNodeVo.setConfId(confId);
            bpmnNodeVo.setFormCode(formCode);
            BpmnNodeAdpConfEnum bpmnNodeAdpConfEnum = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(bpmnNodeVo);

            //if it can not get the node's adapter,continue
            if (ObjectUtils.isEmpty(bpmnNodeAdpConfEnum)) {
                continue;
            }

            //edit in node notice template
            bpmnTemplateService.editBpmnTemplate(bpmnNodeVo);


            //edit in node approver remind conf
            bpmnApproveRemindService.editBpmnApproveRemind(bpmnNodeVo);

            //get node adaptor
            BpmnNodeAdaptor bpmnNodeAdaptor = nodeAdditionalInfoService.getBpmnNodeAdaptor(bpmnNodeAdpConfEnum);

            //then edit the node
            bpmnNodeAdaptor.editBpmnNode(bpmnNodeVo);
            if(NodeTypeEnum.NODE_TYPE_COPY.getCode().equals(bpmnNodeVo.getNodeType())&&CollectionUtils.isEmpty(bpmnNodeVo.getNodeTo())){
                hasLastNodeCopy=BpmnConfFlagsEnum.HAS_LAST_NODE_COPY.getCode();
            }

        }
        ProcessorFactory.executePostProcessors(bpmnConfVo);
        Integer extraFlags = bpmnConfVo.getExtraFlags();
        Integer currentFlags=hasStartUserChooseModules|hasCopy|hasLastNodeCopy;
        if(currentFlags!=null&&currentFlags>0){
            Integer binariedOr = BpmnConfFlagsEnum.binaryOr(extraFlags, currentFlags);
            bpmnConfVo.setExtraFlags(binariedOr);
        }
        if (bpmnConfVo.getExtraFlags()!=null) {
            BpmnConf postConf=new BpmnConf();
            postConf.setId(confId);
            postConf.setExtraFlags(bpmnConfVo.getExtraFlags());
            this.updateById(postConf);
        }

    }


    private String getBpmnCode(String bpmnName) {
        BpmnConf.validateBpmnName(bpmnName);
        String bpmnFirstLetters = StrUtils.getFirstLetters(bpmnName);
        String maxBpmnCode = this.getMaxBpmnCode(bpmnFirstLetters);
        if (!Strings.isNullOrEmpty(maxBpmnCode)) {
            return reCheckBpmnCode(bpmnFirstLetters, maxBpmnCode);
        }
        return reCheckBpmnCode(bpmnFirstLetters, bpmnFirstLetters);
    }

    private String getMaxBpmnCode(String bpmnCodeParts) {
        return this.getBaseMapper().getMaxBpmnCode(bpmnCodeParts);
    }

    private String reCheckBpmnCode(String bpmnCodeParts, String bpmnCode) {

        long count = this.getBaseMapper().selectCount(new QueryWrapper<BpmnConf>().eq("bpmn_code", bpmnCode));

        if (count == 0) {
            return bpmnCode;
        }

        String reJoinedBpmnCode = StrUtils.joinBpmnCode(bpmnCodeParts, bpmnCode);

        return reCheckBpmnCode(bpmnCodeParts, reJoinedBpmnCode);

    }

    /**
     * query conf detail by id
     *
     * @param id
     * @return
     */
    public BpmnConfVo detail(long id) {
        BpmnConf bpmnConf = this.getBaseMapper().selectById(id);
        return formatConfVo(getBpmnConfVo(bpmnConf));
    }

    /**
     * query conf detail by bpmnCode
     * @param bpmnCode
     * @return
     */
    public BpmnConfVo detail(String bpmnCode) {
        BpmnConf bpmnConf = this.getBaseMapper().selectOne(new LambdaQueryWrapper<BpmnConf>()
                .eq(BpmnConf::getBpmnCode, bpmnCode));
        return getBpmnConfVo(bpmnConf);
    }

    /**
     * query conf by formCode
     *
     * @param formCode
     * @return
     */
    public BpmnConfVo detailByFormCode(String formCode) {
        BpmnConf bpmnConf = this.getBaseMapper().selectOne(new QueryWrapper<BpmnConf>()
                .eq("form_code", formCode).eq("effective_status", 1));
        if(bpmnConf==null){
            throw new JiMuBizException("can not get a bpmnConf by provided formCode");
        }
        return getBpmnConfVo(bpmnConf);
    }

    private BpmnConfVo formatConfVo(BpmnConfVo confVo){
        if(confVo==null){
            throw new JiMuBizException("has not confVo");
        }
        List<BpmnNodeVo> nodes = confVo.getNodes();
        if(CollectionUtils.isEmpty(nodes)){
            throw new JiMuBizException("confVo has empty nodes");
        }
        for (BpmnNodeVo node : nodes) {
            BpmnNodePropertysVo property = node.getProperty();
            if(property!=null){
                property.setConditionsConf(null);
            }
        }
        return confVo;
    }
    /**
     * 获得BpmnConfVo
     *
     * @param bpmnConf
     * @return
     */
    private BpmnConfVo getBpmnConfVo(BpmnConf bpmnConf) {
        if (ObjectUtils.isEmpty(bpmnConf)) {
            return new BpmnConfVo();
        }
        BpmnConfVo bpmnConfVo = new BpmnConfVo();
        BeanUtils.copyProperties(bpmnConf, bpmnConfVo);

        String conditionsUrl = "";
        if (bpmnConfVo.getIsOutSideProcess()!=null&&bpmnConf.getIsOutSideProcess()==1) {
            //query and set business party's call url
            OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = outSideBpmCallbackUrlConfService
                    .getOutSideBpmCallbackUrlConf(bpmnConf.getId(), bpmnConf.getBusinessPartyId());
            if (outSideBpmCallbackUrlConf!=null) {
                bpmnConfVo.setBpmConfCallbackUrl(outSideBpmCallbackUrlConf.getBpmConfCallbackUrl());//process config call back url
                bpmnConfVo.setBpmFlowCallbackUrl(outSideBpmCallbackUrlConf.getBpmFlowCallbackUrl());//process flow call back url
            }


            //query business party's info
            OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getById(bpmnConf.getBusinessPartyId());

            //set business party's name
            bpmnConfVo.setBusinessPartyName(outSideBpmBusinessParty.getName());

            //set business party's mark,mark just like record is a unique identifier for a certain business party,but for human readability
            bpmnConfVo.setBusinessPartyMark(outSideBpmBusinessParty.getBusinessPartyMark());

            //set business party's business type
            bpmnConfVo.setType(BPMN_FLOW_TYPE_OUTSIDE);

            //query business application url
            BpmProcessAppApplicationVo applicationUrl = applicationService.getApplicationUrl(outSideBpmBusinessParty.getBusinessPartyMark(), bpmnConfVo.getFormCode());


            //set view url,submit url and condition url
            if (applicationUrl!=null) {
                bpmnConfVo.setViewUrl(applicationUrl.getLookUrl());//view url
                bpmnConfVo.setSubmitUrl(applicationUrl.getSubmitUrl());//submit url
                bpmnConfVo.setConditionsUrl(applicationUrl.getConditionUrl());//condition url
                bpmnConfVo.setAppId(applicationUrl.getId());//关联应用Id
                conditionsUrl = applicationUrl.getConditionUrl();
            }
        }
        ProcessorFactory.executePreReadProcessors(bpmnConfVo);
        //set nodes
        List<BpmnNode> bpmnNodes = bpmnNodeService.getBaseMapper()
                .selectList(new LambdaQueryWrapper<BpmnNode>()
                        .eq(BpmnNode::getConfId,bpmnConf.getId())
                        .eq(BpmnNode::getIsDel,0));

        boolean isOutSideProcess=bpmnConf.getIsOutSideProcess()!=null&&bpmnConf.getIsOutSideProcess()==1;
        boolean isLowCodeFlow=bpmnConf.getIsLowCodeFlow()!=null&&bpmnConf.getIsLowCodeFlow()==1;
        if(isOutSideProcess||isLowCodeFlow||bpmnConf.getExtraFlags()!=null){
            for (BpmnNode bpmnNode : bpmnNodes) {
                bpmnNode.setIsOutSideProcess(bpmnConf.getIsOutSideProcess());
                bpmnNode.setIsLowCodeFlow(bpmnConf.getIsLowCodeFlow());
                bpmnNode.setExtraFlags(bpmnConf.getExtraFlags());
            }
        }
        bpmnConfVo.setNodes(getBpmnNodeVoList(bpmnNodes, conditionsUrl));
        if (!ObjectUtils.isEmpty(bpmnConfVo.getNodes())) {
            Map<String,BpmnNodeVo>id2NodeMap=null;
            for (BpmnNodeVo node : bpmnConfVo.getNodes()) {
                    node.setFormCode(bpmnConfVo.getFormCode());
                    if(NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode().equals(node.getNodeType())){
                        BpmnNodeVo aggregationNode = BpmnUtils.getAggregationNode(node, bpmnConfVo.getNodes());
                        if(aggregationNode==null){
                            throw new JiMuBizException("can not find parallel gateway's aggregation node!");
                        }
                        aggregationNode.setAggregationNode(true);
                        aggregationNode.setDeduplicationExclude(true);
                    }
                   /* if(NODE_TYPE_CONDITIONS.getCode().equals(node.getNodeType())&&node.getNodeTo().size()>1){
                        String nodeFrom = node.getNodeFrom();
                        if(id2NodeMap==null){
                            id2NodeMap=bpmnConfVo.getNodes().stream().collect(Collectors.toMap(BpmnNodeVo::getNodeId, o -> o,(k1,k2)->k1));
                        }
                        BpmnNodeVo gatewayNode = id2NodeMap.get(nodeFrom);
                        gatewayNode.setIsParallel(true);
                        BpmnNodeVo aggregationNode = BpmnUtils.getAggregationNode(node, bpmnConfVo.getNodes());
                        if(aggregationNode==null){
                            throw new JiMuBizException("can not find parallel gateway's aggregation node!");
                        }
                        aggregationNode.setAggregationNode(true);
                        aggregationNode.setDeduplicationExclude(true);
                    }*/
            }
        }
        //set viewpage buttons
        setViewPageButton(bpmnConfVo);


        //set out node notice template
        setBpmnTemplateVos(bpmnConfVo);
        return bpmnConfVo;
    }

    /**
     * set out of node notice template
     *
     * @param bpmnConfVo bpmnConfVo
     */
    public void setBpmnTemplateVos(BpmnConfVo bpmnConfVo) {
        bpmnConfVo.setTemplateVos(
                bpmnTemplateService.getBaseMapper().selectList(
                        new QueryWrapper<BpmnTemplate>()
                                .eq("form_code", bpmnConfVo.getFormCode())
                                .eq("is_del", 0)
                                .isNull("node_id"))
                        .stream()
                        .map(o -> {
                            BpmnTemplateVo vo = new BpmnTemplateVo();
                            buildBpmnTemplateVo(o,vo);
                            return vo;
                        }).collect(Collectors.toList()));
    }

    private void buildBpmnTemplateVo(BpmnTemplate entity,BpmnTemplateVo vo) {
        BeanUtils.copyProperties(entity, vo);
        vo.setEventValue(EventTypeEnum.getDescByByCode(vo.getEvent()));
        if (!ObjectUtils.isEmpty(vo.getInforms())) {
            vo.setInformIdList(
                    Arrays.stream(vo.getInforms().split(","))
                            .collect(Collectors.toList()));
            vo.setInformList(vo.getInformIdList()
                    .stream()
                    .map(o -> BaseIdTranStruVo
                            .builder()
                            .id(o)
                            .name(InformEnum.getDescByByCode(Integer.parseInt(o)))
                            .build())
                    .collect(Collectors.toList()));
        }
        if (!ObjectUtils.isEmpty(vo.getEmps())) {
            vo.setEmpIdList(
                    Arrays.stream(vo.getEmps().split(","))
                            .collect(Collectors.toList()));

            Map<String, String> employeeInfo = employeeInfoProvider.provideEmployeeInfo(vo.getEmpIdList());
            vo.setEmpList(vo.getEmpIdList()
                    .stream()
                    .map(o -> BaseIdTranStruVo
                            .builder()
                            .id(o)
                            .name(employeeInfo.get(o))
                            .build())
                    .collect(Collectors.toList()));
        }
        if(!StringUtils.isEmpty(entity.getMessageSendType())){
            String[] messageSendTypesStr = entity.getMessageSendType().split(",");
            //这些都是从库里选择出来的,都是活跃的
            List<BaseNumIdStruVo> messageSendTypes = Arrays.stream(messageSendTypesStr)
                    .map(a -> BaseNumIdStruVo.builder().id(Long.parseLong(a)).name(MessageSendTypeEnum.getEnumByCode(Integer.parseInt(a)).getDesc()).active(true).build())
                    .collect(Collectors.toList());
          vo.setMessageSendTypeList(messageSendTypes);
        }
        //todo functions to be implemented
        vo.setTemplateName(Optional
                .ofNullable(informationTemplateService.getBaseMapper().selectById(vo.getTemplateId()))
                .orElse(new InformationTemplate())
                .getName());
    }

    /**
     * set view page buttons
     *
     * @param bpmnConfVo
     */
    private void setViewPageButton(BpmnConfVo bpmnConfVo) {
        List<BpmnViewPageButton> bpmnViewPageButtons = bpmnViewPageButtonService.getBaseMapper().selectList(
                new QueryWrapper<BpmnViewPageButton>()
                        .eq("conf_id", bpmnConfVo.getId())
                        .eq("is_del", 0));

        BpmnViewPageButtonBaseVo bpmnViewPageButtonBaseVo = new BpmnViewPageButtonBaseVo();

        //start user's view page
        bpmnViewPageButtonBaseVo.setViewPageStart(getViewPageButtonsByType(bpmnViewPageButtons, ViewPageTypeEnum.VIEW_PAGE_TYPE_START));

        //approver's view page
        bpmnViewPageButtonBaseVo.setViewPageOther(getViewPageButtonsByType(bpmnViewPageButtons, ViewPageTypeEnum.VIEW_PAGE_TYPE_OTHER));

        //set view page buttons
        bpmnConfVo.setViewPageButtons(bpmnViewPageButtonBaseVo);

    }

    /**
     * query view page button list by type
     *
     * @param bpmnViewPageButtons
     * @param viewPageTypeEnum
     * @return
     */
    private List<Integer> getViewPageButtonsByType(List<BpmnViewPageButton> bpmnViewPageButtons, ViewPageTypeEnum viewPageTypeEnum) {
        return bpmnViewPageButtons
                .stream()
                .filter(o -> o.getViewType().intValue() == viewPageTypeEnum.getCode().intValue())
                .collect(Collectors.toList())
                .stream()
                .map(BpmnViewPageButton::getButtonType)
                .collect(Collectors.toList());
    }

    /**
     * convert volist
     *
     * @param bpmnNodeList bpmnNodeList
     * @return List
     */
    private List<BpmnNodeVo> getBpmnNodeVoList(List<BpmnNode> bpmnNodeList, String conditionsUrl) {


        List<Long> idList = bpmnNodeList.stream().map(BpmnNode::getId).collect(Collectors.toList());


        Map<Long, List<String>> bpmnNodeToMap = nodeAdditionalInfoService.getBpmnNodeToMap(idList);


        Map<Long, List<BpmnNodeButtonConf>> bpmnNodeButtonConfMap = getBpmnNodeButtonConfMap(idList);


        Map<Long, BpmnNodeSignUpConf> bpmnNodeSignUpConfMap = getBpmnNodeSignUpConfMap(idList);


        Map<Long, List<BpmnTemplateVo>> bpmnTemplateVoMap = getBpmnTemplateVoMap(idList);


        Map<Long, BpmnApproveRemindVo> bpmnApproveRemindVoMap = getBpmnApproveRemindVoMap(idList);
        Map<Long, List<BpmnNodeLabel>> bpmnNodeLabelsVoMap =new HashMap<>();

        Integer isLowCodeFlow = bpmnNodeList.get(0).getIsLowCodeFlow();
        Integer extraFlags = bpmnNodeList.get(0).getExtraFlags();
        boolean hasNodeLabels = BpmnConfFlagsEnum.hasFlag(extraFlags, BpmnConfFlagsEnum.HAS_NODE_LABELS);
        if(hasNodeLabels){
            bpmnNodeLabelsVoMap=getBpmnNodeLabelsVoMap(idList);
        }
        Map<Long, List<BpmnNodeLfFormdataFieldControl>> bpmnNodeFieldControlConfMap;
        if(isLowCodeFlow!=null&&isLowCodeFlow==1){
            bpmnNodeFieldControlConfMap = getBpmnNodeFieldControlConfMap(idList);
        } else {
            bpmnNodeFieldControlConfMap = null;
        }

        Map<Long, List<BpmnNodeLabel>> finalBpmnNodeLabelsVoMap = bpmnNodeLabelsVoMap;
        List<BpmnNodeVo> bpmnNodeVoList = new ArrayList<>(bpmnNodeList.size());
        for (BpmnNode bpmnNode : bpmnNodeList) {
            BpmnNodeVo bpmnNodeVo = getBpmnNodeVo(bpmnNode, bpmnNodeToMap, bpmnNodeButtonConfMap, bpmnNodeSignUpConfMap,
                    bpmnTemplateVoMap, bpmnApproveRemindVoMap, bpmnNodeFieldControlConfMap, conditionsUrl, finalBpmnNodeLabelsVoMap);
            bpmnNodeVoList.add(bpmnNodeVo);
            //动态条件节点是网关节点,找到网关节点的上一级节点,然后打上标签,流程执行过程中如果有相应标签,则执行动态条件判断
            if(Boolean.TRUE.equals(bpmnNodeVo.getIsDynamicCondition())){
                BpmnNode prevNode= bpmnNodeList.stream().
                        filter(o ->bpmnNodeVo.getNodeFrom().equals(o.getNodeId())).findFirst().orElse(null);

                if(prevNode!=null){
                    List<BpmnNode> nodes=new ArrayList<>();
                    nodes.add(prevNode);
                    List<Long> dynamicLabelNodeIds=new ArrayList<>();
                    if(NodeTypeEnum.NODE_TYPE_GATEWAY.getCode().equals(prevNode.getNodeType())){
                        for (Map.Entry<Long, List<String>> nodeToEntry : bpmnNodeToMap.entrySet()) {
                            Long nodeId = nodeToEntry.getKey();
                            List<String> nodeTos = nodeToEntry.getValue();
                            if(!CollectionUtils.isEmpty(nodeTos)&&(nodeTos.contains(prevNode.getNodeId())|| nodeTos.contains(bpmnNodeVo.getNodeId()))){
                                dynamicLabelNodeIds.add(nodeId);
                            }

                        }
                    }

                    if(!CollectionUtils.isEmpty(dynamicLabelNodeIds)){
                        List<BpmnNode> dynamicLabelNodes = bpmnNodeList.stream().filter(a -> dynamicLabelNodeIds.contains(a.getId())).collect(Collectors.toList());
                        nodes.addAll(dynamicLabelNodes);
                    }
                    for (BpmnNode node : nodes) {
                        List<BpmnNodeLabelVO> labelList = node.getLabelList();
                        if(CollectionUtils.isEmpty(labelList)){
                            labelList = new ArrayList<>();
                            labelList.add(NodeLabelConstants.dynamicCondition);
                            node.setLabelList(labelList);
                        }else{
                            node.getLabelList().add(NodeLabelConstants.dynamicCondition);
                        }
                    }

                }else{
                    log.warn("can not find prev node for node:%s");
                }
            }
        }
        return bpmnNodeVoList;

    }

    /**
     * query notice template by ids
     *
     * @param ids ids
     * @return map
     */
    private Map<Long, List<BpmnTemplateVo>> getBpmnTemplateVoMap(List<Long> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            return new HashMap<>();
        }
        return bpmnTemplateService.getBaseMapper().selectList(
                new QueryWrapper<BpmnTemplate>()
                        .in("node_id", ids)
                        .eq("is_del", 0))
                .stream()
                .collect(Collectors.toMap(
                        BpmnTemplate::getNodeId,
                        o -> {
                            BpmnTemplateVo vo = new BpmnTemplateVo();
                            buildBpmnTemplateVo(o,vo);
                            return new ArrayList<>(Collections.singletonList(vo));
                        },
                        (a, b) -> {
                            a.addAll(b);
                            return a;
                        }));
    }

    private Map<Long, BpmnApproveRemindVo> getBpmnApproveRemindVoMap(List<Long> ids) {
        if (ObjectUtils.isEmpty(ids)) {
            return new HashMap<>();
        }
        return bpmnApproveRemindService.getBaseMapper().selectList(
                new QueryWrapper<BpmnApproveRemind>()
                        .in("node_id", ids)
                        .eq("is_del", 0))
                .stream()
                .collect(Collectors.toMap(
                        BpmnApproveRemind::getNodeId,
                        o -> {
                            BpmnApproveRemindVo vo = new BpmnApproveRemindVo();
                            vo.setIsInuse(false);
                            BeanUtils.copyProperties(o, vo);
                            vo.setTemplateName(Optional
                                    .ofNullable(informationTemplateService.getBaseMapper().selectById(vo.getTemplateId()))
                                    .orElse(new InformationTemplate())
                                    .getName());
                            if (!ObjectUtils.isEmpty(vo.getDays())) {
                                vo.setDayList(Arrays.stream(vo.getDays().split(","))
                                        .map(Integer::parseInt)
                                        .collect(Collectors.toList()));
                            }
                            if (!ObjectUtils.isEmpty(vo.getTemplateId())
                                    && !ObjectUtils.isEmpty(vo.getDays())) {
                                vo.setIsInuse(true);
                            }
                            return vo;
                        },
                        (a, b) -> a));
    }
private Map<Long,List<BpmnNodeLabel>> getBpmnNodeLabelsVoMap(List<Long> ids){
    List<BpmnNodeLabel> nodeLabels = nodeLabelsService.list(Wrappers.<BpmnNodeLabel>lambdaQuery().in(BpmnNodeLabel::getNodeId,ids));
    return nodeLabels.stream().collect(Collectors.groupingBy(BpmnNodeLabel::getNodeId));
}
    /**
     * get node signup conf map
     *
     * @param idList
     * @return
     */
    private Map<Long, BpmnNodeSignUpConf> getBpmnNodeSignUpConfMap(List<Long> idList) {
        return bpmnNodeSignUpConfService.getBaseMapper().selectList(new QueryWrapper<BpmnNodeSignUpConf>()
                .in("bpmn_node_id", idList)
                .eq("is_del", 0)).stream()
                .collect(Collectors.toMap(BpmnNodeSignUpConf::getBpmnNodeId, o -> o));
    }

    /**
     * get button conf map
     *
     * @param idList
     * @return
     */
    private Map<Long, List<BpmnNodeButtonConf>> getBpmnNodeButtonConfMap(List<Long> idList) {
        return bpmnNodeButtonConfService.getBaseMapper().selectList(
                new QueryWrapper<BpmnNodeButtonConf>()
                        .in("bpmn_node_id", idList)
                        .eq("is_del", 0))
                .stream()
                .collect(Collectors.toMap(
                        BpmnNodeButtonConf::getBpmnNodeId,
                        v -> Lists.newArrayList(Collections.singletonList(v)),
                        (a, b) -> {
                            a.addAll(b);
                            return a;
                        }));
    }

    private Map<Long,List<BpmnNodeLfFormdataFieldControl>> getBpmnNodeFieldControlConfMap(List<Long> idList){
        return nodeLfFormdataFieldControlService.list(
                Wrappers.<BpmnNodeLfFormdataFieldControl>lambdaQuery()
                        .in(BpmnNodeLfFormdataFieldControl::getNodeId,idList)
        ).stream()
                .collect(Collectors.toMap(
                        BpmnNodeLfFormdataFieldControl::getNodeId,
                        Lists::newArrayList,
                        (a,b)->{
                            a.addAll(b);
                            return a;
                        }
                ));
    }
    /**
     * convert bpmnnode to nodevo
     *
     * @param bpmnNode bpmnNode
     * @return BpmnNodeVo
     */
    private BpmnNodeVo getBpmnNodeVo(BpmnNode bpmnNode, Map<Long, List<String>> bpmnNodeToMap, Map<Long,
            List<BpmnNodeButtonConf>> bpmnNodeButtonConfMap, Map<Long, BpmnNodeSignUpConf> bpmnNodeSignUpConfMap,
                                     Map<Long, List<BpmnTemplateVo>> bpmnTemplateVoMap,
                                     Map<Long, BpmnApproveRemindVo> bpmnApproveRemindVoMap,
                                     Map<Long, List<BpmnNodeLfFormdataFieldControl>> lfFieldControlMap,
                                     String conditionsUrl, Map<Long, List<BpmnNodeLabel>> bpmnNodeLabelsVoMap) {


        BpmnNodeVo bpmnNodeVo = new BpmnNodeVo();
        BeanUtils.copyProperties(bpmnNode, bpmnNodeVo);


        //set nodeto
        bpmnNodeVo.setNodeTo(bpmnNodeToMap.get(bpmnNode.getId()));

        //set buttons conf
        setButtons(bpmnNodeVo, bpmnNodeButtonConfMap.get(bpmnNode.getId()));

        //assign property name
        bpmnNodeVo.setNodePropertyName(NodePropertyEnum.getDescByCode(bpmnNodeVo.getNodeProperty()));

        //set in node notice template
        bpmnNodeVo.setTemplateVos(bpmnTemplateVoMap.get(bpmnNode.getId()));


        //set in node approvement remind
        bpmnNodeVo.setApproveRemindVo(bpmnApproveRemindVoMap.get(bpmnNode.getId()));


        BpmnNodeAdpConfEnum bpmnNodeAdpConfEnum = NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(bpmnNodeVo);


        if (ObjectUtils.isEmpty(bpmnNodeAdpConfEnum)) {
            return bpmnNodeVo;
        }

        //get node adaptor
        BpmnNodeAdaptor bpmnNodeAdaptor = getBpmnNodeAdaptor(bpmnNodeAdpConfEnum);

        //use adaptor to format nodevo
        bpmnNodeAdaptor.formatToBpmnNodeVo(bpmnNodeVo);


        if (NodeTypeEnum.NODE_TYPE_OUT_SIDE_CONDITIONS.getCode().equals(bpmnNode.getNodeType())) {
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITIONS.getCode());
        }

        //set sign up conf
        setBpmnNodeSignUpConf(bpmnNode, bpmnNodeSignUpConfMap, bpmnNodeVo);
        setFieldControlVOs(bpmnNode,lfFieldControlMap,bpmnNodeVo);
        List<BpmnNodeLabel> nodeLabels = bpmnNodeLabelsVoMap.get(bpmnNode.getId());
        if(!CollectionUtils.isEmpty(nodeLabels)){
            List<BpmnNodeLabelVO> labelVOList = nodeLabels.stream().map(a -> new BpmnNodeLabelVO(a.getLabelValue(), a.getLabelName())).collect(Collectors.toList());
            bpmnNodeVo.setLabelList(labelVOList);
        }

        return bpmnNodeVo;
    }

    /**
     * get node adaptor
     *
     * @param bpmnNodeAdpConfEnum
     * @return
     */
    private BpmnNodeAdaptor getBpmnNodeAdaptor(BpmnNodeAdpConfEnum bpmnNodeAdpConfEnum) {

        return adaptorFactory.getBpmnNodeAdaptor(bpmnNodeAdpConfEnum);
    }


    /**
     * set buttons
     *
     * @param bpmnNodeVo
     */
    private void setButtons(BpmnNodeVo bpmnNodeVo, List<BpmnNodeButtonConf> bpmnNodeButtonConfs) {

        if (!ObjectUtils.isEmpty(bpmnNodeButtonConfs)) {

            BpmnNodeButtonConfBaseVo buttons = new BpmnNodeButtonConfBaseVo();


            buttons.setStartPage(getButtons(bpmnNodeButtonConfs, ButtonPageTypeEnum.INITIATE));


            buttons.setApprovalPage(getButtons(bpmnNodeButtonConfs, ButtonPageTypeEnum.AUDIT));


            bpmnNodeVo.setButtons(buttons);

        }

    }

    /**
     * get buttons list
     *
     * @param bpmnNodeButtonConfs
     * @param buttonPageTypeEnum
     * @return
     */
    private List<Integer> getButtons(List<BpmnNodeButtonConf> bpmnNodeButtonConfs, ButtonPageTypeEnum buttonPageTypeEnum) {
        return bpmnNodeButtonConfs
                .stream()
                .filter(o -> o.getButtonPageType().intValue() == buttonPageTypeEnum.getCode())
                .map(BpmnNodeButtonConf::getButtonType)
                .collect(Collectors.toList())
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * set node sign up conf
     *
     * @param bpmnNode
     * @param bpmnNodeSignUpConfMap
     * @param bpmnNodeVo
     */
    private void setBpmnNodeSignUpConf(BpmnNode bpmnNode, Map<Long, BpmnNodeSignUpConf> bpmnNodeSignUpConfMap, BpmnNodeVo bpmnNodeVo) {
        if (bpmnNode.getIsSignUp() != 1) {
            return;
        }
        BpmnNodeSignUpConf bpmnNodeSignUpConf = bpmnNodeSignUpConfMap.get(bpmnNode.getId());
        if (ObjectUtils.isEmpty(bpmnNodeSignUpConf)) {
            return;
        }
        BpmnNodePropertysVo propertysVo = bpmnNodeVo.getProperty();
        if (ObjectUtils.isEmpty(propertysVo)) {
            propertysVo = new BpmnNodePropertysVo();
        }
        propertysVo.setAfterSignUpWay(bpmnNodeSignUpConf.getAfterSignUpWay());
        propertysVo.setSignUpType(bpmnNodeSignUpConf.getSignUpType());
        bpmnNodeVo.setProperty(propertysVo);
    }


    private void setFieldControlVOs(BpmnNode bpmnNode,Map<Long,List<BpmnNodeLfFormdataFieldControl>> fieldControlMap,BpmnNodeVo nodeVo){
        boolean isLowFlow=Objects.equals(bpmnNode.getIsLowCodeFlow(),1);
        if(!isLowFlow){
            return;
        }
        if(CollectionUtils.isEmpty(fieldControlMap)){

            return;
        }
        List<BpmnNodeLfFormdataFieldControl> fieldControls = fieldControlMap.get(bpmnNode.getId());
        if(CollectionUtils.isEmpty(fieldControls)){
            return;
        }
        List<LFFieldControlVO> fieldControlVOS=new ArrayList<>();
        for (BpmnNodeLfFormdataFieldControl fieldControl : fieldControls) {
            LFFieldControlVO lfFieldControlVO=new LFFieldControlVO();
            lfFieldControlVO.setFieldId(fieldControl.getFieldId());
            lfFieldControlVO.setFieldName(fieldControl.getFieldName());
            lfFieldControlVO.setPerm(fieldControl.getPerm());
            fieldControlVOS.add(lfFieldControlVO);
        }
        nodeVo.setLfFieldControlVOs(fieldControlVOS);
    }
    /**
     * effective bpmn conf
     *
     * @param id
     */
    public void effectiveBpmnConf(Integer id) {
        BpmnConf bpmnConf = this.getBaseMapper().selectById(id);
        AssertUtil.throwsIfEmpty(bpmnConf,"未能根据id:查询到指定配置!",Lists.newArrayList(id));

        //query the old effective workflow configuration by formcode
        BpmnConf confInDb = this.getBaseMapper().selectOne(new QueryWrapper<BpmnConf>()
                .eq("form_code", bpmnConf.getFormCode())
                .eq("effective_status", 1));

        if (!ObjectUtils.isEmpty(confInDb)) {
            //set the old one effective status to zero
            confInDb.setEffectiveStatus(0);
            this.updateById(confInDb);
        }else{
            confInDb=new BpmnConf();
        }
        this.updateById(BpmnConf
                .builder()
                .id(id.longValue())
                .appId(confInDb.getAppId())
                .bpmnType(confInDb.getBpmnType())
                .isAll(getIsAll(bpmnConf, confInDb))
                .effectiveStatus(1)
                .build());

        bpmProcessNameService.editProcessName(bpmnConf);
    }

    private Integer getIsAll(BpmnConf bpmnConf, BpmnConf beforeBpmnConf) {
        if (bpmnConf.getIsOutSideProcess() == 1) {
            return 1;
        } else {
            if (!ObjectUtils.isEmpty(beforeBpmnConf.getIsAll())) {
                return beforeBpmnConf.getIsAll();
            }
        }
        return 0;
    }
    /**
     * get conf list with paging
     *
     * @param pageDto
     * @param vo
     * @return
     */
    public ResultAndPage<BpmnConfVo> selectPage(PageDto pageDto, BpmnConfVo vo) {
        //use mybatus plus's paging plugin,mbatis plus is very popular in China even all over the world
        Page<BpmnConfVo> page = PageUtils.getPageByPageDto(pageDto);
        List<BpmnConfVo> bpmnConfVos = this.getBaseMapper().selectPageList(page, vo);
        if (bpmnConfVos==null) {
            return PageUtils.getResultAndPage(page);
        }
        if (vo.getIsOutSideProcess() == 1){
            List<BpmProcessAppApplication> bizAppList = bpmProcessAppApplicationService.selectApplicationList();
            Map<String, String>  bizAppMap= bizAppList
                    .stream()
                    .collect(Collectors.toMap(p->p.getProcessKey(),p->p.getTitle()));
            for (BpmnConfVo record : bpmnConfVos) {
                if (record.getIsOutSideProcess() == 1){
                    record.setFormCodeDisplayName(bizAppMap.get(record.getFormCode()));
                }
            }
        }
        if (vo.getIsOutSideProcess() == 0){
            List<DIYProcessInfoDTO> diyFormCodeList = TaskMgmtService.viewProcessInfo(null);
            Map<String, String>  diyFormCodes= diyFormCodeList
                    .stream()
                    .collect(Collectors.toMap(p->p.getKey(),p->p.getValue()));
            for (BpmnConfVo record : bpmnConfVos) {
                if (record.getIsLowCodeFlow() == 0 && record.getIsOutSideProcess() == 0){
                    record.setFormCodeDisplayName(diyFormCodes.get(record.getFormCode()));
                }
            }
        }
        page.setRecords(bpmnConfVos
                .stream()
                .peek(o -> o.setDeduplicationTypeName(DeduplicationTypeEnum.getDescByCode(o.getDeduplicationType())))
                .collect(Collectors.toList()));
        return PageUtils.getResultAndPage(page);
    }

    /**
     * format outside process form code
     *
     * @param bpmnConfVo
     * @return
     */
    private String formatOutSideFormCode(BpmnConfVo bpmnConfVo) {
        String formCode = bpmnConfVo.getFormCode();
        return formCode.substring(formCode.indexOf(linkMark) + 1);
    }
}
