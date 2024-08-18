package org.openoa.engine.bpmnconf.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.openoa.engine.bpmnconf.confentity.Department;
import org.openoa.engine.bpmnconf.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * department service
 * generic department service
 * @since 0.5
 */
@Slf4j
@Service
public class DepartmentServiceImpl {
    @Autowired
    private DepartmentMapper departmentMapper;;

    public List<Department> ListSubDepartmentByEmployeeId(Long userId) {
        return departmentMapper.ListSubDepartmentByEmployeeId(userId);
    }
    public Department getDepartmentByEmployeeId(Long userId){
        return departmentMapper.getDepartmentByEmployeeId(userId);
    }
}
