package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessPermissions;

import java.util.List;

public interface BpmProcessPermissionsService extends IService<BpmProcessPermissions> {
    List<BpmProcessPermissions> permissionsList(String processKey, Integer permissionsType, boolean isUser);

    List<BpmProcessPermissions> permissionsList(String processKey, Integer permissionsType);
}
