package org.openoa.engine.bpmnconf.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.SortTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.*;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAppApplicationMapper;
import org.openoa.engine.bpmnconf.service.biz.OutSideBpmBaseServiceImpl;

import org.openoa.engine.bpmnconf.service.interf.repository.ApplicationService;
import org.openoa.engine.utils.AFWrappers;
import org.openoa.engine.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_APPLICATION;


/**
 * process application service
 *
 * @author tylerZhou
 * @since 0.5
 */
@Service
public class ApplicationServiceImpl extends ServiceImpl<BpmProcessAppApplicationMapper, BpmProcessAppApplication> implements ApplicationService {

    @Autowired
    private BpmProcessAppApplicationMapper bpmProcessAppApplicationMapper;
    @Autowired
    private BpmProcessApplicationTypeServiceImpl bpmProcessApplicationTypeService;
    @Autowired
    private AfUserService employeeService;
    @Autowired
    private BpmProcessCategoryServiceImpl processCategoryService;
    @Autowired
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;
    @Autowired
    private OutSideBpmBaseServiceImpl outSideBpmBaseService;
    @Autowired
    @Lazy
    private BpmnConfServiceImpl bpmnConfService;

    /**
     * add /remove process application
     */
    @Override
    public void edit(BpmProcessAppApplicationVo vo) {
        BpmProcessAppApplication application = new BpmProcessAppApplication();
        BeanUtils.copyProperties(vo, application);
        if (!StringUtil.isEmpty(application.getRoute())) {
            application.setRoute(StringEscapeUtils.unescapeHtml4(application.getRoute()));
        }
        if (vo.getId()!=null) {
            application.setCreateUserId(SecurityUtils.getLogInEmpIdSafe());
        }

        List<BpmProcessAppApplication> list = this.list(
                AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                        .ne(application.getId()!=null, BpmProcessAppApplication::getId,application.getId())
                        .eq(BpmProcessAppApplication::getTitle,application.getTitle()));
        //check whether the name is repeated
        if (!CollectionUtils.isEmpty(list)) {
            throw new JiMuBizException("该选项名称已存在");
        }

        List<BpmProcessAppApplication> list1 = this.list(
                AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                        .ne(application.getId()!=null, BpmProcessAppApplication::getId,application.getId())
                        .eq(BpmProcessAppApplication::getProcessKey,application.getProcessKey()));

        //check whether the number is repeated
        if (!CollectionUtils.isEmpty(list1)) {
            throw new JiMuBizException("该选项名称已存在");
        }

        //add or update
        this.saveOrUpdate(application);

        //add application to category
        if (!CollectionUtils.isEmpty(vo.getProcessTypes())) {
            bpmProcessApplicationTypeService.editProcessApplicationType(
                    BpmProcessApplicationTypeVo
                            .builder()
                            .applicationId(application.getId().longValue())
                            .processTypes(vo.getProcessTypes())
                            .visbleState(!StringUtil.isEmpty(vo.getBusinessCode()) ? 0 : 1)
                            .build());


            //if current operation is edit and is parent application,then find out all child applications
            if (vo.getId()!=null && vo.getApplyType().equals(3)) {

                List<Integer> sonIds = this.list(
                        AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                                .eq(BpmProcessAppApplication::getIsSon,1)
                                .eq(BpmProcessAppApplication::getParentId,vo.getId()))
                        .stream()
                        .map(BpmProcessAppApplication::getId)
                        .collect(Collectors.toList());

                //edit child application's category
                for (Integer sonId : sonIds) {

                    //find out its old category
                    List<Long> types = bpmProcessApplicationTypeService.list(
                            AFWrappers.<BpmProcessApplicationType>lambdaTenantQuery()
                                    .eq(BpmProcessApplicationType::getApplicationId,sonId)
                            )
                            .stream()
                            .map(BpmProcessApplicationType::getCategoryId)
                            .collect(Collectors.toList());


                    //get parent application's category
                    List<Long> pTypes = vo.getProcessTypes();


                    List<Long> newTypes = types.stream().filter(pTypes::contains).collect(Collectors.toList());


                    //if new types are fewer then edit
                    if (newTypes.size() < types.size()) {
                        bpmProcessApplicationTypeService.editProcessApplicationType(
                                BpmProcessApplicationTypeVo
                                        .builder()
                                        .applicationId(sonId.longValue())
                                        .processTypes(newTypes)
                                        .visbleState(!StringUtil.isEmpty(vo.getBusinessCode()) ? 0 : 1)
                                        .build());
                    }
                }
            }

        }
    }

