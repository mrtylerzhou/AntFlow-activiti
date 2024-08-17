package org.openoa.base.service;

import org.openoa.base.entity.Role;
import org.openoa.base.entity.User;
import org.openoa.base.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * used to provide role employee info by role ids
 * @Author TylerZhou
 * @Date 2024/7/17 22:42
 * @Version 0.5
 */
@Service
public class RoleServiceImpl {
    @Autowired
    private RoleMapper rolesMapper;
    public List<Role> queryRoleByIds(Collection<Long> roleIds) {
        return rolesMapper.queryRoleByIds(roleIds);
    }
    public List<User> queryUserByRoleIds(Collection<Long> roleIds) {
        return rolesMapper.queryUserByRoleIds(roleIds);
    }
}
