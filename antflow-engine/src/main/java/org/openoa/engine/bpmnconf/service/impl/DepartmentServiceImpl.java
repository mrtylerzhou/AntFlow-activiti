package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openoa.base.entity.Department;
import org.openoa.base.service.AfDepartmentService;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        List<Department> matched = departmentMapper.queryByNameFuzzy(name);
        if(matched == null || matched.isEmpty()){
            return java.util.Collections.emptyList();
        }
        //补祖先链: 收集匹配记录 path 的全部前缀(如 /1/2/3 -> /1, /1/2), 一次查出祖先并合并返回
        java.util.Set<String> ancestorPaths = new java.util.LinkedHashSet<>();
        for (Department d : matched) {
            String path = d.getPath();
            if (!StringUtils.hasText(path)) {
                continue;
            }
            String[] segs = path.split("/");
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < segs.length - 1; i++) {
                sb.append('/').append(segs[i]);
                ancestorPaths.add(sb.toString());
            }
        }
        if (!ancestorPaths.isEmpty()) {
            List<Department> ancestors = departmentMapper.queryByPaths(new ArrayList<>(ancestorPaths));
            Map<Integer, Department> merged = new LinkedHashMap<>();
            for (Department a : ancestors) {
                merged.putIfAbsent(a.getId(), a);
            }
            for (Department m : matched) {
                merged.putIfAbsent(m.getId(), m);
            }
            return new ArrayList<>(merged.values());
        }
        return matched;
    }

    @Override
    public List<Department> getDepartmentsByParentId(Integer parentId){
        //初始两级: path 深度 1~2(根+根的直接子级), 前端一次请求渲染两级
        if(parentId == null){
            return departmentMapper.getTopTwoLevels();
        }
        //子节点: 先查父部门 path, 再按 path 前缀+深度查询直接子级
        Department parent = departmentMapper.getDepartmentById(parentId);
        if(parent == null || !StringUtils.hasText(parent.getPath())){
            return java.util.Collections.emptyList();
        }
        String parentPath = parent.getPath();
        int depth = (int) parentPath.chars().filter(ch -> ch == '/').count();
        return departmentMapper.getChildrenByParentPath(parentPath, depth);
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