    /**
     * remove application
     */
    @Override
    public void del(Integer id) {
        BpmProcessAppApplication application = this.getById(id);

        List<BpmProcessAppApplication> list = this.list(
                AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                        .eq(BpmProcessAppApplication::getParentId,id));
        //如果父级应用且关联子级应用 则不可删除
        if (application.getApplyType().equals(3)
                && !CollectionUtils.isEmpty(list)) {
            throw new JiMuBizException("父级应用内联了子应用，请先删除子应用！");
        }
        application.setIsDel(1);
        bpmProcessAppApplicationMapper.updateById(application);
    }

    /**
     * page list
     */
    @Override
    public ResultAndPage<BpmProcessAppApplicationVo> pageList(PageDto pageDto, BpmProcessAppApplicationVo vo) {

        //query current login's visible business permission list (central system's permission list is empty,others return empty list directly)
        if (vo.getIsBusiness().equals(1)) {
            List<OutSideBpmBusinessPartyVo> list = outSideBpmBaseService.getEmplBusinessPartys(StringUtils.EMPTY, ADMIN_PERSONNEL_TYPE_APPLICATION.getPermCode());
            if (!CollectionUtils.isEmpty(list)) {
                vo.setBusinessCodeList(list
                        .stream()
                        .map(OutSideBpmBusinessPartyVo::getBusinessPartyMark)
                        .collect(Collectors.toList()));
            } else {
                return new ResultAndPage<BpmProcessAppApplicationVo>(new ArrayList<>(), new PageDto());
            }
        } else if (vo.getIsBusiness().equals(2)) {
            vo.setBusinessCodeList(Lists.newArrayList());
        } else {
            return new ResultAndPage<BpmProcessAppApplicationVo>(new ArrayList<>(), new PageDto());
        }

        LinkedHashMap<String, SortTypeEnum> orderFieldMap = new LinkedHashMap<>();
        orderFieldMap.put("id", SortTypeEnum.DESC);
        Page<BpmProcessAppApplicationVo> page = PageUtils.getPageByPageDto(pageDto, orderFieldMap);
        List<BpmProcessAppApplicationVo> list = bpmProcessAppApplicationMapper.newListPage(page, vo);

        if (CollectionUtils.isEmpty(list)) {
            return PageUtils.getResultAndPage(page);
        }

        Map<Long, String> categoryMap = processCategoryService.list(
                new QueryWrapper<>())
                .stream()
                .collect(Collectors.toMap(
                        BpmProcessCategory::getId,
                        v -> v.getProcessTypeName() + v.getEntrance(),
                        (v1, v2) -> v1));
        List<String> userIds = list.stream()
                .filter(o -> o.getCreateUserId()!=null)
                .map(BpmProcessAppApplicationVo::getCreateUserId)
                .collect(Collectors.toList());
        Map<String, BaseIdTranStruVo> employeeMap =!CollectionUtils.isEmpty(userIds)
                ? employeeService.queryUserByIds(userIds)
                .stream()
                .collect(Collectors.toMap(
                        BaseIdTranStruVo::getId,
                        v -> v,
                        (v1, v2) -> v1))
                : Maps.newHashMap();
        for (BpmProcessAppApplicationVo o : list) {


            //to check where can be deleted
            o.setIsCanDel(true);
            if (bpmnConfService.count(
                    AFWrappers.<BpmnConf>lambdaTenantQuery()
                            .eq(a->a.getFormCode(),o.getBusinessCode() + "_" + o.getProcessKey())) > 0) {
                o.setIsCanDel(false);
            }

            OutSideBpmBusinessParty party = Optional
                    .ofNullable(outSideBpmBusinessPartyService.getBaseMapper().selectOne(
                            AFWrappers.<OutSideBpmBusinessParty>lambdaTenantQuery()
                                    .eq(OutSideBpmBusinessParty::getBusinessPartyMark,o.getBusinessCode())))
                    .orElse(new OutSideBpmBusinessParty());


            //set business name by business party mark
            o.setBusinessName(party.getName());


            //set business party's access type
            o.setAccessType(party.getType());


            //set application type
            List<Long> processTypes = bpmProcessApplicationTypeService.list(
                    AFWrappers.<BpmProcessApplicationType>lambdaTenantQuery()
                            .eq(BpmProcessApplicationType::getApplicationId,o.getId()))
                    .stream()
                    .map(BpmProcessApplicationType::getCategoryId)
                    .filter(type -> !type.equals(1L) && !type.equals(2L))
                    .distinct()
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(processTypes)) {
                o.setProcessTypes(processTypes);
                o.setProcessTypeNames(processTypes
                        .stream()
                        .map(categoryMap::get)
                        .collect(Collectors.toList()));
            }

            //mapping application typename
            if (o.getApplyType()!=null) {
                if (o.getApplyType().equals(1)) {
                    o.setApplyTypeName("流程");
                    if (o.getIsSon().equals(1)) {
                        o.setApplyTypeName("子流程");
                    }
                } else if (o.getApplyType().equals(2)) {
                    o.setApplyTypeName("应用");
                    if (o.getIsSon().equals(1)) {
                        o.setApplyTypeName("子应用");
                    }
                } else {
                    o.setApplyTypeName("父级应用");
                }
            }

            //set create user
            BaseIdTranStruVo employee = Optional
                    .ofNullable(employeeMap.get(o.getCreateUserId()))
                    .orElse(new BaseIdTranStruVo());
            o.setCreateUserName(employee.getName());

            if (!StringUtils.isEmpty(o.getLookUrl())) {
                o.setLookUrl(StringEscapeUtils.unescapeHtml4(o.getLookUrl()));
            }
            if (!StringUtils.isEmpty(o.getSubmitUrl())) {
                o.setSubmitUrl(StringEscapeUtils.unescapeHtml4(o.getSubmitUrl()));
            }
            if (!StringUtils.isEmpty(vo.getConditionUrl())) {
                o.setConditionUrl(StringEscapeUtils.unescapeHtml4(o.getConditionUrl()));
            }
        }

