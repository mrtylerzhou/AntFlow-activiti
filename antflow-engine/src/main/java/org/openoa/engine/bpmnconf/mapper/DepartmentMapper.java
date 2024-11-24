package org.openoa.engine.bpmnconf.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.Department;

import java.util.List;

/**
 * department mapper,feel free to use it,AntFlow only need a few fields(such as name and id),you can mapping your department to AntFlow's
 * @since 0.5
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
    List<Department> ListSubDepartmentByEmployeeId(@Param("employeeId") String employeeId);

    Department getDepartmentByEmployeeId(@Param("employeeId") String employeeId);
}
