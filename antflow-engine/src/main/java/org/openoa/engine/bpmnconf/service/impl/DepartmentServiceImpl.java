package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openoa.base.entity.Department;
import org.openoa.base.service.AfDepartmentService;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname DepartmentServiceImpl
 * @Description default implementation of {@link AfDepartmentService}, feel free to
 * provide your own implementation and inject {@link AfDepartmentService} wherever a department is needed
 * @since 0.5
 */
@Service
public class DepartmentServiceImpl implements AfDepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public Department getDepartmentById(Integer id){
        return departmentMapper.getDepartmentById(id);
    }

    @Override
    public List<Department> ListSubDepartmentByEmployeeId(String employeeId){
        return departmentMapper.ListSubDepartmentByEmployeeId(employeeId);
    }

    @Override
    public List<Department> getByIds(List<Integer> ids){
        if(ids == null || ids.isEmpty()){
            return java.util.Collections.emptyList();
        }
        return departmentMapper.getByIds(ids);
    }

    @Override
    public List<Department> queryByNameFuzzy(String name){
        return departmentMapper.queryByNameFuzzy(name);
    }

    @Override
    public List<Department> getDepartmentsByParentId(Integer parentId){
        return departmentMapper.getDepartmentsByParentId(parentId);
    }

    @Override
    public List<Department> getAllDepartments(){
        return departmentMapper.getAllDepartments();
    }

    @Override
    public List<Department> getDepartmentByCompanyId(String companyId){
        return departmentMapper.getDepartmentByCompanyId(companyId);
    }

    @Override
    public ResultAndPage<Department> getDepartmentPageList(Integer page, Integer pageSize, String name){
        if(page == null || page <= 0){
            page = 1;
        }
        if(pageSize == null || pageSize <= 0){
            pageSize = 10;
        }
        Page<Department> pageParam = new Page<>(page, pageSize);
        List<Department> list = departmentMapper.getDepartmentPageList(name);
        long total = departmentMapper.getDepartmentPageListCount(name);
        pageParam.setRecords(list);
        pageParam.setTotal(total);
        return PageUtils.getResultAndPage(pageParam);
    }
}
