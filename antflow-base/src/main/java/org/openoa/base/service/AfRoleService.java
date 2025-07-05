package org.openoa.base.service;

import org.openoa.base.entity.Role;
import org.openoa.base.entity.User;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.Collection;
import java.util.List;

public interface AfRoleService {
    List<BaseIdTranStruVo> queryRoleByIds(Collection<String> roleIds);

    List<BaseIdTranStruVo> queryUserByRoleIds(Collection<String> roleIds);
}
