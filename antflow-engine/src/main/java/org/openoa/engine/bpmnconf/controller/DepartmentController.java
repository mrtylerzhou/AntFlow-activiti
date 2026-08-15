package org.openoa.engine.bpmnconf.controller;

import org.openoa.base.entity.Department;
import org.openoa.base.entity.Result;
import org.openoa.base.service.AfDepartmentService;
import org.openoa.base.vo.ResultAndPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Classname DepartmentController
 * @Description department related interfaces
 * @since 0.5
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private AfDepartmentService departmentService;

    /**
     * 分页查询部门
     */
    @RequestMapping("/getDepartmentPageList")
    public ResultAndPage<Department> getDepartmentPageList(Integer page, Integer pageSize, String name) {
        return departmentService.getDepartmentPageList(page, pageSize, name);
    }

    /**
     * 根据部门名称模糊查询部门
     */
    @RequestMapping("/queryByNameFuzzy")
    public Result<List<Department>> queryByNameFuzzy(String name) {
        return Result.newSuccessResult(departmentService.queryByNameFuzzy(name));
    }

    /**
     * 根据部门id集合批量查询部门
     */
    @RequestMapping("/getByIds")
    public Result<List<Department>> getByIds(List<Integer> ids) {
        return Result.newSuccessResult(departmentService.getByIds(ids));
    }
}
