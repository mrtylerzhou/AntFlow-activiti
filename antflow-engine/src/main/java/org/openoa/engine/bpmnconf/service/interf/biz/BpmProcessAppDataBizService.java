package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmProcessAppData;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAppDataMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessAppDataService;
import org.openoa.engine.vo.SysVersionVo;

import java.util.List;

public interface BpmProcessAppDataBizService extends BizService<BpmProcessAppDataMapper, BpmProcessAppDataService, BpmProcessAppData>{
    SysVersionVo findMaxAppData();

    List<BaseIdTranStruVo> findAppDataByVersionId(Long versionId, Integer type);

    void addAppVersionData(List<Long> applicationIds, Long versionId, Integer type);

    void addVersionData(List<Long> applicationIds, Long versionId, Integer type);
}
