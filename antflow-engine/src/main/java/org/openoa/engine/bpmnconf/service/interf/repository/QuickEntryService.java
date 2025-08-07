package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.QuickEntry;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.vo.QuickEntryVo;

import java.util.List;

public interface QuickEntryService extends IService<QuickEntry> {
    boolean deleteQuickEntry(Long id);

    ResultAndPage<QuickEntryVo> findProcessList(PageDto pageDto, QuickEntryVo vo);

    Page<QuickEntryVo> getPcProcessData(Page<QuickEntryVo> page);

    List<QuickEntryVo> searchQuickEntry(String search, Integer limitSize);
}
