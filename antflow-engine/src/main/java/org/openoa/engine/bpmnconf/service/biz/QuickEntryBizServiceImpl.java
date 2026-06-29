package org.openoa.engine.bpmnconf.service.biz;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openoa.base.constant.enums.AppApplicationType;
import org.openoa.base.entity.BpmProcessAppData;
import org.openoa.base.entity.QuickEntry;
import org.openoa.base.entity.SysVersion;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessAppDataService;
import org.openoa.engine.bpmnconf.service.interf.repository.QuickEntryBizService;
import org.openoa.engine.bpmnconf.service.interf.repository.SysVersionService;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.ProcessTypeInforVo;
import org.openoa.engine.vo.QuickEntryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuickEntryBizServiceImpl implements QuickEntryBizService {

    @Autowired
    private BpmProcessAppDataService bpmProcessAppDataService;

    @Autowired
    private SysVersionService sysVersionService;

    /**
     * add quick entry
     */
    @Override
    public boolean editQuickEntry(QuickEntryVo vo) {
        QuickEntry quickEntry = new QuickEntry();
        BeanUtils.copyProperties(vo, quickEntry, "serialVersionUID");
        String route = StringEscapeUtils.unescapeHtml3(vo.getRoute());
        quickEntry.setRoute(route);
        if (!CollectionUtils.isEmpty(vo.getTypes())) {
            List<Map<String, Object>> typeList = new ArrayList<>();
            for (Integer type : vo.getTypes()) {
                Map<String, Object> typeMap = new LinkedHashMap<>();
                typeMap.put("type", type);
                typeMap.put("typeName", type == 1 ? "PC" : "APP");
                typeList.add(typeMap);
            }
            quickEntry.setTypeConfigJson(JSON.toJSONString(typeList));
        }
        if (vo.getId()!=null) {
            this.getService().updateById(quickEntry);
        } else {
            quickEntry.setCreateTime(new Date());
            this.getMapper().insert(quickEntry);
        }
        return true;
    }

    @Override
    public ProcessTypeInforVo allQuickEntry(String version) {
        List<BpmProcessAppApplicationVo> appApplicationList = new ArrayList<>();
        SysVersion infoByVersion = sysVersionService.getInfoByVersion(version);
        if (infoByVersion==null) {
            return null;
        }
        List<BpmProcessAppData> processAppData = bpmProcessAppDataService.getProcessAppData(infoByVersion.getId(), 0, AppApplicationType.APP_QUICK_ENTRY.getCode());
        if (processAppData==null) {
            return null;
        }

        List<Long> collect = processAppData.stream().map(a->Long.valueOf(a.getApplicationId())).collect(Collectors.toList());
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
        int targetType = isApp ? 2 : 1;
        QueryWrapper<QuickEntry> wrapper = new QueryWrapper<QuickEntry>();
        wrapper.eq("is_del", 0);
        wrapper.isNotNull("type_config_json");
        wrapper.orderByAsc("sort");
        wrapper.orderByAsc("create_time");
        List<QuickEntry> allEntries = getMapper().selectList(wrapper);
        return allEntries.stream()
                .filter(e -> {
                    if (e.getTypeConfigJson() == null) return false;
                    List<Map<String, Object>> typeList = JSON.parseObject(e.getTypeConfigJson(),
                            new com.alibaba.fastjson2.TypeReference<List<Map<String, Object>>>() {});
                    if (CollectionUtils.isEmpty(typeList)) return false;
                    for (Map<String, Object> typeMap : typeList) {
                        Object typeVal = typeMap.get("type");
                        int type = typeVal instanceof Number ? ((Number) typeVal).intValue() : Integer.parseInt(typeVal.toString());
                        if (type == targetType) return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
