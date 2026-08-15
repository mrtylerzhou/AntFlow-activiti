package org.openoa.engine.bpmnconf.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.Department;

import java.util.List;

/**
 * department mapper,feel free to use it,AntFlow only need a few fields(such as name and id),you can mapping your department to AntFlow's
 * @since 0.5
 */
@Mapper
public interface DepartmentMapper  {
    List<Department> ListSubDepartmentByEmployeeId(@Param("employeeId") String employeeId);

    Department getDepartmentByEmployeeId(@Param("employeeId") String employeeId);

    Department getDepartmentById(@Param("id") Integer id);

    List<Department> getByIds(@Param("ids") List<Integer> ids);

    List<Department> queryByNameFuzzy(@Param("name") String name);

    /**
     * 树懒加载-根节点: path 深度=1 的部门
     */
    List<Department> getRootDepartments();

    /**
     * 树懒加载-子节点: path 以父path开头且深度=父深度+1
     *
     * @param parentPath 父部门 path
     * @param parentDepth 父部门 path 深度(段数)
     */
    List<Department> getChildrenByParentPath(@Param("parentPath") String parentPath, @Param("parentDepth") Integer parentDepth);

    /**
     * 按 path 集合批量查询部门(搜索补祖先链用)
     */
    List<Department> queryByPaths(@Param("paths") List<String> paths);

    List<Department> getDepartmentByCompanyId(@Param("companyId") String companyId);

    List<Department> getDepartmentPageList(@Param("name") String name);

    Long getDepartmentPageListCount(@Param("name") String name);
}
