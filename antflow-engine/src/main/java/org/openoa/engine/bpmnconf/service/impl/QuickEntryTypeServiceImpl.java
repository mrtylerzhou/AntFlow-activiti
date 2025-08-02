package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.QuickEntryType;
import org.openoa.engine.bpmnconf.mapper.QuickEntryTypeMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.QuickEntryTypeService;
import org.openoa.engine.vo.QuickEntryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;


/**
 * query entry curd service
 */
@Service
public class QuickEntryTypeServiceImpl extends ServiceImpl<QuickEntryTypeMapper, QuickEntryType> implements QuickEntryTypeService {

    @Autowired
    private QuickEntryTypeMapper quickEntryTypeMapper;

    /**
     * add quick entry
     *
     * @param vo
     * @return
     */
    public boolean addQuickEntryType(QuickEntryVo vo) {
        if (!CollectionUtils.isEmpty(vo.getTypes())) {
            List<QuickEntryType> quickEntryTypes = this.entryTypeList(vo.getId());
            for (QuickEntryType quickEntryType : quickEntryTypes) {
                this.getBaseMapper().deleteById(quickEntryType);
            }
            for (Integer type : vo.getTypes()) {
                this.getBaseMapper().insert(QuickEntryType.builder()
                        .createTime(new Date())
                        .quickEntryId(vo.getId().longValue())
                        .type(type)
                        .typeName(type == 1 ? "PC" : "APP")
                        .build());
            }
        }
        return true;
    }

    private List<QuickEntryType> entryTypeList(Integer quickEntryId) {
        QueryWrapper<QuickEntryType> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        wrapper.eq("quick_entry_id", quickEntryId);
        return quickEntryTypeMapper.selectList(wrapper);
    }

    public List<QuickEntryType> quickEntryTypeList(Boolean isApp) {
        QueryWrapper<QuickEntryType> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        if (isApp) {
            wrapper.eq("type",2);
        } else {
            wrapper.eq("type", 1);
        }
        return quickEntryTypeMapper.selectList(wrapper);
    }
}