        page.setRecords(list);
        return PageUtils.getResultAndPage(page);
    }

    /**
     * get application url
     *
     * @param businessCode business code
     * @param processKey   process key
     * @return 应用url
     */
    @Override
    public BpmProcessAppApplicationVo getApplicationUrl(String businessCode, String processKey) {
        if (!StringUtil.isEmpty(businessCode) &&!StringUtil.isEmpty(processKey)) {

            List<BpmProcessAppApplication> list = this.list(
                    AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                            .eq(BpmProcessAppApplication::getBusinessCode,businessCode)
                            .eq(BpmProcessAppApplication::getProcessKey,processKey));

            if (!CollectionUtils.isEmpty(list)) {
                BpmProcessAppApplication application = list.get(0);
                BpmProcessAppApplicationVo vo = new BpmProcessAppApplicationVo();
               BeanUtils.copyProperties(application, vo);
                if (!StringUtil.isEmpty(vo.getLookUrl())) {
                    vo.setLookUrl(StringEscapeUtils.unescapeHtml4(vo.getLookUrl()));
                }
                if (!StringUtil.isEmpty(vo.getSubmitUrl())) {
                    vo.setSubmitUrl(StringEscapeUtils.unescapeHtml4(vo.getSubmitUrl()));
                }
                if (!StringUtil.isEmpty(vo.getConditionUrl())) {
                    vo.setConditionUrl(StringEscapeUtils.unescapeHtml4(vo.getConditionUrl()));
                }
                return vo;
            }
        }
        return null;
    }

    /**
     * get parent application list
     */
    @Override
    public List<BpmProcessAppApplicationVo> getParentApplicationList(BpmProcessAppApplicationVo applicationVo) {
        List<BpmProcessAppApplication> list = applicationVo!=null && !StringUtil.isEmpty(applicationVo.getBusinessCode())
                ? this.list(
                        AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                                .eq(BpmProcessAppApplication::getBusinessCode,applicationVo.getBusinessCode())
                                .eq(BpmProcessAppApplication::getApplyType,3))
                : this.list(
                AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                        .isNull(BpmProcessAppApplication::getBusinessCode)
                        .eq(BpmProcessAppApplication::getApplyType,3));
        Map<Long, String> categoryMap = processCategoryService.list(
                new QueryWrapper<>())
                .stream()
                .collect(Collectors.toMap(
                        BpmProcessCategory::getId,
                        v -> v.getProcessTypeName() + v.getEntrance(),
                        (v1, v2) -> v1));
        return list.stream()
                .map(o -> {
                    BpmProcessAppApplicationVo vo = new BpmProcessAppApplicationVo();

                    List<Long> processTypes = bpmProcessApplicationTypeService.list(
                            AFWrappers.<BpmProcessApplicationType>lambdaTenantQuery()
                                    .eq(BpmProcessApplicationType::getApplicationId,o.getId()))
                            .stream()
                            .map(BpmProcessApplicationType::getCategoryId)
                            .filter(type -> !type.equals(1L) && !type.equals(2L))
                            .distinct()
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(processTypes)) {
                        vo.setProcessTypeList(processTypes
                                .stream()
                                .map(type -> BpmProcessCategoryVo
                                        .builder()
                                        .id(type)
                                        .name(categoryMap.get(type))
                                        .build())
                                .collect(Collectors.toList()));
                    }
                    vo.setId(o.getId());
                    vo.setTitle(o.getTitle());

                    return vo;
                }).collect(Collectors.toList());
    }

    /**
     * get category list
     */
    @Override
    public List<BpmProcessCategoryVo> getProcessTypeList(BpmProcessAppApplicationVo vo) {
        BpmProcessAppApplication application = this.getById(vo.getId());
        if (application!=null) {
            Map<Long, String> categoryMap = processCategoryService.list(
                    new QueryWrapper<>())
                    .stream()
                    .collect(Collectors.toMap(
                            BpmProcessCategory::getId,
                            v -> v.getProcessTypeName() + v.getEntrance(),
                            (v1, v2) -> v1));
            List<Long> processTypes = bpmProcessApplicationTypeService.list(
                    AFWrappers.<BpmProcessApplicationType>lambdaTenantQuery()
                            .eq(BpmProcessApplicationType::getApplicationId,application.getId()))
                    .stream()
                    .map(BpmProcessApplicationType::getCategoryId)
                    .filter(type -> !type.equals(1L) && !type.equals(2L))
                    .distinct()
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(processTypes)) {
                return processTypes
                        .stream()
                        .map(type -> BpmProcessCategoryVo
                                .builder()
                                .id(type)
                                .name(categoryMap.get(type))
                                .build())
                        .collect(Collectors.toList());
            }
        }
        return Lists.newArrayList();
    }

    /**
     * get application key list
     */
    @Override
    public List<BaseApplicationVo> getApplicationKeyList(BpmProcessAppApplicationVo applicationVo) {
        if (applicationVo==null || StringUtil.isEmpty(applicationVo.getBusinessCode())) {
            return Lists.newArrayList();
        }
        List<BpmProcessAppApplication> list = this.list(
                AFWrappers.<BpmProcessAppApplication>lambdaTenantQuery()
                        .eq(BpmProcessAppApplication::getBusinessCode,applicationVo.getBusinessCode()));
        return list.stream()
                .filter(o -> !StringUtil.isEmpty(o.getProcessKey()))
                .map(o -> BaseApplicationVo
                        .builder()
                        .id(o.getProcessKey())
                        .name(o.getTitle())
                        .lookUrl(o.getLookUrl())
                        .submitUrl(o.getSubmitUrl())
                        .conditionUrl(o.getConditionUrl())
                        .pkId(o.getId())
                        .build())
                .distinct()
                .collect(Collectors.toList());
    }

}
