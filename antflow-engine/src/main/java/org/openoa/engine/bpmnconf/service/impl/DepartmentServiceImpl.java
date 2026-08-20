package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openoa.base.entity.Department;
import org.openoa.base.service.AfDepartmentService;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Classname DepartmentServiceImpl
 * @Description default implementation of {@link AfDepartmentService}, feel free to
 * provide your own implementation and inject {@link AfDepartmentService} wherever a department is needed.
 * 抽象层方法返回 BaseIdTranStruVo(id+name), 完整数据方法(WithDetail 后缀)供 DepartmentController 等前端部门树接口使用
 * @since 0.5
 */
@Service
public class DepartmentServiceImpl implements AfDepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    // ==================== AfDepartmentService 抽象层(id+name) ====================

    @Override
    public BaseIdTranStruVo getDepartmentById(String id) {
        if (!StringUtils.hasText(id)) {
            return null;
        }
        return toVo(departmentMapper.getDepartmentById(Integer.valueOf(id)));
    }

    @Override
    public List<BaseIdTranStruVo> ListSubDepartmentByEmployeeId(String employeeId) {
        return toVos(departmentMapper.ListSubDepartmentByEmployeeId(employeeId));
    }

    @Override
    public List<BaseIdTranStruVo> getByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> intIds = ids.stream().filter(StringUtils::hasText).map(Integer::valueOf).collect(Collectors.toList());
        return toVos(departmentMapper.getByIds(intIds));
    }

    @Override
    public List<BaseIdTranStruVo> queryByNameFuzzy(String name) {
        return toVos(queryByNameFuzzyWithDetail(name));
    }

    @Override
    public List<BaseIdTranStruVo> getDepartmentsByParentId(String parentId) {
        Integer pid = StringUtils.hasText(parentId) ? Integer.valueOf(parentId) : null;
        return toVos(getDepartmentsByParentIdWithDetail(pid));
    }

    @Override
    public List<BaseIdTranStruVo> getDepartmentByCompanyId(String companyId) {
        return toVos(departmentMapper.getDepartmentByCompanyId(companyId));
    }

    @Override
    public ResultAndPage<BaseIdTranStruVo> getDepartmentPageList(Integer page, Integer pageSize, String name) {
        ResultAndPage<Department> detail = getDepartmentPageListWithDetail(page, pageSize, name);
        return new ResultAndPage<>(toVos(detail.getData()), detail.getPagination());
    }

    // ==================== 完整数据方法(供 DepartmentController 等前端部门树接口使用) ====================

    public List<Department> getByIdsWithDetail(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return departmentMapper.getByIds(ids);
    }

    public List<Department> queryByNameFuzzyWithDetail(String name) {
        List<Department> matched = departmentMapper.queryByNameFuzzy(name);
        if (matched == null || matched.isEmpty()) {
            return Collections.emptyList();
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

    public List<Department> getDepartmentsByParentIdWithDetail(Integer parentId) {
        //初始两级: path 深度 1~2(根+根的直接子级), 前端一次请求渲染两级
        if (parentId == null) {
            return departmentMapper.getTopTwoLevels();
        }
        //子节点: 先查父部门 path, 再按 path 前缀+深度查询直接子级
        Department parent = departmentMapper.getDepartmentById(parentId);
        if (parent == null || !StringUtils.hasText(parent.getPath())) {
            return Collections.emptyList();
        }
        String parentPath = parent.getPath();
        int depth = (int) parentPath.chars().filter(ch -> ch == '/').count();
        return departmentMapper.getChildrenByParentPath(parentPath, depth);
    }

    public ResultAndPage<Department> getDepartmentPageListWithDetail(Integer page, Integer pageSize, String name) {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        Page<Department> pageParam = new Page<>(page, pageSize);
        List<Department> list = departmentMapper.getDepartmentPageList(name);
        long total = departmentMapper.getDepartmentPageListCount(name);
        pageParam.setRecords(list);
        pageParam.setTotal(total);
        return PageUtils.getResultAndPage(pageParam);
    }

    // ==================== 转换 ====================

    private BaseIdTranStruVo toVo(Department d) {
        if (d == null) {
            return null;
        }
        return BaseIdTranStruVo.builder().id(String.valueOf(d.getId())).name(d.getName()).build();
    }

    private List<BaseIdTranStruVo> toVos(List<Department> deps) {
        if (deps == null || deps.isEmpty()) {
            return Collections.emptyList();
        }
        return deps.stream().map(this::toVo).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
