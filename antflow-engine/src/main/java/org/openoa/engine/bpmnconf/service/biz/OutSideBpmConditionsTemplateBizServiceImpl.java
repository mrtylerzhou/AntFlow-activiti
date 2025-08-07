package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.*;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.constant.enus.ConditionTypeEnum;
import org.openoa.engine.bpmnconf.service.impl.*;
import org.openoa.engine.bpmnconf.service.interf.biz.OutSideBpmConditionsTemplateBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.*;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.openoa.engine.vo.OutSideBpmConditionsTemplateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_TEMPLATE;

@Service
public class OutSideBpmConditionsTemplateBizServiceImpl implements OutSideBpmConditionsTemplateBizService {

    @Autowired
    private OutSideBpmBusinessPartyService outSideBpmBusinessPartyService;

    @Autowired
    private OutSideBpmBaseServiceImpl outSideBpmBaseService;

    @Autowired
    private AfUserService employeeService;

    @Autowired
    @Lazy
    private BpmnConfService bpmnConfService;

    @Autowired
    private BpmnNodeService bpmnNodeService;

    @Autowired
    private BpmnNodeConditionsConfService bpmnNodeConditionsConfService;

    @Autowired
    private BpmnNodeConditionsParamConfService bpmnNodeConditionsParamConfService;

    @Autowired
    private BpmProcessAppApplicationService bpmProcessAppApplicationService;

    /**
     * get condition templates by page
     * @param pageDto
     * @param vo
     * @return
     */
    @Override
    public ResultAndPage<OutSideBpmConditionsTemplateVo> listPage(PageDto pageDto, OutSideBpmConditionsTemplateVo vo) {
        //1、user mybatis plus paging plugin,mybatis plus is a famous orm framework based on mybatis and very popular in China even all over the world
        Page<OutSideBpmConditionsTemplateVo> page = PageUtils.getPageByPageDto(pageDto);


        //query and set login employee business party list,control query data permission
        List<OutSideBpmBusinessPartyVo> emplBusinessPartys = outSideBpmBaseService.getEmplBusinessPartys(StringUtils.EMPTY, ADMIN_PERSONNEL_TYPE_TEMPLATE.getPermCode());
        vo.setBusinessPartyIds(emplBusinessPartys.stream().map(OutSideBpmBusinessPartyVo::getId).collect(Collectors.toList()));

        //querying business party's condition template vo
        List<OutSideBpmConditionsTemplateVo> outSideBpmConditionsTemplateVos = getMapper().selectPageList(page, vo);


        if (CollectionUtils.isEmpty(outSideBpmConditionsTemplateVos)) {
            return PageUtils.getResultAndPage(page);
        }


        //query business party's info,for mapping purpose
        Map<Long, OutSideBpmBusinessParty> outSideBpmBusinessPartyMap = outSideBpmBusinessPartyService.getBaseMapper().selectBatchIds(outSideBpmConditionsTemplateVos
                        .stream()
                        .map(OutSideBpmConditionsTemplateVo::getBusinessPartyId)
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(OutSideBpmBusinessParty::getId, o -> o));

        //qeury employee info,for mapping purpose
        Map<String, Employee> employeeMap = employeeService.getEmployeeDetailByIds(outSideBpmConditionsTemplateVos
                        .stream()
                        .map(OutSideBpmConditionsTemplateVo::getCreateUserId)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Employee::getId, o -> o));


        for (OutSideBpmConditionsTemplateVo outSideBpmConditionsTemplateVo : outSideBpmConditionsTemplateVos) {
            OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyMap.get(outSideBpmConditionsTemplateVo.getBusinessPartyId());
            if (outSideBpmBusinessParty!=null) {
                outSideBpmConditionsTemplateVo.setBusinessPartyMark(outSideBpmBusinessParty.getBusinessPartyMark());
                outSideBpmConditionsTemplateVo.setBusinessPartyName(outSideBpmBusinessParty.getName());
            }

            Employee employee = employeeMap.get(outSideBpmConditionsTemplateVo.getCreateUserId());
            if (employee!=null) {
                outSideBpmConditionsTemplateVo.setCreateUserName(employee.getUsername());
            }


            if (outSideBpmConditionsTemplateVo.getApplicationId()!=null && !outSideBpmConditionsTemplateVo.getApplicationId().equals(0)) {
                BpmProcessAppApplication bpmProcessAppApplication = bpmProcessAppApplicationService.getById(outSideBpmConditionsTemplateVo.getApplicationId());
                if (bpmProcessAppApplication!=null) {
                    outSideBpmConditionsTemplateVo.setApplicationFormCode(bpmProcessAppApplication.getProcessKey());
                    outSideBpmConditionsTemplateVo.setApplicationName(bpmProcessAppApplication.getTitle());
                }
            }
        }


