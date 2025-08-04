package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.constant.enums.ApplyType;
import org.openoa.base.constant.enums.ProcessJurisdictionEnum;
import org.openoa.base.constant.enums.SortTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.*;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.common.ProcessBusinessContans;
import org.openoa.engine.bpmnconf.service.impl.*;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessDeptBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessPermissionsBizService;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmnConfBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.*;
import org.openoa.engine.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BpmProcessAppApplicationBizServiceImpl implements BpmProcessAppApplicationBizService {
    @Autowired
    @Lazy
    private BpmProcessAppDataService processAppDataService;
    @Autowired
    private BpmProcessPermissionsBizService processPermissionsBizService;
    @Autowired
    @Lazy
    private SysVersionService sysVersionService;
    @Autowired
    private BpmProcessApplicationTypeService bpmProcessApplicationTypeService;
    @Autowired
    private BpmProcessCategoryService bpmProcessCategoryService;
    @Autowired
    @Lazy
    private QuickEntryService quickEntryService;
    @Autowired
    private BpmProcessDeptBizService processDeptBizService;
    @Autowired
    private BpmnConfBizService bpmnConfBizService;
    @Autowired
    private ProcessBusinessContans processBusinessContans;

    // app's frequently used function's id
    public final static Integer appCommonId = 2;
    // pc's frequently used function's id
    public final static Integer pcCommonId = 1;



    /**
     * get app entrance by version
     */
    @Override
    public List<String> getAppEntrance(String version) {
        SysVersion sysVersion = sysVersionService.getInfoByVersion(version);
        if (sysVersion!=null) {
            //根据版本id获取本次版本上线的应用数据
            List<BpmProcessAppData> appDataList = processAppDataService.getProcessAppData(sysVersion.getId(), 0, AppApplicationType.TWO_TYPE.getCode());
            return appDataList.stream().map(o -> o.getProcessKey()).collect(Collectors.toList());
        }
        return Arrays.asList();
    }

    /**
     * pc process data mapping
     *
     * @return
     */
    @Override
    public Page<BpmProcessAppApplicationVo> getPcProcessData(Page<BpmProcessAppApplicationVo> page) {
        return page.setRecords(page.getRecords().stream()
                .map(o -> {
                    if (!StringUtil.isEmpty(o.getEntrance())) {
                        o.setName(o.getTitle() + "," + o.getEntrance());
                    } else {
                        o.setName(o.getTitle());
                    }
                    if (!StringUtil.isEmpty(o.getTypeIds())) {
                        String[] split = o.getTypeIds().split(",");
                        List<Long> list = new ArrayList<>();
                        if (split.length > 1) {
                            for (String typeId : split) {
                                List<String> stringList = Arrays.asList(appCommonId.toString(), pcCommonId.toString());
                                if (!stringList.contains(typeId)) {
                                    list.add(Long.parseLong(typeId));
                                }
                            }
                            o.setProcessTypes(list);
                        } else {
                            list.add(Long.parseLong(o.getTypeIds()));

                        }
                        o.setProcessTypes(list);
                    }
                    o.setApplyTypeName(ApplyType.getDescByCode(o.getApplyType()));
                    return o;
                }).collect(Collectors.toList()));
    }

    /**
     * process application list
     */
    @Override
    public List<ProcessTypeInforVo> processApplicationList() {
        List<BpmProcessCategory> bpmProcessCategories = bpmProcessCategoryService.getBaseMapper().selectList(new QueryWrapper<BpmProcessCategory>().eq("is_app", 1).eq("is_del", 0).ne("id", appCommonId));
        bpmProcessCategories.sort((BpmProcessCategory bpmProcessAppApplication, BpmProcessCategory appApplication) -> bpmProcessAppApplication.getSort().compareTo(appApplication.getSort()));
        List<ProcessTypeInforVo> list = new ArrayList<>();
        for (BpmProcessCategory processCategory : bpmProcessCategories) {
            ProcessTypeInforVo processTypeInforVo = new ProcessTypeInforVo();
            processTypeInforVo.setProcessTypeName(processCategory.getProcessTypeName());
            processTypeInforVo.setApplicationList(getMapper().listProcessIcon(BpmProcessAppApplicationVo.builder().id(processCategory.getId().intValue()).build()));
            list.add(processTypeInforVo);
        }
        return list;
    }
    /**
     * app/pc config
     */
    @Override
    public IconInforVo iconConfig(Integer isApp, Integer parentId, Integer processCategoryId) {

        //sub application list
        if (parentId!=null && processCategoryId!=null) {
            //查询类别
            BpmProcessCategory processCategory = bpmProcessCategoryService.getBaseMapper().selectById(processCategoryId);
            IconInforVo iconInforVo = new IconInforVo();
            ProcessTypeInforVo typeInforVo = new ProcessTypeInforVo();
            List<BpmProcessAppApplicationVo> vos = getMapper().listIcon(BpmProcessAppApplicationVo.builder()
                    .id(processCategoryId)
                    .isSon(1)
                    .parentId(parentId)
                    .build());
            typeInforVo.setIconList(this.bpmProcessAppApplicationVoList(vos));
            typeInforVo.setTypeId(processCategory.getId().intValue());
            typeInforVo.setProcessTypeName(processCategory.getProcessTypeName());
            iconInforVo.setSonApplicationList(typeInforVo);
            return iconInforVo;
        }

//        //1:querying all app
//        List<BpmProcessAppApplication> appApplications = this.applicationsList();
//        List<String> stringStream = appApplications.stream().map(BpmProcessAppApplication::getProcessKey).collect(Collectors.toList());
        Integer isPc = 1;
        Integer id = appCommonId;
        if (isApp.equals(0)) {
            isPc = 0;
            id = pcCommonId;
        }
        IconInforVo iconInforVo = new IconInforVo();
        //2: querying all category list
        List<BpmProcessCategory> bpmProcessCategoryList = bpmProcessCategoryService.processCategoryList(BpmProcessCategoryVo.builder().isApp(isPc).build());
        //常用功能
        ProcessTypeInforVo typeInforVo = new ProcessTypeInforVo();
        BpmProcessCategory processCategory = bpmProcessCategoryService.getProcessCategory(id.longValue());
        if (processCategory!=null) {
            List<BpmProcessAppApplicationVo> vos = getMapper().listIcon(BpmProcessAppApplicationVo.builder()
                    .id(processCategory.getId().intValue())
                    .build());
            typeInforVo.setIconList(this.bpmProcessAppApplicationVoList(vos));
            typeInforVo.setTypeId(processCategory.getId().intValue());
            //typeInforVo.set
            typeInforVo.setProcessTypeName(processCategory.getProcessTypeName());
        }
        iconInforVo.setCommonFunction(typeInforVo);
        //process type info
        List<ProcessTypeInforVo> typeInforVoList = new ArrayList<>();
        for (BpmProcessCategory bpmProcessCategory : bpmProcessCategoryList) {
            ProcessTypeInforVo processTypeInforVo = new ProcessTypeInforVo();
            if (bpmProcessCategory.getId().equals(id.longValue())) {
                continue;
            }
            List<BpmProcessAppApplicationVo> vos = getMapper().listIcon(BpmProcessAppApplicationVo.builder()
//                    .processKeyList(stringStream)
                    .id(bpmProcessCategory.getId().intValue())
                    .isSon(0)
                    .build());
            processTypeInforVo.setIconList(this.bpmProcessAppApplicationVoList(vos));
            processTypeInforVo.setTypeId(bpmProcessCategory.getId().intValue());
            processTypeInforVo.setProcessTypeName(bpmProcessCategory.getProcessTypeName());
            typeInforVoList.add(processTypeInforVo);
        }
        iconInforVo.setApplicationList(typeInforVoList);
        return iconInforVo;
    }

    /**
     * home page process info on the pc
     */
    @Override
    public List<ProcessTypeInforVo> homePageIcon(BpmProcessAppApplicationVo vo) {

        List<ProcessTypeInforVo> typeInforVoList = new ArrayList<>();

        List<String> collect = processDeptBizService.findProcessKey();

        collect.addAll(this.permissionsProcessKeys());

        // if it is a sub process
        if (vo!=null &&vo.getParentId()!=null) {
            if (vo.getProcessCategoryId()!=null) {
                //get sub process
                BpmProcessAppApplication application = this.getMapper().selectById(vo.getParentId());
                ProcessTypeInforVo processTypeInforVo = new ProcessTypeInforVo();
                List<BpmProcessAppApplicationVo> vos;

                // querying the sub application under the category
                vos = getMapper().listIcon(BpmProcessAppApplicationVo.builder()
                        .processKeyList(collect)
                        .visbleState(1)
                        .isSon(1)
                        .parentId(vo.getParentId())
                        .id(vo.getProcessCategoryId())
                        .build());
                processTypeInforVo.setIconList(this.bpmProcessAppApplicationVoList(vos));
                processTypeInforVo.setTypeId(application.getId());
                processTypeInforVo.setProcessTypeName(application.getTitle());
                typeInforVoList.add(processTypeInforVo);
                return typeInforVoList;
            } else {

                //get sub process from frequently used application
                BpmProcessAppApplication application =this.getMapper().selectById(vo.getParentId());
                ProcessTypeInforVo processTypeInforVo = new ProcessTypeInforVo();
                List<BpmProcessAppApplicationVo> vos;

                //querying the sub application under the parent application
                vos = getMapper().listIcon(BpmProcessAppApplicationVo.builder()
                        .processKeyList(collect)
                        .visbleState(1)
                        .isSon(1)
                        .parentId(vo.getParentId())
                        .build());

                //deduplication
                Map<Integer, BpmProcessAppApplicationVo> map = vos.stream()
                        .collect(Collectors.toMap(
                                BpmProcessAppApplicationVo::getApplicationId,
                                v -> v,
                                (v1, v2) -> v1));
                vos = new ArrayList<>(map.values());

                processTypeInforVo.setIconList(this.bpmProcessAppApplicationVoList(vos));
                processTypeInforVo.setTypeId(application.getId());
                processTypeInforVo.setProcessTypeName(application.getTitle());
                typeInforVoList.add(processTypeInforVo);
                return typeInforVoList;
            }
        }


        //query first level application
        //query all category
        List<BpmProcessCategory> bpmProcessCategoryList = bpmProcessCategoryService.processCategoryList(BpmProcessCategoryVo.builder().isApp(0).build());
        for (BpmProcessCategory bpmProcessCategory : bpmProcessCategoryList) {
            ProcessTypeInforVo processTypeInforVo = new ProcessTypeInforVo();
            List<BpmProcessAppApplicationVo> vos;
            if (bpmProcessCategory.getId() == 1) {

                //if it is common function,then do not filter its sub process
                vos = getMapper().listIcon(BpmProcessAppApplicationVo.builder()
                        .processKeyList(collect)
                        .visbleState(1)
                        .id(bpmProcessCategory.getId().intValue())
                        .build());

                //if one has son application' permission to view, then the parent application has permission to view
                List<Integer> list = vos
                        .stream()
                        //get all sub application
                        .filter(o -> o.getIsSon().equals(1))

                        .map(BpmProcessAppApplicationVo::getParentId)
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(list)) {
                    vos.addAll(getMapper().listIcon(BpmProcessAppApplicationVo.builder()
                            .visbleState(1)
                            .id(bpmProcessCategory.getId().intValue())
                            .ids(list)
                            .build()));
                }
            } else {

                //if it is common category,then filter its sub process
                vos = getMapper().listIcon(BpmProcessAppApplicationVo.builder()
                        .processKeyList(collect)
                        .visbleState(1)
                        .id(bpmProcessCategory.getId().intValue())
                        .build());

                List<Integer> list = vos
                        .stream()
                        .filter(o -> o.getIsSon().equals(1))
                        .map(BpmProcessAppApplicationVo::getParentId)
                        .collect(Collectors.toList());
                vos = vos
                        .stream()
                        .filter(o -> o.getIsSon().equals(0))
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(list)) {
                    vos.addAll(getMapper().listIcon(BpmProcessAppApplicationVo.builder()
                            .visbleState(1)
                            .id(bpmProcessCategory.getId().intValue())
                            .ids(list)
                            .build()));
                }
            }
            processTypeInforVo.setIconList(this.bpmProcessAppApplicationVoList(vos));
            processTypeInforVo.setTypeId(bpmProcessCategory.getId().intValue());
            processTypeInforVo.setProcessTypeName(bpmProcessCategory.getProcessTypeName());
            typeInforVoList.add(processTypeInforVo);
        }
        return typeInforVoList;
    }

    /**
     * mapping
     */
    @Override
    public List<BpmProcessAppApplicationVo> bpmProcessAppApplicationVoList(List<BpmProcessAppApplicationVo> list) {
        List<String> keys = list.stream()
                .map(BpmProcessAppApplicationVo::getProcessKey)
                .collect(Collectors.toList());
        Map<String, BpmnConf> map = bpmnConfBizService.getMapper().selectList(new QueryWrapper<BpmnConf>()
                        .in("form_code", keys)
                        .eq("effective_status", 1))
                .stream()
                .collect(Collectors.toMap(
                        BpmnConf::getFormCode,
                        o -> o,
                        (a, b) -> a));
        return list.stream().peek(o -> {
            if (!StringUtil.isEmpty(o.getProcessKey())) {
                o.setSource(o.getEffectiveSource());

                //如果该应用是外部应用 则code为业务标识前缀+code
                if (!StringUtil.isEmpty(o.getBusinessCode())) {
                    o.setProcessCode(o.getBusinessCode() + "_" + o.getProcessKey());
                    o.setProcessKey(o.getProcessCode());
                } else {
                    //如果不是外部应用按照原有逻辑
                    BpmnConf bpmnConfByCode = map.get(o.getProcessKey());
                    if (bpmnConfByCode!=null && bpmnConfByCode.getId()!=null) {
                        o.setProcessCode(bpmnConfByCode.getFormCode());
                    } else {
                        o.setProcessCode(o.getProcessKey().split("\\_")[0].toString());
                    }
                    o.setAppVersion(BpmBusinessProcess.VERSION_DEFAULT_0);

                }
            }
        }).collect(Collectors.toList());
    }

    /**
     * app's home page frequently used app
     */
    @Override
    public ProcessTypeInforVo iconCommon() {

        BpmProcessCategory processCategory = bpmProcessCategoryService.getBaseMapper().selectById(appCommonId);
        if (processCategory!=null) {
            List<BpmProcessAppApplicationVo> vos = getMapper().listIcon(
                    BpmProcessAppApplicationVo
                            .builder()
                            .id(appCommonId)
                            .visbleState(1)
                            .build());

            return ProcessTypeInforVo
                    .builder()
                    .applicationList(vos)
                    .processTypeName(processCategory.getProcessTypeName())
                    .build();
        }
        return null;
    }
    /**
     *  all list on the pc
     */
    @Override
    public ResultAndPage<BpmProcessAppApplicationVo> applicationsNewList(PageDto pageDto, BpmProcessAppApplicationVo vo) {
        //排序字段链表
        LinkedHashMap<String, SortTypeEnum> orderFieldMap = new LinkedHashMap<>();
        orderFieldMap.put("id", SortTypeEnum.DESC);
        Page<BpmProcessAppApplicationVo> page = PageUtils.getPageByPageDto(pageDto, orderFieldMap);
        page.setRecords(getMapper().newListPage(page, vo));
        this.getPcProcessData(page);
        return PageUtils.getResultAndPage(page);
    }

    /**
     *  all list on the pc
     */
    @Override
    public ResultAndPage<BpmProcessAppApplicationVo> applicationsList(PageDto pageDto, BpmProcessAppApplicationVo vo) {
        //排序字段链表
        LinkedHashMap<String, SortTypeEnum> orderFieldMap = new LinkedHashMap<>();
        orderFieldMap.put("id", SortTypeEnum.DESC);
        Page<BpmProcessAppApplicationVo> page = PageUtils.getPageByPageDto(pageDto, orderFieldMap);
        page.setRecords(getMapper().listPage(page, vo));
        this.getPcProcessData(page);
        return PageUtils.getResultAndPage(page);
    }
    /**
     * get permissions
     *
     * @return
     */
    private List<String> permissionsProcessKeys() {
        List<String> processKey = new ArrayList<>();
        List<BpmProcessAppApplication> appApplications = this.getMapper().selectList(new QueryWrapper<BpmProcessAppApplication>().eq("is_del", 0).eq("apply_type", 2));
        if (!CollectionUtils.isEmpty(appApplications)) {
            for (BpmProcessAppApplication appApplication : appApplications) {
                if (!StringUtil.isEmpty(appApplication.getPermissionsCode())) {

                } else {
                    processKey.add(appApplication.getProcessKey());
                }

            }
        }
        return processKey;
    }
    /**
     * query applications data by version
     */
    private List<String> findBpmProcessAppApplication(String version) {
        GenericEmployee genericEmployee = new GenericEmployee();
        genericEmployee.setUserId(SecurityUtils.getLogInEmpIdSafe());
        genericEmployee.setUsername(SecurityUtils.getLogInEmpNameSafe());
        SysVersion sysVersion = sysVersionService.getInfoByVersion(version);
        if (sysVersion==null) {
            return Arrays.asList();
        }

        //get all process
        List<String> allProcess = processDeptBizService.getService().getAllProcess();

        // process's detailed conf
        List<BpmnConf> allConfList = Optional.ofNullable(bpmnConfBizService.getIsAllConfs()).orElse(Arrays.asList());
        List<String> collect = allConfList.stream().map(BpmnConf::getFormCode).collect(Collectors.toList());
        allProcess.addAll(collect);
        // allProcess.addAll(ProcessTypeQuery.getprocessKeyList(VPN_TYPE.getCode()));

        //get all process that a specified user has permission to create
        List<String> processKeyList = processPermissionsBizService.getProcessKey(genericEmployee.getUserId(), ProcessJurisdictionEnum.CREATE_TYPE.getCode());
        if (!CollectionUtils.isEmpty(processKeyList)) {
            allProcess.addAll(processKeyList);
        }
        //筛选当前版本应用数据
        //filtering current version's process
        List<String> allAppData = processAppDataService.getBpmProcessAppDataVo(sysVersion.getId(), allProcess);
        allAppData.addAll(getMapper().findProcessAppApplication().stream().map(BpmProcessAppApplication::getProcessKey).collect(Collectors.toList()));

        allAppData.addAll(this.permissionsProcessKeys());
        return allAppData;
    }
    /**
     * mapping
     */
    private List<BpmProcessAppApplicationVo> processAppDate(List<BpmProcessAppApplicationVo> list) {
        return list.stream().map(o -> {
            o.setSource(o.getEffectiveSource());
            BpmnConf bpmnConfByCode = bpmnConfBizService.getBpmnConfByFormCode(o.getProcessKey());
            if (bpmnConfByCode!=null && bpmnConfByCode.getId()!=null) {

                boolean isOutSide = false;

                if (bpmnConfByCode.getIsOutSideProcess()!=null && bpmnConfByCode.getIsOutSideProcess() == 1) {
                    isOutSide = true;
                }

                o.setRoute(processBusinessContans.applyRoute(bpmnConfByCode.getBpmnCode(), bpmnConfByCode.getFormCode(), isOutSide));
            } else {
                if (o.getRoute()!=null) {
                    String route = StringEscapeUtils.unescapeJson(o.getRoute());
                    try {
                        o.setRoute(URLEncoder.encode(route, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            return o;
        }).collect(Collectors.toList());
    }

}
