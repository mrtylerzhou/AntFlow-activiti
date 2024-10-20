package org.openoa.engine.bpmnconf.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.NodeTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Employee;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.AntCollectionUtil;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.confentity.*;
import org.openoa.engine.bpmnconf.constant.enus.ConditionTypeEnum;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmConditionsTemplateMapper;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.openoa.engine.vo.OutSideBpmConditionsTemplateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_TEMPLATE;


/**
 * third party process service-conditions template configuration table service implementation
 * @since 0.5
 */
@Service
public class OutSideBpmConditionsTemplateServiceImpl extends ServiceImpl<OutSideBpmConditionsTemplateMapper, OutSideBpmConditionsTemplate> {

    @Autowired
    private OutSideBpmConditionsTemplateMapper outSideBpmConditionsTemplateMapper;

    @Autowired
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;

    @Autowired
    private OutSideBpmBaseServiceImpl outSideBpmBaseService;

    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private BpmnConfServiceImpl bpmnConfService;

    @Autowired
    private BpmnNodeServiceImpl bpmnNodeService;

    @Autowired
    private BpmnNodeConditionsConfServiceImpl bpmnNodeConditionsConfService;

    @Autowired
    private BpmnNodeConditionsParamConfServiceImpl bpmnNodeConditionsParamConfService;

    @Autowired
    private BpmProcessAppApplicationServiceImpl bpmProcessAppApplicationService;


