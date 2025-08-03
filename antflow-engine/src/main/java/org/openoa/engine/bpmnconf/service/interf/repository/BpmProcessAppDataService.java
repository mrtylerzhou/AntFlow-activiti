package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessAppData;

import java.util.List;

public interface BpmProcessAppDataService extends IService<BpmProcessAppData> {
    List<String> getBpmProcessAppDataVo(Long versionId, List<String> processKey);

    List<BpmProcessAppData> getProcessAppData(Long versionId, Integer isAll, Integer type);

    void deleteAppVersionData(Long versionId, Integer type);
}
