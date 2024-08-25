package org.openoa.base.mapper;

import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.Role;
import org.openoa.base.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/17 22:34
 * @Version 1.0
 */
@Repository
public interface RoleMapper {
    List<Role> queryRoleByIds(@Param("roleIds") Collection<Long> roleIds);
    List<User> queryUserByRoleIds(@Param("roleIds") Collection<Long> roleIds);
}
