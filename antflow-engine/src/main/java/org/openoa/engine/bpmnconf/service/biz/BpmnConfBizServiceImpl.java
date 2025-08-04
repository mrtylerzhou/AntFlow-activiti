package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.*;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.*;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.service.ProcessorFactory;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.*;
import org.openoa.base.vo.*;
import org.openoa.common.adaptor.bpmnelementadp.BpmnNodeFormatImpl;
import org.openoa.common.adaptor.bpmnelementadp.BpmnOptionalDuplicatesAdaptor;
import org.openoa.common.adaptor.bpmnelementadp.BpmnOptionalDuplicatesImpl;
import org.openoa.common.formatter.BpmnPersonnelFormat;
import org.openoa.engine.bpmnconf.adp.bpmnnodeadp.BpmnNodeAdaptor;
import org.openoa.engine.bpmnconf.adp.formatter.BpmnRemoveConfFormatFactory;
import org.openoa.engine.bpmnconf.adp.formatter.BpmnStartFormatFactory;
import org.openoa.engine.bpmnconf.common.NodeAdditionalInfoServiceImpl;
import org.openoa.engine.bpmnconf.common.TaskMgmtServiceImpl;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.service.impl.*;
import org.openoa.engine.bpmnconf.service.interf.biz.*;
import org.openoa.engine.bpmnconf.service.interf.repository.*;
import org.openoa.engine.factory.FormFactory;
import org.openoa.engine.factory.IAdaptorFactory;
import org.openoa.engine.utils.AFWrappers;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.NumberConstants.BPMN_FLOW_TYPE_OUTSIDE;
import static org.openoa.base.constant.enums.BpmnNodeParamTypeEnum.*;
import static org.openoa.base.constant.enums.DeduplicationTypeEnum.*;
import static org.openoa.base.constant.enums.NodeTypeEnum.NODE_TYPE_APPROVER;

@Service
@Slf4j
public class BpmnConfBizServiceImpl implements BpmnConfBizService {

    @Autowired
    private BpmnNodeServiceImpl nodeService;

    @Autowired
    private FormFactory formFactory;
    @Autowired
    private BpmnRemoveConfFormatFactory bpmnRemoveConfFormatFactory;
    @Autowired
    private BpmnStartFormatFactory bpmnStartFormatFactory;
    @Autowired
    private BpmnPersonnelFormat bpmnPersonnelFormat;
    @Autowired
    private BpmVariableBizService bpmVariableBizService;
    @Autowired
    private BpmVerifyInfoBizServiceImpl bpmVerifyInfoBizService;
    @Autowired
    private BpmProcessForwardService processForwardService;
    @Autowired
    private NodeAdditionalInfoServiceImpl nodeAdditionalInfoService;
    private static final String linkMark = "_";
    @Autowired
    private BpmnNodeService bpmnNodeService;
    @Autowired
    private BpmnViewPageButtonService bpmnViewPageButtonService;
    @Autowired
    private BpmnNodeToService bpmnNodeToService;
    @Autowired
    private BpmnTemplateService bpmnTemplateService;
    @Autowired
    private InformationTemplateService informationTemplateService;
    @Autowired
    private BpmnNodeButtonConfService bpmnNodeButtonConfService;
    @Autowired
    private BpmnNodeSignUpConfService bpmnNodeSignUpConfService;
    @Autowired
    private BpmnApproveRemindService bpmnApproveRemindService;
    @Autowired
    private BpmnConfNoticeTemplateBizService bpmnConfNoticeTemplateBizService;
    @Autowired
    private BpmnViewPageButtonBizServiceImpl bpmnViewPageButtonBizService;
    @Autowired
    private BpmProcessNameBizService bpmProcessNameService;
    @Autowired
    private BpmnEmployeeInfoProviderService employeeInfoProvider;
    @Autowired
    @Lazy
    private OutSideBpmBusinessPartyService outSideBpmBusinessPartyService;
    @Autowired
    @Lazy
    private OutSideBpmCallbackUrlConfService outSideBpmCallbackUrlConfService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private IAdaptorFactory adaptorFactory;
    @Autowired
    private BpmnNodeLfFormdataFieldControlService nodeLfFormdataFieldControlService;
    @Autowired
    private BpmNodeLabelsService nodeLabelsService;
    @Autowired
    @Lazy
    private BpmProcessAppApplicationService bpmProcessAppApplicationService;
    @Autowired
    private TaskMgmtServiceImpl TaskMgmtService;


    @Override
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
        bpmnConf.setTenantId(MultiTenantUtil.getCurrentTenantId());
        bpmnConfVo.setUpdateTime(new Date());

