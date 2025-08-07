package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.QuickEntryType;
import org.openoa.engine.vo.QuickEntryVo;

import java.util.List;

public interface QuickEntryTypeService extends IService<QuickEntryType> {
    boolean addQuickEntryType(QuickEntryVo vo);

    List<QuickEntryType> quickEntryTypeList(Boolean isApp);
}
