package org.openoa.base.service.empinfoprovider;

import com.google.common.collect.Maps;
import org.openoa.base.entity.Role;
import org.openoa.base.entity.User;
import org.openoa.base.service.AfRoleServiceImpl;
import org.openoa.base.vo.BaseIdTranStruVo;
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
    private AfRoleServiceImpl roleService;
    @Override
    public Map<String, String> provideRoleInfo(Collection<String> roleIds) {
        if(CollectionUtils.isEmpty(roleIds)){
            return Maps.newHashMap();
        }
        List<BaseIdTranStruVo> roles = roleService.queryRoleByIds(roleIds);
        if(CollectionUtils.isEmpty(roles)){
            return Maps.newHashMap();
        }
        return roles.stream().collect(Collectors.toMap(a -> a.getId().toString(), BaseIdTranStruVo::getName,(k1, k2)->k1));
    }

    @Override
    public Map<String, String> provideRoleEmployeeInfo(Collection<String> roleIds) {
        List<BaseIdTranStruVo> users = roleService.queryUserByRoleIds(roleIds);
        if(CollectionUtils.isEmpty(users)){
            return Maps.newHashMap();
        }
        return users.stream().collect(Collectors.toMap(a->a.getId().toString(),BaseIdTranStruVo::getName,(k1,k2)->k1));
    }
}