        this.getMapper().insert(bpmnConf);
        //effectiveBpmnConf(bpmnConf.getId().intValue());
        //notice template
        bpmnConfNoticeTemplateBizService.insert(bpmnCode);
        Long confId = bpmnConf.getId();
        if(confId==null){
            throw new AFBizException(Strings.lenientFormat("conf id for formcode:%s can not be null",formCode));
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
                throw new AFBizException("apporver node has no property,can not be saved！");
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
            bpmnNode.setTenantId(MultiTenantUtil.getCurrentTenantId());
            bpmnNodeService.getBaseMapper().insert(bpmnNode);

            Long bpmnNodeId = bpmnNode.getId();
            if(bpmnNodeId==null){
                throw new AFBizException("can not get bpmn node id!");
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
            this.getService().updateById(postConf);
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
        return this.getMapper().getMaxBpmnCode(bpmnCodeParts);
    }

    private String reCheckBpmnCode(String bpmnCodeParts, String bpmnCode) {

        long count = this.getMapper().selectCount(AFWrappers.<BpmnConf>lambdaTenantQuery().eq(BpmnConf::getBpmnCode, bpmnCode));

        if (count == 0) {
            return bpmnCode;
        }

        String reJoinedBpmnCode = StrUtils.joinBpmnCode(bpmnCodeParts, bpmnCode);

        return reCheckBpmnCode(bpmnCodeParts, reJoinedBpmnCode);

    }

    /**
     * query conf by formCode
     *
     * @param formCode
     * @return
     */
    @Override
    public BpmnConf getBpmnConfByFormCode(String formCode) {
        return Optional.ofNullable(getService().getOne(new QueryWrapper<BpmnConf>()
                .eq("form_code", formCode)
                .eq("effective_status", 1)))
                .orElse(new BpmnConf());
    }

    /**
     * qeruy a list of conf by formCodes
     *
     * @param formCodes
     * @return
     */
    @Override
    public List<BpmnConf> getBpmnConfByFormCodeBatch(List<String> formCodes) {
        return getService().list(new QueryWrapper<BpmnConf>()
                .in("form_code", formCodes)
                .eq("effective_status", 1));
    }

    /**
     * //todo
     * update bpmn conf's extra information
     *
     * @param appId
     * @param bpmnType
     * @param bpmnCode
     */
    @Override
    public void updateBpmnConfByCode(Integer appId, Integer bpmnType, Integer isAll, String bpmnCode) {
        getService().update(BpmnConf
                        .builder()
                        .appId(appId)
                        .bpmnType(bpmnType)
                        .isAll(isAll)
                        .build(),
                new QueryWrapper<BpmnConf>()
                        .eq("bpmn_code", bpmnCode));
    }


    /**
     * process's submit process
     *
     * @param bpmnCode
     * @param bpmnStartConditions
     */
    @Override
    public void startProcess(String bpmnCode, BpmnStartConditionsVo bpmnStartConditions) {

        //to query the process's config information
        BpmnConfVo bpmnConfVo =detail(bpmnCode);
        bpmnStartConditions.setPreview(false);

        // format process's floating direction,set assignees,assignees deduplication and remove some nodes on conditions
        BpmnConfVo confVo = getBpmnConfVo(bpmnStartConditions, bpmnConfVo);

        //to convert the process element information
        //set some basic information
        BpmnConfCommonVo bpmnConfCommonVo = BpmnConfCommonVo
                .builder()
                .bpmnCode(confVo.getBpmnCode())
                .formCode(confVo.getFormCode())
                .bpmnName(confVo.getBpmnName())
                .processNum(bpmnStartConditions.getProcessNum())
                .processName(bpmnConfVo.getBpmnName())
                .processDesc(bpmnStartConditions.getProcessDesc())
                //set out of process notice template
                .templateVos(bpmnConfVo.getTemplateVos())
                .build();

        //set view page's buttons information
        setViewPageButtons(bpmnConfVo, bpmnConfCommonVo);


        BpmnNodeFormatImpl bpmnNodeFormat = SpringBeanUtils.getBean(BpmnNodeFormatImpl.class);
        bpmnConfCommonVo.setElementList(bpmnNodeFormat
                .getBpmnConfCommonElementVoList(bpmnConfCommonVo, confVo.getNodes(), bpmnStartConditions));

        //5、set record variables
        BpmnInsertVariables bpmnInsertVariables = SpringBeanUtils.getBean(BpmnInsertVariablesImpl.class);
        if(Boolean.TRUE.equals(bpmnStartConditions.getIsMigration())){
            bpmVariableBizService.deleteByProcessNumber(bpmnStartConditions.getProcessNum());
        }
        bpmnInsertVariables.insertVariables(bpmnConfCommonVo, bpmnStartConditions);
        //prepared and begin to start up a process
        BpmnCreateBpmnAndStart bpmnCreateBpmnAndStart = SpringBeanUtils.getBean(BpmnCreateBpmnAndStartImpl.class);
        bpmnCreateBpmnAndStart.createBpmnAndStart(bpmnConfCommonVo, bpmnStartConditions);
    }



    /**
     * preview process information on the configuration page
     *
     * @param params
     * @return
     */
    @Override
    public PreviewNode previewNode(String params) {
        return getPreviewNode(params, false);
    }

    /**
     * preview process's information on the start up process page
     *
     * @param params
     * @return
     */
    @Override
    public PreviewNode startPagePreviewNode(String params) {
        return getPreviewNode(params, true);
    }

    public PreviewNode taskPagePreviewNode(String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        String processNumber = jsonObject.getString("processNumber");
        Boolean isLowCodeFlow = jsonObject.getBoolean("isLowCodeFlow");

        QueryWrapper<BpmVariable> wrapper = new QueryWrapper<>();
        wrapper.eq("process_num", processNumber);
        BpmVariable bpmnVariable = bpmVariableBizService.getService().getOne(wrapper);

        String processStartConditions = bpmnVariable.getProcessStartConditions();
        JSONObject objectStart = JSON.parseObject(processStartConditions);
        objectStart.put("bpmnCode", bpmnVariable.getBpmnCode());
        objectStart.put("isLowCodeFlow",Boolean.TRUE.equals(isLowCodeFlow));
        objectStart.put("processNumber",processNumber);
        return getPreviewNode(objectStart.toString(), false);
    }

    /**
     * start pages's preview on smart devices(todo not implemented yet)
     *
     * @param params
     * @return
     */
    @Override
    public List<BpmVerifyInfoVo> appStartPagePreviewNode(String params) {
        List<BpmVerifyInfoVo> bpmVerifyInfoVos = Lists.newArrayList();

        //set start up node info
        bpmVerifyInfoVos.add(BpmVerifyInfoVo
                .builder()
                .taskName("发起")
                .build());

        //get previewNodes
        PreviewNode previewNode = getPreviewNode(params, true);

        //if the NodeList is empty then do nothing
        if (!ObjectUtils.isEmpty(previewNode.getBpmnNodeList())) {
            //nodelist
            List<BpmnNodeVo> bpmnNodeList = previewNode.getBpmnNodeList();
            //get the start node
            BpmnNodeVo startNode = bpmnNodeList.stream().filter(o -> o.getNodeType().equals(NodeTypeEnum.NODE_TYPE_START.getCode())).findFirst().orElse(null);
            //if can not get the start page node then error should thrown
            if (ObjectUtils.isEmpty(startNode)) {
                throw new AFBizException("can't find out the start node！");
            }
            //添加节点
            addBpmVerifyInfoVo(startNode, bpmnNodeList, bpmVerifyInfoVos);
        }

        //add the process end node
        bpmVerifyInfoVos.add(BpmVerifyInfoVo
                .builder()
                .taskName("流程结束")
                .build());

        return bpmVerifyInfoVos;
    }




    @Override
    public boolean migrationCheckConditionsChange(BusinessDataVo vo) {
        BpmnConf bpmnConf = getService().getOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", vo.getBpmnCode()));
        if(bpmnConf==null){
            throw new AFBizException("未找到对应的 bpmnConf 记录");
        }
        BpmnConfVo bpmnConfVo = new BpmnConfVo();
        BeanUtils.copyProperties(bpmnConf, bpmnConfVo);
        FormOperationAdaptor formAdapter = formFactory.getFormAdaptor(vo);
        BpmnStartConditionsVo bpmnStartConditionsVo = formAdapter.launchParameters(vo);
        bpmnStartConditionsVo.setPreview(true);
        bpmnStartConditionsVo.setProcessNum(vo.getProcessNumber());
        bpmnStartConditionsVo.setIsMigration(true);
        List<BpmnNode> bpmnNodes = nodeService.getBaseMapper().selectList(new QueryWrapper<BpmnNode>()
                .eq("conf_id", bpmnConf.getId())
                .eq("is_del", 0));
        Map<Long, List<String>> bpmnNodeToMap = nodeAdditionalInfoService.getBpmnNodeToMap(bpmnNodes.stream().map(BpmnNode::getId).collect(Collectors.toList()));
        // 将查询到的 bpmnNodes 转换为 bpmnNodeVo 列表
        List<BpmnNodeVo> bpmnNodeVoList = bpmnNodes.stream()
            .map(bpmnNode -> {
                BpmnNodeVo bpmnNodeVo = new BpmnNodeVo();
                BeanUtils.copyProperties(bpmnNode, bpmnNodeVo);
                List<String> nodeToIds = bpmnNodeToMap.get(bpmnNode.getId());
                bpmnNodeVo.setNodeTo(nodeToIds);
                BpmnNodeAdaptor bpmnNodeAdaptor = nodeAdditionalInfoService.getBpmnNodeAdaptor(NodeAdditionalInfoServiceImpl.getBpmnNodeAdpConfEnum(bpmnNodeVo));
                if(bpmnNodeAdaptor==null){
                    return bpmnNodeVo;
                }
                //use adaptor to format nodevo
                bpmnNodeAdaptor.formatToBpmnNodeVo(bpmnNodeVo);
                return bpmnNodeVo;
            })
            .collect(Collectors.toList());
        bpmnConfVo.setNodes(bpmnNodeVoList);
        try {
            bpmnStartFormatFactory.formatBpmnConf(bpmnConfVo,bpmnStartConditionsVo);
        }catch (Exception ex){
            if(ex instanceof AFBizException){
                String code = ((AFBizException) ex).getCode();
                if(StringConstants.CONDITION_CHANGED.equals(code)){
                    return true;
                }
                throw ex;
            }
        }
        return false;
    }


    /**
     * set node from information
     *
     * @param nodeList
     * @return
     */
    @Override
    public List<BpmnNodeVo> setNodeFrom(List<BpmnNodeVo> nodeList) {
        Map<String, BpmnNodeVo> map = new HashMap<>(nodeList.size());
        BpmnNodeVo startNode = getNodeMapAndStartNode(nodeList, map);
        List<BpmnNodeVo> resultList = new ArrayList<>();
        BpmnNodeVo lastNode = new BpmnNodeVo();
        lastNode.setNodeId("");
        BpmnNodeVo nowNode = startNode;
        if (nowNode != null) {
            while (true) {
                if (BPMN_NODE_PARAM_SINGLE.getCode().equals(nowNode.getParams().getParamType())) {
                    nowNode.getParams().setAssigneeList(Arrays.asList(nowNode.getParams().getAssignee()));
                }
                nowNode.setNodePropertyName(NodePropertyEnum.getDescByCode(nowNode.getNodeProperty()));
                if (StringUtils.isBlank(nowNode.getParams().getNodeTo())) {
                    nowNode.setNodeFrom(lastNode.getNodeId());
                    resultList.add(nowNode);
                    break;
                }
                if (resultList.size() > nodeList.size()) {
                    log.info("error occur while set nodeFrom info,nodeList:{}", JSON.toJSONString(nodeList));
                    throw new AFBizException("999", "nodeId数据错误");
                }
                nowNode.setNodeFrom(lastNode.getNodeId());
                resultList.add(nowNode);
                lastNode = nowNode;
                nowNode = map.get(nowNode.getParams().getNodeTo());
            }
        }
        return resultList;
    }

    @Override
    public List<BpmnNodeVo> setNodeFromV2(List<BpmnNodeVo> nodeList) {
        Map<String, BpmnNodeVo> map = new HashMap<>(nodeList.size());
        BpmnNodeVo startNode = getNodeMapAndStartNode(nodeList, map);
        List<BpmnNodeVo> resultList = new ArrayList<>();
        BpmnNodeVo lastNode = new BpmnNodeVo();
        lastNode.setNodeId("");
        BpmnNodeVo nowNode = startNode;
        if (nowNode != null) {
            while (true) {
                if (NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode().equals(nowNode.getNodeType())) {
                    BpmnNodeVo aggregationNode = BpmnUtils.getAggregationNode(nowNode, nodeList);
                    treatParallelGateWayRecursively(nowNode,lastNode, aggregationNode, map, resultList,new HashSet<>());

                    nowNode = map.get(aggregationNode.getParams().getNodeTo());
                    lastNode=aggregationNode;
                }
                if (nowNode == null) {
                    break;
                }
                if(!NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode().equals(nowNode.getNodeType())){
                    if (BPMN_NODE_PARAM_SINGLE.getCode().equals(nowNode.getParams().getParamType())) {
                        nowNode.getParams().setAssigneeList(Collections.singletonList(nowNode.getParams().getAssignee()));
                    }
                    nowNode.setNodePropertyName(NodePropertyEnum.getDescByCode(nowNode.getNodeProperty()));
                    if (StringUtils.isBlank(nowNode.getParams().getNodeTo())) {
                        nowNode.setNodeFrom(lastNode.getNodeId());
                        resultList.add(nowNode);
                        break;
                    }
                    if (resultList.size() > nodeList.size()) {
                        log.info("error occur while set nodeFrom info,nodeList:{}", JSON.toJSONString(nodeList));
                        throw new AFBizException("999", "nodeId数据错误");
                    }
                    nowNode.setNodeFrom(lastNode.getNodeId());
                    resultList.add(nowNode);
                    lastNode = nowNode;
                    nowNode = map.get(nowNode.getParams().getNodeTo());
                }
            }

        }
        return resultList;

    }






    /**
     * query all process that all user can create conf
     *
     * @return
     */
    @Override
    public List<BpmnConf> getIsAllConfs() {
        return getService().getBaseMapper().selectList(new QueryWrapper<BpmnConf>()
                .eq("is_all", 1)
                .eq("effective_status", 1)
                .eq("is_del", 0));
    }
    /**
     * query conf detail by id
     *
     * @param id
     * @return
     */
    @Override
    public BpmnConfVo detail(long id) {
        BpmnConf bpmnConf = this.getMapper().selectById(id);
        return formatConfVo(getBpmnConfVo(bpmnConf));
    }

    /**
     * query conf detail by bpmnCode
     * @param bpmnCode
     * @return
     */
    @Override
    public BpmnConfVo detail(String bpmnCode) {
        BpmnConf bpmnConf = this.getMapper().selectOne(AFWrappers.<BpmnConf>lambdaTenantQuery()
                .eq(BpmnConf::getBpmnCode, bpmnCode));
        return getBpmnConfVo(bpmnConf);
    }

    /**
     * query conf by formCode
     *
     * @param formCode
     * @return
     */
    @Override
    public BpmnConfVo detailByFormCode(String formCode) {
        BpmnConf bpmnConf = this.getMapper().selectOne(new QueryWrapper<BpmnConf>()
                .eq("form_code", formCode).eq("effective_status", 1));
        if(bpmnConf==null){
            throw new AFBizException("can not get a bpmnConf by provided formCode");
        }
        return getBpmnConfVo(bpmnConf);
    }



    /**
     * set out of node notice template
     *
     * @param bpmnConfVo bpmnConfVo
     */
    @Override
    public void setBpmnTemplateVos(BpmnConfVo bpmnConfVo) {
        bpmnConfVo.setTemplateVos(
                bpmnTemplateService.getBaseMapper().selectList(
                                AFWrappers.<BpmnTemplate>lambdaTenantQuery()
                                        .eq(BpmnTemplate::getFormCode,bpmnConfVo.getFormCode())
                                        .isNull(BpmnTemplate::getNodeId))
                        .stream()
                        .map(o -> {
                            BpmnTemplateVo vo = new BpmnTemplateVo();
                            buildBpmnTemplateVo(o,vo);
                            return vo;
                        }).collect(Collectors.toList()));
    }
    /**
     * effective bpmn conf
     *
     * @param id
     */
    @Override
    public void effectiveBpmnConf(Integer id) {
        BpmnConf bpmnConf = this.getMapper().selectById(id);
        AssertUtil.throwsIfEmpty(bpmnConf,"未能根据id:查询到指定配置!",Lists.newArrayList(id));

        //query the old effective workflow configuration by formcode
        BpmnConf confInDb = this.getMapper().selectOne(
                AFWrappers.<BpmnConf>lambdaTenantQuery()
                        .eq(BpmnConf::getFormCode,bpmnConf.getFormCode())
                        .eq(BpmnConf::getEffectiveStatus,1));

        if (!ObjectUtils.isEmpty(confInDb)) {
            //set the old one effective status to zero
            confInDb.setEffectiveStatus(0);
            this.getService().updateById(confInDb);
        }else{
            confInDb=new BpmnConf();
        }
        this.getService().updateById(BpmnConf
                .builder()
                .id(id.longValue())
                .appId(confInDb.getAppId())
                .bpmnType(confInDb.getBpmnType())
                .isAll(getIsAll(bpmnConf, confInDb))
                .effectiveStatus(1)
                .build());

        bpmProcessNameService.editProcessName(bpmnConf);
    }


    /**
     * get conf list with paging
     *
     * @param pageDto
     * @param vo
     * @return
     */
    @Override
    public ResultAndPage<BpmnConfVo> selectPage(PageDto pageDto, BpmnConfVo vo) {
        //use mybatus plus's paging plugin,mbatis plus is very popular in China even all over the world
        Page<BpmnConfVo> page = PageUtils.getPageByPageDto(pageDto);
        List<BpmnConfVo> bpmnConfVos = this.getMapper().selectPageList(page, vo);
        if (bpmnConfVos==null) {
            return PageUtils.getResultAndPage(page);
        }
        if (vo.getIsOutSideProcess() == 1){
            List<BpmProcessAppApplication> bizAppList = bpmProcessAppApplicationService.selectApplicationList();
            Map<String, String>  bizAppMap= bizAppList
                    .stream()
                    .collect(Collectors.toMap(BpmProcessAppApplication::getProcessKey, BpmProcessAppApplication::getTitle));
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
                    .collect(Collectors.toMap(DIYProcessInfoDTO::getKey, DIYProcessInfoDTO::getValue));
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
     * preview the process's information
     *
     * @param params
     * @return
     */
    private PreviewNode getPreviewNode(String params, Boolean isStartPagePreview) {

        BusinessDataVo dataVo = JSON.parseObject(params, BusinessDataVo.class);

        BpmnConfVo detail;
        if (isStartPagePreview) {
            detail = detailByFormCode(dataVo.getFormCode());
        } else {
            detail = detail(dataVo.getBpmnCode());
        }

        JSONObject object = JSON.parseObject(params);
        object.put("formCode", detail.getFormCode());

        BusinessDataVo vo = formFactory.dataFormConversion(JSON.toJSONString(object),null);
        vo.setIsOutSideAccessProc(Objects.equals(1,detail.getIsOutSideProcess()));
        vo.setIsLowCodeFlow(detail.getIsLowCodeFlow());
        vo.setBpmnConfVo(detail);
        //set a flag to indicate whether is a start page preview
        vo.setIsStartPagePreview(isStartPagePreview);

        BpmnStartConditionsExtendVo bpmnStartConditionsExtendVo = new BpmnStartConditionsExtendVo();
        bpmnStartConditionsExtendVo.setLowCodeFlow(true);
        //set start user information
        String startUserId;
        if (isStartPagePreview) {
            if (dataVo.getIsOutSideAccessProc()) {
                startUserId = dataVo.getStartUserId();
            } else {
                startUserId = SecurityUtils.getLogInEmpIdStr();
                vo.setStartUserId(startUserId);
            }

        } else {
            startUserId = vo.getStartUserId();
            if (ObjectUtils.isEmpty(startUserId)){
                vo.setStartUserId(SecurityUtils.getLogInEmpNameSafe());
            }
        }
        if (!ObjectUtils.isEmpty(startUserId)) {
            bpmnStartConditionsExtendVo.setStartUserId(startUserId);
            //todo set startcondition
        }

        BpmnStartConditionsVo bpmnStartConditionsVo = new BpmnStartConditionsVo();
        if(dataVo.getIsOutSideAccessProc()){
            //set conditions before preview
            bpmnStartConditionsVo.setTemplateMarkIds(dataVo.getTemplateMarkIds());
            bpmnStartConditionsVo.setOutSideType(dataVo.getOutSideType());
            //set embedded nodes
            bpmnStartConditionsVo.setEmbedNodes(dataVo.getEmbedNodes());
            //set flag to indicate it is a outside process
            bpmnStartConditionsVo.setIsOutSideAccessProc(true);
            bpmnStartConditionsVo.setStartUserId(dataVo.getStartUserId());
        }else{
            //call business logic to set start up preview conditions
            bpmnStartConditionsVo = formFactory.getFormAdaptor(vo).previewSetCondition(vo);
        }


        BeanUtils.copyProperties(bpmnStartConditionsExtendVo, bpmnStartConditionsVo, StrUtils.getNullPropertyNames(bpmnStartConditionsExtendVo));
        bpmnStartConditionsVo.setApproversList(dataVo.getApproversList());
        bpmnStartConditionsVo.setPreview(true);
        BpmnConfVo bpmnConfVo = getBpmnConfVo(bpmnStartConditionsVo, detail);
        PreviewNode previewNode = new PreviewNode();
        previewNode.setBpmnName(detail.getBpmnName());
        previewNode.setFormCode(detail.getFormCode());
        previewNode.setBpmnNodeList(setNodeFromV2(bpmnConfVo.getNodes()));
        previewNode.setDeduplicationType(bpmnConfVo.getDeduplicationType());
        previewNode.setDeduplicationTypeName(DeduplicationTypeEnum.getDescByCode(bpmnConfVo.getDeduplicationType()));

        String currentNodeIdStr= bpmVerifyInfoBizService.findCurrentNodeIds(vo.getProcessNumber());
        previewNode.setCurrentNodeId(currentNodeIdStr);

        List<String> currentNodeIds = Lists.newArrayList(currentNodeIdStr.split(","));
        List<BpmnNodeVo> bpmnNodeList = previewNode.getBpmnNodeList();
        Map<String, BpmnNodeVo> bpmnNodeVoMap = bpmnNodeList.stream().collect(Collectors.toMap(BpmnNodeVo::getNodeId, b -> b, (v1, v2) -> v1));
        List<String> nodeToResults=new ArrayList<>();
        processNodeToRecursively(currentNodeIds,bpmnNodeVoMap,nodeToResults);
        previewNode.setAfterNodeIds(nodeToResults);
        List<String> nodeFromResults=new ArrayList<>();
        Set<String> allNodeIds = bpmnNodeVoMap.keySet();
        allNodeIds.stream().filter(o -> !nodeToResults.contains(o)&&!currentNodeIds.contains(o)).forEach(nodeFromResults::add);
        previewNode.setBeforeNodeIds(nodeFromResults);
        return previewNode;

    }

    /**
     * 添加节点
     *
     * @param bpmnNodeVo
     * @param bpmnNodeList
     * @param bpmVerifyInfoVos
     */
    private void addBpmVerifyInfoVo(BpmnNodeVo bpmnNodeVo, List<BpmnNodeVo> bpmnNodeList, List<BpmVerifyInfoVo> bpmVerifyInfoVos) {
        Map<String, String> verifyMap = getVerifyMap(bpmnNodeVo);
        bpmVerifyInfoVos.add(BpmVerifyInfoVo
                .builder()
                .taskName(verifyMap.get("taskName"))
                .verifyUserName(verifyMap.get("verifyUserName"))
                .build());
        if (!ObjectUtils.isEmpty(bpmnNodeVo.getNodeTo())) {
            BpmnNodeVo nextBpmnNodeVo = bpmnNodeList.stream().filter(o -> o.getNodeFrom().equals(bpmnNodeVo.getNodeId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(nextBpmnNodeVo)) {
                addBpmVerifyInfoVo(nextBpmnNodeVo, bpmnNodeList, bpmVerifyInfoVos);
            }
        }
    }

    /**
     * query node's assignee and taskName
     *
     * @param bpmnNodeVo
     * @return
     */
    private Map<String, String> getVerifyMap(BpmnNodeVo bpmnNodeVo) {
        Map<String, String> verifyMap = Maps.newHashMap();
        String verifyUserName = StringUtils.EMPTY;
        String taskName = bpmnNodeVo.getNodePropertyName();
        if (bpmnNodeVo.getParams().getParamType().equals(BPMN_NODE_PARAM_SINGLE.getCode())) {
            //single approver
            verifyUserName = bpmnNodeVo.getParams().getAssignee().getAssigneeName();
            taskName = Optional.ofNullable(taskName)
                    .orElse(bpmnNodeVo.getParams().getAssignee().getElementName());
        } else if (bpmnNodeVo.getParams().getParamType().equals(BPMN_NODE_PARAM_MULTIPLAYER.getCode()) ||
                bpmnNodeVo.getParams().getParamType().equals(BPMN_NODE_PARAM_MULTIPLAYER_SORT.getCode())) {
            //multi user or multi user in sequence
            List<BpmnNodeParamsAssigneeVo> assigneeList = bpmnNodeVo.getParams().getAssigneeList();
            verifyUserName = StringUtils.join(assigneeList
                    .stream()
                    .map(BpmnNodeParamsAssigneeVo::getAssigneeName)
                    .collect(Collectors.toList()), ",");
            taskName = Optional.ofNullable(taskName)
                    .orElse(StringUtils.join(assigneeList
                            .stream()
                            .map(BpmnNodeParamsAssigneeVo::getElementName)
                            .distinct()
                            .collect(Collectors.toList()), ","));
        }
        verifyMap.put("verifyUserName", verifyUserName);
        verifyMap.put("taskName", taskName);
        return verifyMap;
    }

    private void processNodeToRecursively(List<String>currentNodeIds, Map<String, BpmnNodeVo> bpmnNodeVoMap, List<String> results){
        if(currentNodeIds.isEmpty()){
            return;
        }
        for (String currentNodeId : currentNodeIds) {
            BpmnNodeVo bpmnNodeVo = bpmnNodeVoMap.get(currentNodeId);
            if (bpmnNodeVo != null) {
                List<String> nodeTo = bpmnNodeVo.getNodeTo();
                if(!CollectionUtils.isEmpty(nodeTo)){
                    results.addAll(nodeTo);
                    processNodeToRecursively(nodeTo,bpmnNodeVoMap,results);
                }
            }
        }
    }
    private void treatParallelGateWayRecursively(BpmnNodeVo outerMostParallelGatewayNode, BpmnNodeVo itsPrevNode,
                                                 BpmnNodeVo itsAggregationNode, Map<String, BpmnNodeVo> mapNodes, List<BpmnNodeVo> results,
                                                 Set<String> alreadyProcessNodeIds) {

        String aggregationNodeNodeId = itsAggregationNode.getNodeId();
        List<String> nodeTos = outerMostParallelGatewayNode.getNodeTo();
        outerMostParallelGatewayNode.setNodeFrom(itsPrevNode.getNodeId());
        itsAggregationNode.setNodeFrom(outerMostParallelGatewayNode.getNodeId());
        results.add(outerMostParallelGatewayNode);
        results.add(itsAggregationNode);
        alreadyProcessNodeIds.add(outerMostParallelGatewayNode.getNodeId());
        alreadyProcessNodeIds.add(itsAggregationNode.getNodeId());
        for (String nodeTo : nodeTos) {
            BpmnNodeVo prevNode = outerMostParallelGatewayNode;
            BpmnNodeVo currentNodeVo = mapNodes.get(nodeTo);

            for (BpmnNodeVo nodeVo = currentNodeVo; nodeVo != null && !nodeVo.getNodeId().equals(aggregationNodeNodeId); nodeVo = mapNodes.get(nodeVo.getParams().getNodeTo())) {
                if(alreadyProcessNodeIds.contains(nodeVo.getNodeId())){
                    continue;
                }

                if (NodeTypeEnum.NODE_TYPE_PARALLEL_GATEWAY.getCode().equals(nodeVo.getNodeType())) {
                    BpmnNodeVo aggregationNode = BpmnUtils.getAggregationNode(nodeVo, mapNodes.values());
                    treatParallelGateWayRecursively(nodeVo, prevNode,aggregationNode, mapNodes, results, alreadyProcessNodeIds);
                }

                if (BPMN_NODE_PARAM_SINGLE.getCode().equals(nodeVo.getParams().getParamType())) {
                    nodeVo.getParams().setAssigneeList(Collections.singletonList(nodeVo.getParams().getAssignee()));
                }
                nodeVo.setNodePropertyName(NodePropertyEnum.getDescByCode(nodeVo.getNodeProperty()));
                if (StringUtils.isBlank(nodeVo.getParams().getNodeTo())) {
                    nodeVo.setNodeFrom(prevNode.getNodeId());
                    results.add(nodeVo);
                    alreadyProcessNodeIds.add(nodeVo.getNodeId());
                    break;
                }
                if (results.size() > mapNodes.values().size()) {
                    log.info("error occur while set nodeFrom info,nodeList:{}", JSON.toJSONString(mapNodes.values()));
                    throw new AFBizException("999", "nodeId数据错误");
                }
                nodeVo.setNodeFrom(prevNode.getNodeId());
                results.add(nodeVo);
                alreadyProcessNodeIds.add(nodeVo.getNodeId());
                prevNode = nodeVo;
            }
        }
    }

    /**
     * iterate the nodelist and use a map structure to store them
     *
     * @param nodeList
     * @param nodeIdMapNode
     * @return
     */
    private static BpmnNodeVo getNodeMapAndStartNode(List<BpmnNodeVo> nodeList, Map<String, BpmnNodeVo> nodeIdMapNode) {
        BpmnNodeVo startNode = null;
        boolean existEnd = false;
        for (BpmnNodeVo bpmnNodeVo : nodeList) {
            nodeIdMapNode.put(bpmnNodeVo.getNodeId(), bpmnNodeVo);
            if (NodeTypeEnum.NODE_TYPE_START.getCode().equals(bpmnNodeVo.getNodeType())) {
                if (startNode == null) {
                    startNode = bpmnNodeVo;
                } else {
                    log.info("has more than one start up user while previewing the process,nodeId:{}", bpmnNodeVo.getNodeId());
                    throw new AFBizException("999", "has more than 1 start up node");
                }
            }
            if (bpmnNodeVo.getParams() == null || StringUtils.isBlank(bpmnNodeVo.getParams().getNodeTo())) {
                existEnd = true;
            }
        }
        if (!existEnd) {
            log.info("has no end node while previewing the process,nodeList:{}", JSON.toJSONString(nodeList));
            throw new AFBizException("has not end node while previewing the process");
        }
        return startNode;
    }

    /**
     * format the floating direction,set assignees and deduplication
     *
     * @param bpmnStartConditions
     * @param bpmnConfVo
     * @return
     */
    private BpmnConfVo getBpmnConfVo(BpmnStartConditionsVo bpmnStartConditions, BpmnConfVo bpmnConfVo) {


        //1. Format the process,filter it by condition
        bpmnStartFormatFactory.formatBpmnConf(bpmnConfVo,bpmnStartConditions);


        //2、set consignees information and finally determine the flow's direction
        bpmnPersonnelFormat.formatPersonnelsConf(bpmnConfVo, bpmnStartConditions);


        //3. to determine whether it is necessary to deduplication
        if (!bpmnConfVo.getDeduplicationType().equals(DEDUPLICATION_TYPE_NULL.getCode())) {
            BpmnDeduplicationFormat bpmnDeduplicationFormat = SpringBeanUtils.getBean(BpmnDeduplicationFormatImpl.class);
            if (bpmnConfVo.getDeduplicationType().equals(DEDUPLICATION_TYPE_FORWARD.getCode())) {
                //deduplication forward
                bpmnDeduplicationFormat.forwardDeduplication(bpmnConfVo, bpmnStartConditions);
            } else if (bpmnConfVo.getDeduplicationType().equals(DEDUPLICATION_TYPE_BACKWARD.getCode())) {
                //deduplication backword
                bpmnDeduplicationFormat.backwardDeduplication(bpmnConfVo, bpmnStartConditions);
            }
        }

        //self chosen module deduplication
        BpmnOptionalDuplicatesAdaptor bpmnOptionalDuplicates = SpringBeanUtils.getBean(BpmnOptionalDuplicatesImpl.class);
        bpmnOptionalDuplicates.optionalDuplicates(bpmnConfVo, bpmnStartConditions);

        //4、format the nodes by pipelines
        bpmnRemoveConfFormatFactory.removeBpmnConf(bpmnConfVo,bpmnStartConditions);
        if(BpmnConfFlagsEnum.hasFlag(bpmnConfVo.getExtraFlags(),BpmnConfFlagsEnum.HAS_COPY)){
            for (BpmnNodeVo node : bpmnConfVo.getNodes()) {
                //copy nodes have already removed,and its forwarded list assigned to its next node
                if (!NodeTypeEnum.NODE_TYPE_COPY.getCode().equals(node.getNodeType())&&!CollectionUtils.isEmpty(node.getEmpToForwardList())) {
                    List<BaseIdTranStruVo> empToForwardList = node.getEmpToForwardList();
                    List<BpmProcessForward> processForwardList=new ArrayList<>(node.getEmpToForwardList().size());
                    boolean lastNodeForward = node.isLastNodeForward();
                    for (BaseIdTranStruVo baseIdTranStruVo : empToForwardList) {
                        BpmProcessForward bpmProcessForward = BpmProcessForward.builder()
                                .createTime(new Date())
                                .createUserId(SecurityUtils.getLogInEmpId())
                                .forwardUserId(baseIdTranStruVo.getId())
                                .ForwardUserName(baseIdTranStruVo.getName())
                                .processNumber(bpmnStartConditions.getProcessNum())
                                .nodeId(String.valueOf(node.getId()))
                                .isDel(1)//it is invalid at first,then set it to be valid
                                //at this moment,we can not get procInstId,update it later
                                .build();
                        if(lastNodeForward){
                            bpmProcessForward.setNodeId(StringConstants.LASTNODE_COPY);
                        }
                        processForwardList.add(bpmProcessForward);
                    }
                    processForwardService.saveBatch(processForwardList);
                }
            }
        }
        return bpmnConfVo;
    }
    private BpmnConfVo formatConfVo(BpmnConfVo confVo){
        if(confVo==null){
            throw new AFBizException("has not confVo");
        }
        List<BpmnNodeVo> nodes = confVo.getNodes();
        if(CollectionUtils.isEmpty(nodes)){
            throw new AFBizException("confVo has empty nodes");
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
                .selectList(AFWrappers.<BpmnNode>lambdaTenantQuery()
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
                        throw new AFBizException("can not find parallel gateway's aggregation node!");
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
                Wrappers.<BpmnViewPageButton>lambdaQuery()
                        .eq(BpmnViewPageButton::getConfId,bpmnConfVo.getId()));

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
                        AFWrappers.<BpmnTemplate>lambdaTenantQuery()
                                .in(BpmnTemplate::getNodeId, ids))
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
                        AFWrappers.<BpmnApproveRemind>lambdaTenantQuery()
                                .in(BpmnApproveRemind::getNodeId, ids))
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
        List<BpmnNodeLabel> nodeLabels = nodeLabelsService.list(AFWrappers.<BpmnNodeLabel>lambdaTenantQuery().in(BpmnNodeLabel::getNodeId,ids));
        return nodeLabels.stream().collect(Collectors.groupingBy(BpmnNodeLabel::getNodeId));
    }
    /**
     * get node signup conf map
     *
     * @param idList
     * @return
     */
    private Map<Long, BpmnNodeSignUpConf> getBpmnNodeSignUpConfMap(List<Long> idList) {
        return bpmnNodeSignUpConfService.getBaseMapper().selectList(AFWrappers.<BpmnNodeSignUpConf>lambdaTenantQuery()
                        .in(BpmnNodeSignUpConf::getBpmnNodeId, idList))
                .stream()
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
                        AFWrappers.<BpmnNodeButtonConf>lambdaTenantQuery()
                                .in(BpmnNodeButtonConf::getBpmnNodeId, idList))
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
                        AFWrappers.<BpmnNodeLfFormdataFieldControl>lambdaTenantQuery()
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
     * format outside process form code
     *
     * @param bpmnConfVo
     * @return
     */
    private String formatOutSideFormCode(BpmnConfVo bpmnConfVo) {
        String formCode = bpmnConfVo.getFormCode();
        return formCode.substring(formCode.indexOf(linkMark) + 1);
    }
    /**
     * set view page's buttons
     *
     * @param bpmnConfVo
     * @param bpmnConfCommonVo
     */
    private void setViewPageButtons(BpmnConfVo bpmnConfVo, BpmnConfCommonVo bpmnConfCommonVo) {
        bpmnConfCommonVo.setViewPageButtons(BpmnConfViewPageButtonVo
                .builder()
                .viewPageStart(bpmnConfVo.getViewPageButtons().getViewPageStart()
                        .stream()
                        .map(o -> BpmnConfCommonButtonPropertyVo
                                .builder()
                                .buttonType(o)
                                .buttonName(ButtonTypeEnum.getDescByCode(o))
                                .build())
                        .collect(Collectors.toList()))
                .viewPageOther(bpmnConfVo.getViewPageButtons().getViewPageOther()
                        .stream()
                        .map(o -> {
                            return BpmnConfCommonButtonPropertyVo
                                    .builder()
                                    .buttonType(o)
                                    .buttonName(ButtonTypeEnum.getDescByCode(o))
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build());
    }
}
