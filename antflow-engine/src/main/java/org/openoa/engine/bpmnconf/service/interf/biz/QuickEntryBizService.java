package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.QuickEntry;
import org.openoa.engine.bpmnconf.mapper.QuickEntryMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.QuickEntryService;
import org.openoa.engine.vo.ProcessTypeInforVo;
import org.openoa.engine.vo.QuickEntryVo;

import java.util.List;

public interface QuickEntryBizService extends BizService<QuickEntryMapper, QuickEntryService, QuickEntry> {
    boolean editQuickEntry(QuickEntryVo vo);

    ProcessTypeInforVo allQuickEntry(String version);

    List<QuickEntry> listQuickEntry(List<Long> ids);

    List<QuickEntry> listQuickEntry(Boolean isApp);
}
