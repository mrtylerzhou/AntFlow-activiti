package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.QuickEntry;
import org.openoa.engine.vo.QuickEntryVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * query entry mapper
 */
@Repository
public interface QuickEntryMapper extends BaseMapper<QuickEntry> {
    /**
     * all quick entry list
     *
     * @return
     */
    public List<QuickEntryVo> allQuickEntry(QuickEntryVo vo);


    public List<QuickEntryVo> listQuickEntry(Page page, QuickEntryVo vo);


    /**
     *  search quick entry
     * @return
     */
    public List<QuickEntryVo> searchQuickEntry(@Param("search") String search,@Param("limitSize") Integer limitSize);
}
