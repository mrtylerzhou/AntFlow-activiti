package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmProcessPermissions;
import org.openoa.engine.bpmnconf.mapper.BpmProcessPermissionsMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessPermissionsService;

import java.util.List;

public interface BpmProcessPermissionsBizService extends BizService<BpmProcessPermissionsMapper, BpmProcessPermissionsService, BpmProcessPermissions>{
    List<String> getProcessKey(String userId, Integer type);

    boolean getJurisdiction(String processKey);
}
