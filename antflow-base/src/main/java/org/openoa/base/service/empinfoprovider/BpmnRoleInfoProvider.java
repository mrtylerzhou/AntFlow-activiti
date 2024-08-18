package org.openoa.base.service.empinfoprovider;

import com.google.common.collect.Maps;
import org.openoa.base.entity.Role;
import org.openoa.base.entity.User;
import org.openoa.base.service.RoleServiceImpl;
import org.openoa.base.util.AntCollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author TylerZhou
 * @Date 2024/7/17 22:30
 * @Version 0.5
 */
@Service
public class BpmnRoleInfoProvider implements BpmnRoleInfoProviderService{
    @Autowired
    private RoleServiceImpl roleService;
    @Override
    public Map<String, String> provideRoleInfo(Collection<String> roleIds) {
        if(CollectionUtils.isEmpty(roleIds)){
            return Maps.newHashMap();
        }
        List<Role> roles = roleService.queryRoleByIds(AntCollectionUtil.StringToLongList(roleIds));
        if(CollectionUtils.isEmpty(roles)){
            return Maps.newHashMap();
        }
        return roles.stream().collect(Collectors.toMap(a -> a.getId().toString(), Role::getRoleName,(k1, k2)->k1));
    }

    @Override
    public Map<String, String> provideRoleEmployeeInfo(Collection<Long> roleIds) {
        List<User> users = roleService.queryUserByRoleIds(roleIds);
        if(CollectionUtils.isEmpty(users)){
            return Maps.newHashMap();
        }
        return users.stream().collect(Collectors.toMap(a->a.getId().toString(),User::getUserName,(k1,k2)->k1));
    }
}
