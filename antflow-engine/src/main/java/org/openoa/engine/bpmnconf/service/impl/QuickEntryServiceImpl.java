package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.SortTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.entity.QuickEntry;
import org.openoa.engine.bpmnconf.mapper.QuickEntryMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.QuickEntryService;
import org.openoa.engine.vo.QuickEntryVo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;



/**
 * quick entry curd service
 */
@Slf4j
@Service
public class QuickEntryServiceImpl extends ServiceImpl<QuickEntryMapper, QuickEntry> implements QuickEntryService {

    @Override
    public boolean deleteQuickEntry(Long id) {
        QuickEntry quickEntry = this.getById(id);
        quickEntry.setIsDel(1);
        this.updateById(quickEntry);
        return true;
    }

    /**
     * find a list of quick entry
     *
     * @param pageDto
     * @param vo
     * @return
     */
    @Override
    public ResultAndPage<QuickEntryVo> findProcessList(PageDto pageDto, QuickEntryVo vo)  {
        //排序字段链表
        LinkedHashMap<String, SortTypeEnum> orderFieldMap = new LinkedHashMap<>();
        orderFieldMap.put("sort", SortTypeEnum.ASC);
        orderFieldMap.put("create_time", SortTypeEnum.DESC);
        Page<QuickEntryVo> page = PageUtils.getPageByPageDto(pageDto, orderFieldMap);
        page.setRecords(getBaseMapper().listQuickEntry(page, vo));
        this.getPcProcessData(page);
        return PageUtils.getResultAndPage(page);
    }

    /**
     * pc process data mapping
     *
     * @param page
     * @return
     */
    @Override
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




    @Override
    public List<QuickEntryVo> searchQuickEntry(String search, Integer limitSize) {
        if (limitSize==null) {
            limitSize = 10;
        }
        if (!StringUtil.isEmpty(search)) {
            return getBaseMapper().searchQuickEntry(search, limitSize);
        }
        return Collections.EMPTY_LIST;

    }

    public Object getQuickVarUrl(Long id, String requestIp) {
        QuickEntry quickEntry = getBaseMapper().selectById(id);
        if (QuickEntry.VARIABLE_URL_FLAG_FALSE.equals(quickEntry.getVariableUrlFlag())) {
            return quickEntry.getRoute();
        }

        return quickEntry.getRoute();
    }
}
