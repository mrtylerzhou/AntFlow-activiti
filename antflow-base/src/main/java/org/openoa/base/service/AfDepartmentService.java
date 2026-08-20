package org.openoa.base.service;

import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ResultAndPage;

import java.util.List;

/**
 * @Classname AfDepartmentService
 * @Description abstraction over department service, users can provide their own
 * implementation and inject it wherever a department is needed
 * @since 0.0.1
 * @Created by AntOffice
 */
public interface AfDepartmentService {


    /**
     * 根据部门id查询部门
     *
     * @param id 部门id
     * @return 部门id+名称
     */
    BaseIdTranStruVo getDepartmentById(String id);

    /**
     * 根据员工id查询其下级部门
     *
     * @param employeeId 员工id
     * @return 部门id+名称集合
     */
    List<BaseIdTranStruVo> ListSubDepartmentByEmployeeId(String employeeId);

    /**
     * 根据id集合批量查询部门
     *
     * @param ids 部门id集合
     * @return 部门id+名称集合
     */
    List<BaseIdTranStruVo> getByIds(List<String> ids);

    /**
     * 根据部门名称模糊查询部门
     *
     * @param name 部门名称(模糊)
     * @return 部门id+名称集合
     */
    List<BaseIdTranStruVo> queryByNameFuzzy(String name);

    /**
     * 根据父级部门id查询直接子部门
     *
     * @param parentId 父级部门id
     * @return 子部门id+名称集合
     */
    List<BaseIdTranStruVo> getDepartmentsByParentId(String parentId);

    /**
     * 根据企业id查询部门列表
     *
     * @param companyId 企业id
     * @return 部门id+名称集合
     */
    List<BaseIdTranStruVo> getDepartmentByCompanyId(String companyId);

    /**
     * 部门分页查询
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @param name 部门名称(可选,模糊)
     * @return 分页结果(id+名称)
     */
    ResultAndPage<BaseIdTranStruVo> getDepartmentPageList(Integer page, Integer pageSize, String name);
}