        page.setRecords(outSideBpmConditionsTemplateVos);


        return PageUtils.getResultAndPage(page);
    }

    /**
     *
     *
     * @param businessPartyMark
     * @return
     */
    @Override
    public List<OutSideBpmConditionsTemplateVo> selectListByPartMark(String businessPartyMark) {

        OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getBaseMapper().selectOne(new QueryWrapper<OutSideBpmBusinessParty>()
                .eq("business_party_mark", businessPartyMark));

        if (outSideBpmBusinessParty!=null) {
            List<OutSideBpmConditionsTemplate> outSideBpmConditionsTemplates = this.getService().list(new QueryWrapper<OutSideBpmConditionsTemplate>()
                    .eq("is_del", 0)
                    .eq("business_party_id", outSideBpmBusinessParty.getId()));

            if (!CollectionUtils.isEmpty(outSideBpmConditionsTemplates)) {

                //收集应用Id用以查询翻译信息
                Set<Integer> applicationIds = Sets.newHashSet();


                //key=applicationId;value=List<OutSideBpmConditionsTemplate>
                Multimap<Integer, OutSideBpmConditionsTemplate> conditionsTemplateMultimap = ArrayListMultimap.create();
                for (OutSideBpmConditionsTemplate outSideBpmConditionsTemplate : outSideBpmConditionsTemplates) {

                    if (outSideBpmConditionsTemplate.getApplicationId().equals(0)) {
                        continue;
                    }


                    conditionsTemplateMultimap.put(outSideBpmConditionsTemplate.getApplicationId(), outSideBpmConditionsTemplate);


                    //add application id to list
                    applicationIds.add(outSideBpmConditionsTemplate.getApplicationId());
                }

                if (CollectionUtils.isEmpty(applicationIds)) {
                    return Collections.EMPTY_LIST;
                }


                //query process application
                Map<Integer, BpmProcessAppApplication> applicationMap = bpmProcessAppApplicationService.getBaseMapper().selectBatchIds(new ArrayList<>(applicationIds))
                        .stream()
                        .collect(Collectors.toMap(BpmProcessAppApplication::getId, o -> o));

                List<OutSideBpmConditionsTemplateVo> returnList = Lists.newArrayList();

                for (Integer key : conditionsTemplateMultimap.keySet()) {
                    Collection<OutSideBpmConditionsTemplate> outSideBpmConditionsTemplatesList = conditionsTemplateMultimap.get(key);
                    BpmProcessAppApplication bpmProcessAppApplication = Optional.ofNullable(applicationMap.get(key)).orElse(new BpmProcessAppApplication());
                    returnList.add(OutSideBpmConditionsTemplateVo
                            .builder()
                            .applicationFormCode(bpmProcessAppApplication.getProcessKey())
                            .applicationName(bpmProcessAppApplication.getTitle())
                            .templates(outSideBpmConditionsTemplatesList
                                    .stream()
                                    .map(o -> OutSideBpmConditionsTemplateVo
                                            .builder()
                                            .templateMark(o.getTemplateMark())
                                            .templateName(o.getTemplateName())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build());
                }

                return returnList.stream().filter(o -> !StringUtil.isEmpty(o.getApplicationName())).collect(Collectors.toList());

            }
        }

        return Collections.EMPTY_LIST;

    }

    /**
     * query details by id
     *
     * @param id
     * @return
     */
    @Override
    public OutSideBpmConditionsTemplateVo detail(Integer id) {

        OutSideBpmConditionsTemplate outSideBpmConditionsTemplate = this.getMapper().selectById(id);

        OutSideBpmConditionsTemplateVo vo = new OutSideBpmConditionsTemplateVo();

        BeanUtils.copyProperties(outSideBpmConditionsTemplate, vo);

        OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getBaseMapper().selectById(vo.getBusinessPartyId());

        vo.setBusinessPartyMark(outSideBpmBusinessParty.getBusinessPartyMark());
        vo.setBusinessPartyName(outSideBpmBusinessParty.getName());

        vo.setIsUsed(templateIsUsed(id, outSideBpmBusinessParty));


        if (outSideBpmConditionsTemplate.getApplicationId()!=null) {
            BpmProcessAppApplication bpmProcessAppApplication = bpmProcessAppApplicationService.getBaseMapper().selectById(outSideBpmConditionsTemplate.getApplicationId());
            vo.setApplicationFormCode(bpmProcessAppApplication.getProcessKey());
            vo.setApplicationName(bpmProcessAppApplication.getTitle());
        }

        return vo;
    }
    /**
     * remove
     *
     * @param id
     */
    @Override
    public void delete(Integer id) {

        OutSideBpmConditionsTemplate outSideBpmConditionsTemplate = this.getService().getById(id);

        OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getById(outSideBpmConditionsTemplate.getBusinessPartyId());

        if (templateIsUsed(id, outSideBpmBusinessParty)) {
            throw new AFBizException("审批流程中正使用此条件模板，无法删除");
        }

        this.getService().updateById(OutSideBpmConditionsTemplate
                .builder()
                .id(id.longValue())
                .isDel(1)
                .build());
    }

    /**
     * check whether template is used
     *
     * @param id
     * @return
     */
    private Boolean templateIsUsed(Integer id, OutSideBpmBusinessParty outSideBpmBusinessParty) {

        List<BpmnConf> bpmnConfs = bpmnConfService.list(new QueryWrapper<BpmnConf>()
                .eq("business_party_id", outSideBpmBusinessParty.getId())
                .eq("is_del", 0)
                .eq("effective_status", 1));

        if (!CollectionUtils.isEmpty(bpmnConfs)) {
            List<BpmnNode> bpmnNodes = bpmnNodeService.list(new QueryWrapper<BpmnNode>()
                    .in("conf_id", bpmnConfs
                            .stream()
                            .map(BpmnConf::getId)
                            .collect(Collectors.toList()))
                    .in("node_type", Lists.newArrayList(NodeTypeEnum.NODE_TYPE_CONDITIONS.getCode(), NodeTypeEnum.NODE_TYPE_OUT_SIDE_CONDITIONS.getCode())));

            if (!CollectionUtils.isEmpty(bpmnNodes)) {
                List<BpmnNodeConditionsConf> bpmnNodeConditionsConfs = bpmnNodeConditionsConfService.list(new QueryWrapper<BpmnNodeConditionsConf>()
                        .in("bpmn_node_id", bpmnNodes
                                .stream()
                                .map(BpmnNode::getId)
                                .collect(Collectors.toList())));

                if (!CollectionUtils.isEmpty(bpmnNodeConditionsConfs)) {

                    ConditionTypeEnum conditionTemplatemark = ConditionTypeEnum.CONDITION_TEMPLATEMARK;

                    List<BpmnNodeConditionsParamConf> bpmnNodeConditionsParamConfs = bpmnNodeConditionsParamConfService.list(new QueryWrapper<BpmnNodeConditionsParamConf>()
                            .in("bpmn_node_conditions_id", bpmnNodeConditionsConfs
                                    .stream()
                                    .map(BpmnNodeConditionsConf::getId)
                                    .collect(Collectors.toList()))
                            .eq("condition_param_type", conditionTemplatemark.getCode()));

                    if (!CollectionUtils.isEmpty(bpmnNodeConditionsParamConfs)) {
                        List<Integer> usedTempList = Lists.newArrayList();
                        for (BpmnNodeConditionsParamConf bpmnNodeConditionsParamConf : bpmnNodeConditionsParamConfs) {
                            List<Integer> confTempList = (List<Integer>) JSON.parseArray(bpmnNodeConditionsParamConf.getConditionParamJsom(), conditionTemplatemark.getFieldCls());
                            usedTempList.addAll(confTempList);
                        }
                        if (usedTempList.contains(id)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

}
