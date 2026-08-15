package org.openoa.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.Role;
import org.openoa.base.entity.User;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author TylerZhou
 * @Date 2024/7/17 22:34
 * @Version 1.0
 */
@Mapper
public interface RoleMapper {
    List<BaseIdTranStruVo> queryRoleByIds(@Param("roleIds") Collection<String> roleIds);
    List<BaseIdTranStruVo> queryUserByRoleIds(@Param("roleIds") Collection<String> roleIds);

    LinkedList<BaseIdTranStruVo> selectAll();

    /**
     * 根据用户id查询其拥有的角色id集合
     */
    List<String> queryRoleIdsByUserId(@Param("userId") String userId);

    /**
     * 角色名称模糊查询
     */
    List<BaseIdTranStruVo> queryRoleByNameFuzzy(@Param("name") String name);
}
