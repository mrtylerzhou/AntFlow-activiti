package org.openoa.engine.bpmnconf.service.interf;

import org.openoa.base.entity.DictData;
import org.openoa.base.interf.IAFService;
import org.openoa.engine.bpmnconf.mapper.DicDataMapper;

import java.util.List;

public interface DicDataService extends IAFService<DicDataMapper, DictData> {
    List<DictData> queryDicDataByCategory(String category);
}