    /**
     * get condition templates by page
     * @param pageDto
     * @param vo
     * @return
     */
    public ResultAndPage<OutSideBpmConditionsTemplateVo> listPage(PageDto pageDto, OutSideBpmConditionsTemplateVo vo) {
        //1、user mybatis plus paging plugin,mybatis plus is a famous orm framework based on mybatis and very popular in China even all over the world
        Page<OutSideBpmConditionsTemplateVo> page = PageUtils.getPageByPageDto(pageDto);


        //query and set login employee business party list,control query data permission
        List<OutSideBpmBusinessPartyVo> emplBusinessPartys = outSideBpmBaseService.getEmplBusinessPartys(StringUtils.EMPTY, ADMIN_PERSONNEL_TYPE_TEMPLATE.getPermCode());
        vo.setBusinessPartyIds(emplBusinessPartys.stream().map(OutSideBpmBusinessPartyVo::getId).collect(Collectors.toList()));

        //querying business party's condition template vo
        List<OutSideBpmConditionsTemplateVo> outSideBpmConditionsTemplateVos = outSideBpmConditionsTemplateMapper.selectPageList(page, vo);


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
    public List<OutSideBpmConditionsTemplateVo> selectListByPartMark(String businessPartyMark) {

        OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getBaseMapper().selectOne(new QueryWrapper<OutSideBpmBusinessParty>()
                .eq("business_party_mark", businessPartyMark));

        if (outSideBpmBusinessParty!=null) {
            List<OutSideBpmConditionsTemplate> outSideBpmConditionsTemplates = this.list(new QueryWrapper<OutSideBpmConditionsTemplate>()
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
     * query condition template list by business party mark and application id
     *
     * @param businessPartyMarkId
     * @param applicationId
     * @return
     */
    public List<OutSideBpmConditionsTemplateVo> selectListByPartMark(Long businessPartyMarkId, Integer applicationId) {

        List<OutSideBpmConditionsTemplate> outSideBpmConditionsTemplates = this.list(new QueryWrapper<OutSideBpmConditionsTemplate>()
                .eq("is_del", 0)
                .eq("business_party_id",businessPartyMarkId)
                .eq("application_id", applicationId));

        if (!CollectionUtils.isEmpty(outSideBpmConditionsTemplates)) {
            return outSideBpmConditionsTemplates
                    .stream()
                    .map(o -> OutSideBpmConditionsTemplateVo
                            .builder()
                            .id(o.getId())
                            .templateMark(o.getTemplateMark())
                            .templateName(o.getTemplateName())
                            .remark(o.getRemark())
                            .createTime(o.getCreateTime())
                            .build())
                    .collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }


    /**
     * query details by id
     *
     * @param id
     * @return
     */
    public OutSideBpmConditionsTemplateVo detail(Integer id) {

        OutSideBpmConditionsTemplate outSideBpmConditionsTemplate = this.getBaseMapper().selectById(id);

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
     * edit
     *
     * @param vo
     */
    public void edit(OutSideBpmConditionsTemplateVo vo) {

//        if (vo.getBusinessPartyId()==null) {
//            throw new JiMuBizException("业务方为空无法新建");
//        }

        if (StringUtil.isEmpty(vo.getApplicationFormCode())) {
            throw new JiMuBizException("关联应用未选择，编辑失败");
        }

        BpmProcessAppApplication application = Optional.ofNullable(bpmProcessAppApplicationService.getBaseMapper().selectOne(new QueryWrapper<BpmProcessAppApplication>()
                .eq("process_key", vo.getApplicationFormCode()))).orElse(new BpmProcessAppApplication());

        if (application.getId()==null) {
            throw new JiMuBizException("");
        }

        OutSideBpmBusinessParty outSideBpmBusinessModel = Optional.ofNullable(outSideBpmBusinessPartyService.getBaseMapper().selectOne(new QueryWrapper<OutSideBpmBusinessParty>()
                .eq("business_party_mark", application.getBusinessCode()))).orElse(new OutSideBpmBusinessParty());

        if (outSideBpmBusinessModel.getId()==null) {
            throw new JiMuBizException("业务方为空无法新建");
        }
        //check whether the template mark is repeated
        QueryWrapper<OutSideBpmConditionsTemplate> wrapperTemplateMark = new QueryWrapper<OutSideBpmConditionsTemplate>()
                .eq("is_del", 0)
                .eq("business_party_id", outSideBpmBusinessModel.getId())
                .eq("template_mark", vo.getTemplateMark())
                .eq("application_id", application.getId());
        if (vo.getId()!=null) {
            wrapperTemplateMark.ne("id", vo.getId());
        }
        long countTemplateMark = this.count(wrapperTemplateMark);
        if (countTemplateMark > 0) {
            throw new JiMuBizException("条件模板标识重复，编辑失败");
        }

        //check whether the template name is repeated,although the name can be repeated,but it may cause confusion,so make it not repeatable
        QueryWrapper<OutSideBpmConditionsTemplate> wrapperTemplateName = new QueryWrapper<OutSideBpmConditionsTemplate>()
                .eq("is_del", 0)
                .eq("business_party_id",  outSideBpmBusinessModel.getId())
                .eq("template_name", vo.getTemplateName())
                .eq("application_id", application.getId());
        if (vo.getId()!=null) {
            wrapperTemplateName.ne("id", vo.getId());
        }
        long countTemplateName = this.count(wrapperTemplateName);
        if (countTemplateName > 0) {
            throw new JiMuBizException("条件模板名称重复，编辑失败");
        }

        OutSideBpmConditionsTemplate outSideBpmConditionsTemplate = this.getById(vo.getId());

        if (outSideBpmConditionsTemplate!=null) {
            BeanUtils.copyProperties(vo, outSideBpmConditionsTemplate);
            outSideBpmConditionsTemplate.setApplicationId(application.getId());
            outSideBpmConditionsTemplate.setUpdateUser(SecurityUtils.getLogInEmpName());
            outSideBpmConditionsTemplate.setUpdateTime(new Date());
            this.updateById(outSideBpmConditionsTemplate);
        } else {


            outSideBpmConditionsTemplate = new OutSideBpmConditionsTemplate();
            BeanUtils.copyProperties(vo, outSideBpmConditionsTemplate);
            outSideBpmConditionsTemplate.setIsDel(0);
            outSideBpmConditionsTemplate.setBusinessPartyId(outSideBpmBusinessModel.getId());
            outSideBpmConditionsTemplate.setApplicationId(application.getId());
            outSideBpmConditionsTemplate.setCreateUserId(SecurityUtils.getLogInEmpIdSafe());
            outSideBpmConditionsTemplate.setCreateUser(SecurityUtils.getLogInEmpName());
            outSideBpmConditionsTemplate.setCreateTime(new Date());
            outSideBpmConditionsTemplate.setUpdateUser(SecurityUtils.getLogInEmpName());
            outSideBpmConditionsTemplate.setUpdateTime(new Date());
            this.save(outSideBpmConditionsTemplate);
        }
    }

    /**
     * remove
     *
     * @param id
     */
    public void delete(Integer id) {

        OutSideBpmConditionsTemplate outSideBpmConditionsTemplate = this.getById(id);

        OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getById(outSideBpmConditionsTemplate.getBusinessPartyId());

        if (templateIsUsed(id, outSideBpmBusinessParty)) {
            throw new JiMuBizException("审批流程中正使用此条件模板，无法删除");
        }

        this.updateById(OutSideBpmConditionsTemplate
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
