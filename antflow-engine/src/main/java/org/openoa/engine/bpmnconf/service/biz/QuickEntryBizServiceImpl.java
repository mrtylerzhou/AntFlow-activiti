package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.entity.BpmProcessAppData;
import org.openoa.base.entity.QuickEntry;
import org.openoa.base.entity.QuickEntryType;
import org.openoa.base.entity.SysVersion;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessAppDataServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.QuickEntryTypeServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.SysVersionServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.QuickEntryBizService;
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

@Service
public class QuickEntryBizServiceImpl implements QuickEntryBizService {

    @Autowired
    private QuickEntryTypeServiceImpl quickEntryTypeService;

    @Autowired
    private BpmProcessAppDataServiceImpl bpmProcessAppDataService;

    @Autowired
    private SysVersionServiceImpl sysVersionService;

    /**
     * add quick entry
     */
    @Override
    public boolean editQuickEntry(QuickEntryVo vo) {
        QuickEntry quickEntry = new QuickEntry();
        BeanUtils.copyProperties(vo, quickEntry, "serialVersionUID");
        String route = StringEscapeUtils.unescapeHtml3(vo.getRoute());
        if (vo.getId()!=null) {
            quickEntry.setRoute(route);
            this.getService().updateById(quickEntry);
        } else {
            quickEntry.setRoute(route);
            quickEntry.setCreateTime(new Date());
            this.getMapper().insert(quickEntry);
        }

        Serializable id = Optional.ofNullable(Optional.ofNullable(quickEntry).orElseGet(QuickEntry::new).getId()).orElse((int) 0L);
        vo.setId((Integer) id);
        quickEntryTypeService.addQuickEntryType(vo);
        return true;
    }

    @Override
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

    @Override
    public List<QuickEntry> listQuickEntry(List<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            return getMapper().selectList(new QueryWrapper<QuickEntry>().eq("is_del", 0).eq("status",0).in("id", ids));
        } else {
            return Arrays.asList();
        }
    }

    @Override
    public List<QuickEntry> listQuickEntry(Boolean isApp) {
        List<QuickEntryType> quickEntryTypes = quickEntryTypeService.quickEntryTypeList(isApp);
        List<Long> collect = quickEntryTypes.stream().map(QuickEntryType::getQuickEntryId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(collect)) {
            QueryWrapper<QuickEntry> wrapper = new QueryWrapper<QuickEntry>();
            wrapper.eq("is_del", 0);
            wrapper.in("id", collect);
            wrapper.orderByAsc("sort");
            wrapper.orderByAsc("create_time");
            return getMapper().selectList(wrapper);
        } else {
            return Collections.emptyList();
        }
    }
}
