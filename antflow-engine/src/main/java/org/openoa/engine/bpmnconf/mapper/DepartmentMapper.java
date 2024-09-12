package org.openoa.engine.bpmnconf.mapper;


import org.apache.ibatis.annotations.Param;
import org.openoa.engine.bpmnconf.confentity.Department;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * department mapper,feel free to use it,AntFlow only need a few fields(such as name and id),you can mapping your department to AntFlow's
 * @since 0.5
 */
@Repository
public interface DepartmentMapper  {
    List<Department> ListSubDepartmentByEmployeeId(@Param("employeeId") String employeeId);

    Department getDepartmentByEmployeeId(@Param("employeeId") String employeeId);
}
