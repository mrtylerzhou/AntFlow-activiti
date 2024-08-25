package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.openoa.base.constant.enums.*;
import org.openoa.base.dto.PageDto;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.*;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.adp.bpmnnodeadp.BpmnNodeAdaptor;
import org.openoa.engine.bpmnconf.confentity.*;
import org.openoa.engine.bpmnconf.constant.enus.BpmnNodeAdpConfEnum;
import org.openoa.engine.bpmnconf.constant.enus.EventTypeEnum;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmProcessNameServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnViewPageButtonBizServiceImpl;
import org.openoa.engine.factory.IAdaptorFactory;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

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
        //可配置流程绑定消息模板
        bpmnConfNoticeTemplateService.insert(bpmnCode);
        Long confId = Optional.ofNullable(bpmnConf.getId()).orElse(0L);

        bpmnViewPageButtonBizService.editBpmnViewPageButton(bpmnConfVo, confId);

        bpmnTemplateService.editBpmnTemplate(bpmnConfVo, confId);

        List<BpmnNodeVo> confNodes = bpmnConfVo.getNodes();
        for (BpmnNodeVo bpmnNodeVo : confNodes) {
            if (bpmnNodeVo.getNodeType().intValue() == NODE_TYPE_APPROVER.getCode()
                    && ObjectUtils.isEmpty(bpmnNodeVo.getNodeProperty())) {
                throw new JiMuBizException("apporver node has no property,can not be saved！");
            }


            //if the node has no property,the node property default is "1-no property"
            bpmnNodeVo.setNodeProperty(Optional.ofNullable(bpmnNodeVo.getNodeProperty())
                    .orElse(1));

            BpmnNode bpmnNode = new BpmnNode();
            BeanUtils.copyProperties(bpmnNodeVo, bpmnNode);
            bpmnNode.setConfId(confId);
            bpmnNode.setCreateTime(new Date());
            bpmnNode.setCreateUser(SecurityUtils.getLogInEmpNameSafe());
            bpmnNodeService.getBaseMapper().insert(bpmnNode);

            Long bpmnNodeId = Optional.ofNullable(bpmnNode.getId()).orElse(0L);

            //edit node to
            bpmnNodeToService.editNodeTo(bpmnNodeVo, bpmnNodeId);

            //edit node's button conf
            bpmnNodeButtonConfService.editButtons(bpmnNodeVo, bpmnNodeId);

            //edit node sign up
            bpmnNodeSignUpConfService.editSignUpConf(bpmnNodeVo, bpmnNodeId);


            bpmnNodeVo.setId(bpmnNodeId);
            bpmnNodeVo.setConfId(confId);
            BpmnNodeAdpConfEnum bpmnNodeAdpConfEnum = getBpmnNodeAdpConfEnum(bpmnNodeVo);

            //if can not get the node's adapter,continue
            if (ObjectUtils.isEmpty(bpmnNodeAdpConfEnum)) {
                continue;
            }

            //edit in node notice template
            bpmnTemplateService.editBpmnTemplate(bpmnNodeVo);


            //edit in node approver remind conf
            bpmnApproveRemindService.editBpmnApproveRemind(bpmnNodeVo);

            //get node adaptor
            BpmnNodeAdaptor bpmnNodeAdaptor = getBpmnNodeAdaptor(bpmnNodeAdpConfEnum);

            //then edit the node
            bpmnNodeAdaptor.editBpmnNode(bpmnNodeVo);
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
        BpmnConf bpmnConf = this.getBaseMapper().selectOne(new QueryWrapper<BpmnConf>()
                .eq("bpmn_code", bpmnCode));
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
        if (bpmnConfVo.getIsOutSideProcess() == 1) {
            //query and set business party's call url
            OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = outSideBpmCallbackUrlConfService
                    .getOutSideBpmCallbackUrlConf(bpmnConf.getId(), bpmnConf.getBusinessPartyId());
            if (outSideBpmCallbackUrlConf!=null) {
                bpmnConfVo.setBpmConfCallbackUrl(outSideBpmCallbackUrlConf.getBpmConfCallbackUrl());//process config call back url
                bpmnConfVo.setBpmFlowCallbackUrl(outSideBpmCallbackUrlConf.getBpmFlowCallbackUrl());//process flow call back url
            }


            //query business party's info
            OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getById(bpmnConf.getBusinessPartyId());

            //format outside form code and reset value
            String formCode = formatOutSideFormCode(bpmnConfVo);
            bpmnConfVo.setFormCode(formCode);

            //set business party's name
            bpmnConfVo.setBusinessPartyName(outSideBpmBusinessParty.getName());

            //set business party's mark,mark just like record is a unique identifier for a certain business party,but for human readability
            bpmnConfVo.setBusinessPartyMark(outSideBpmBusinessParty.getBusinessPartyMark());

            //set business party's business type
            bpmnConfVo.setType(outSideBpmBusinessParty.getType());

            //query business application url
            BpmProcessAppApplicationVo applicationUrl = applicationService.getApplicationUrl(outSideBpmBusinessParty.getBusinessPartyMark(), formCode);


            //set view url,submit url and condition url
            if (applicationUrl!=null) {
                bpmnConfVo.setViewUrl(applicationUrl.getLookUrl());//view url
                bpmnConfVo.setSubmitUrl(applicationUrl.getSubmitUrl());//submit url
                bpmnConfVo.setConditionsUrl(applicationUrl.getConditionUrl());//condition url
                bpmnConfVo.setAppId(applicationUrl.getId());//关联应用Id
                conditionsUrl = applicationUrl.getConditionUrl();
            }
        }

        //set nodes
        List<BpmnNode> bpmnNodes = bpmnNodeService.getBaseMapper().selectList(new QueryWrapper<BpmnNode>()
                .eq("conf_id", bpmnConf.getId())
                .eq("is_del", 0));
        bpmnConfVo.setNodes(getBpmnNodeVoList(bpmnNodes, conditionsUrl));
        if (!ObjectUtils.isEmpty(bpmnConfVo.getNodes())) {
            bpmnConfVo.getNodes().forEach(node -> node.setFormCode(bpmnConfVo.getFormCode()));
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
    private void setBpmnTemplateVos(BpmnConfVo bpmnConfVo) {
        bpmnConfVo.setTemplateVos(
                bpmnTemplateService.getBaseMapper().selectList(
                        new QueryWrapper<BpmnTemplate>()
                                .eq("conf_id", bpmnConfVo.getId())
                                .eq("is_del", 0)
                                .isNull("node_id"))
                        .stream()
                        .map(o -> {
                            BpmnTemplateVo vo = new BpmnTemplateVo();
                            BeanUtils.copyProperties(o, vo);
                            buildBpmnTemplateVo(vo);
                            return vo;
                        }).collect(Collectors.toList()));
    }

    private void buildBpmnTemplateVo(BpmnTemplateVo vo) {
        vo.setEventValue(EventTypeEnum.getDescByByCode(vo.getEvent()));
        if (!ObjectUtils.isEmpty(vo.getInforms())) {
            vo.setInformIdList(
                    Arrays.stream(vo.getInforms().split(","))
                            .map(Long::parseLong)
                            .collect(Collectors.toList()));
            vo.setInformList(vo.getInformIdList()
                    .stream()
                    .map(o -> BaseIdTranStruVo
                            .builder()
                            .id(o)
                            .name(EventTypeEnum.getDescByByCode(o.intValue()))
                            .build())
                    .collect(Collectors.toList()));
        }
        if (!ObjectUtils.isEmpty(vo.getEmps())) {
            vo.setEmpIdList(
                    Arrays.stream(vo.getEmps().split(","))
                            .map(Long::parseLong)
                            .collect(Collectors.toList()));

            Map<String, String> employeeInfo = employeeInfoProvider.provideEmployeeInfo(AntCollectionUtil.numberToStringList(vo.getEmpIdList()));
            vo.setEmpList(vo.getEmpIdList()
                    .stream()
                    .map(o -> BaseIdTranStruVo
                            .builder()
                            .id(o)
                            .name(employeeInfo.get(o.toString()))
                            .build())
                    .collect(Collectors.toList()));
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


        Map<Long, List<String>> bpmnNodeToMap = getBpmnNodeToMap(idList);


        Map<Long, List<BpmnNodeButtonConf>> bpmnNodeButtonConfMap = getBpmnNodeButtonConfMap(idList);


        Map<Long, BpmnNodeSignUpConf> bpmnNodeSignUpConfMap = getBpmnNodeSignUpConfMap(idList);


        Map<Long, List<BpmnTemplateVo>> bpmnTemplateVoMap = getBpmnTemplateVoMap(idList);


        Map<Long, BpmnApproveRemindVo> bpmnApproveRemindVoMap = getBpmnApproveRemindVoMap(idList);

        return bpmnNodeList
                .stream()
                .map(o -> getBpmnNodeVo(o, bpmnNodeToMap, bpmnNodeButtonConfMap, bpmnNodeSignUpConfMap,
                        bpmnTemplateVoMap, bpmnApproveRemindVoMap, conditionsUrl))
                .collect(Collectors.toList());

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
                            BeanUtils.copyProperties(o, vo);
                            buildBpmnTemplateVo(vo);
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
                                     String conditionsUrl) {


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


        BpmnNodeAdpConfEnum bpmnNodeAdpConfEnum = getBpmnNodeAdpConfEnum(bpmnNodeVo);


        if (ObjectUtils.isEmpty(bpmnNodeAdpConfEnum)) {
            return bpmnNodeVo;
        }

        //get node adaptor
        BpmnNodeAdaptor bpmnNodeAdaptor = getBpmnNodeAdaptor(bpmnNodeAdpConfEnum);

        //use adaptor to format nodevo
        bpmnNodeAdaptor.formatToBpmnNodeVo(bpmnNodeVo);


        if (bpmnNodeVo.getNodeType().equals(NodeTypeEnum.NODE_TYPE_OUT_SIDE_CONDITIONS.getCode())) {
            bpmnNodeVo.setNodeType(NodeTypeEnum.NODE_TYPE_CONDITIONS.getCode());
        }

        //set sign up conf
        setBpmnNodeSignUpConf(bpmnNode, bpmnNodeSignUpConfMap, bpmnNodeVo);

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
     * get adaptor config enum
     *
     * @param bpmnNodeVo
     * @return
     */
    private BpmnNodeAdpConfEnum getBpmnNodeAdpConfEnum(BpmnNodeVo bpmnNodeVo) {

        BpmnNodeAdpConfEnum bpmnNodeAdpConfEnum;


        NodeTypeEnum nodeTypeEnumByCode = NodeTypeEnum.getNodeTypeEnumByCode(bpmnNodeVo.getNodeType());

        if (!ObjectUtils.isEmpty(nodeTypeEnumByCode)) {

            bpmnNodeAdpConfEnum = BpmnNodeAdpConfEnum.getBpmnNodeAdpConfEnumByEnum(nodeTypeEnumByCode);
        } else {

            NodePropertyEnum nodePropertyEnum = NodePropertyEnum.getNodePropertyEnumByCode(bpmnNodeVo.getNodeProperty());
            bpmnNodeAdpConfEnum = BpmnNodeAdpConfEnum.getBpmnNodeAdpConfEnumByEnum(nodePropertyEnum);
        }
        return bpmnNodeAdpConfEnum;
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


    private Map<Long, List<String>> getBpmnNodeToMap(List<Long> idList) {
        return bpmnNodeToService.getBaseMapper().selectList(
                new QueryWrapper<BpmnNodeTo>()
                        .in("bpmn_node_id", idList)
                        .eq("is_del", 0))
                .stream()
                .collect(Collectors.toMap(
                        BpmnNodeTo::getBpmnNodeId,
                        v -> Lists.newArrayList(Collections.singletonList(v.getNodeTo())),
                        (a, b) -> {
                            a.addAll(b);
                            return a;
                        }));
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
                .id(Long.parseLong(id.toString()))
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
