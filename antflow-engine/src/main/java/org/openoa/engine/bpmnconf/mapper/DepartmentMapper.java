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

    List<Department> getDepartmentsByParentId(@Param("parentId") Integer parentId);

    List<Department> getDepartmentByCompanyId(@Param("companyId") String companyId);

    List<Department> getDepartmentPageList(@Param("name") String name);

    Long getDepartmentPageListCount(@Param("name") String name);
}
