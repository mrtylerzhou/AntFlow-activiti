package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.constant.enums.SortTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.confentity.BpmProcessAppData;
import org.openoa.engine.bpmnconf.confentity.QuickEntry;
import org.openoa.engine.bpmnconf.confentity.QuickEntryType;
import org.openoa.engine.bpmnconf.confentity.SysVersion;
import org.openoa.engine.bpmnconf.mapper.QuickEntryMapper;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.ProcessTypeInforVo;
import org.openoa.engine.vo.QuickEntryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;



/**
 * quick entry curd service
 */
@Slf4j
@Service
public class QuickEntryServiceImpl extends ServiceImpl<QuickEntryMapper, QuickEntry> {

    private static final String RETURN_SUCCESS = "000000";
    @Autowired
    private QuickEntryMapper quickEntryMapper;

    @Autowired
    private QuickEntryTypeServiceImpl quickEntryTypeService;

    @Autowired
    private BpmProcessAppDataServiceImpl bpmProcessAppDataService;

    @Autowired
    private SysVersionServiceImpl sysVersionService;




    /**
     * add quick entry
     */
    public boolean editQuickEntry(QuickEntryVo vo) {
        QuickEntry quickEntry = new QuickEntry();
        BeanUtils.copyProperties(vo, quickEntry, "serialVersionUID");
        String route = StringEscapeUtils.unescapeHtml3(vo.getRoute());
        if (vo.getId()!=null) {
            quickEntry.setRoute(route);
            this.updateById(quickEntry);
        } else {
            quickEntry.setRoute(route);
            quickEntry.setCreateTime(new Date());
            this.getBaseMapper().insert(quickEntry);
        }

        Serializable id = Optional.ofNullable(Optional.ofNullable(quickEntry).orElseGet(QuickEntry::new).getId()).orElse((int) 0L);
        vo.setId((Integer) id);
        quickEntryTypeService.addQuickEntryType(vo);
        return true;
    }


    public boolean deleteQuickEntry(Long id) {
        QuickEntry quickEntry = this.getById(id);
        quickEntry.setIsDel(1);
        this.updateById(quickEntry);
        return true;
    }


    public ProcessTypeInforVo allQuickEntry(String version) {
        List<BpmProcessAppApplicationVo> appApplicationList = new ArrayList<>();
        SysVersion infoByVersion = sysVersionService.getInfoByVersion(version);
        if (infoByVersion==null) {
            return null;
        }
        List<BpmProcessAppData> processAppData = bpmProcessAppDataService.getProcessAppData(infoByVersion.getId(), 0, AppApplicationType.THREE_TYPE.getCode());
        if (processAppData==null) {
            return null;
        }

        List<Long> collect = processAppData.stream().map(BpmProcessAppData::getApplicationId).collect(Collectors.toList());
        List<QuickEntry> quickEntries = this.listQuickEntry(collect);
        if (!CollectionUtils.isEmpty(quickEntries)) {
            appApplicationList.addAll(
                    quickEntries.stream().map(o -> {
                        return BpmProcessAppApplicationVo.builder()
                                .title(o.getTitle())
                                .route(o.getRoute())
                                .source(o.getEffectiveSource())
                                .sort(o.getSort())
                                .build();
                    }).collect(Collectors.toList()));
            return ProcessTypeInforVo.builder()
                    .applicationList(appApplicationList)
                    .processTypeName("快捷入口")
                    .build();
        }
        return null;
    }

    /**
     * find a list of quick entry
     *
     * @param pageDto
     * @param vo
     * @return
     */
    public ResultAndPage<QuickEntryVo> findProcessList(PageDto pageDto, QuickEntryVo vo)  {
        //排序字段链表
        LinkedHashMap<String, SortTypeEnum> orderFieldMap = new LinkedHashMap<>();
        orderFieldMap.put("sort", SortTypeEnum.ASC);
        orderFieldMap.put("create_time", SortTypeEnum.DESC);
        Page<QuickEntryVo> page = PageUtils.getPageByPageDto(pageDto, orderFieldMap);
        page.setRecords(quickEntryMapper.listQuickEntry(page, vo));
        this.getPcProcessData(page);
        return PageUtils.getResultAndPage(page);
    }

    /**
     * pc process data mapping
     *
     * @param page
     * @return
     */
    public Page<QuickEntryVo> getPcProcessData(Page<QuickEntryVo> page) {
        return page.setRecords(page.getRecords().stream()
                .map(o -> {
                    if (!StringUtil.isEmpty(o.getTypeIds())) {
                        List<Integer> list = new ArrayList<>();
                        String[] split = o.getTypeIds().split("\\,");
                        if (split.length > 0) {
                            for (String typeId : split) {
                                list.add(Integer.parseInt(typeId));
                            }
                        }
                        o.setTypes(list);
                    }
                    if (o.getIsDel().equals(0)) {
                        o.setStateName("启用");
                    } else {
                        o.setStateName("禁用");
                    }
                    return o;
                }).collect(Collectors.toList()));
    }


    public List<QuickEntry> listQuickEntry(Boolean isApp) {
        List<QuickEntryType> quickEntryTypes = quickEntryTypeService.quickEntryTypeList(isApp);
        List<Long> collect = quickEntryTypes.stream().map(QuickEntryType::getQuickEntryId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(collect)) {
            QueryWrapper<QuickEntry> wrapper = new QueryWrapper<QuickEntry>();
            wrapper.eq("is_del", 0);
            wrapper.in("id", collect);
            wrapper.orderByAsc("sort");
            wrapper.orderByAsc("create_time");
            return quickEntryMapper.selectList(wrapper);
        } else {
            return Arrays.asList();
        }
    }


    public List<QuickEntry> listQuickEntry(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            return quickEntryMapper.selectList(new QueryWrapper<QuickEntry>().eq("is_del", 0).eq("status",0).in("id", ids));
        } else {
            return Arrays.asList();
        }
    }


    public List<QuickEntryVo> searchQuickEntry(String search, Integer limitSize) {
        if (limitSize==null) {
            limitSize = 10;
        }
        if (!StringUtil.isEmpty(search)) {
            return quickEntryMapper.searchQuickEntry(search, limitSize);
        }
        return Collections.EMPTY_LIST;

    }

    public Object getQuickVarUrl(Long id, String requestIp) {
        QuickEntry quickEntry = quickEntryMapper.selectById(id);
        if (QuickEntry.VARIABLE_URL_FLAG_FALSE.equals(quickEntry.getVariableUrlFlag())) {
            return quickEntry.getRoute();
        }

        return quickEntry.getRoute();
    }
}
