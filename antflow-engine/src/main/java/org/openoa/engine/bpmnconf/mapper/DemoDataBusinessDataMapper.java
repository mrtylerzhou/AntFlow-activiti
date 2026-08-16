package org.openoa.engine.bpmnconf.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.openoa.engine.vo.DemoDataDepartmentVo;
import org.openoa.engine.vo.DemoDataRoleUserVo;
import org.openoa.engine.vo.DemoDataRoleVo;
import org.openoa.engine.vo.DemoDataUserVo;

import java.util.Collection;
import java.util.List;

/**
 * 业务数据(演示数据)列表查询
 */
@Mapper
public interface DemoDataBusinessDataMapper {

    /**
     * 按 mainId 集合 + formCode 批量查询低代码字段值
     * <p>注意:参数名必须为 formCode,分表路由拦截器依赖该参数名做一致性哈希路由</p>
     *
     * @param mainIds  lf_main 主键集合(bpm_business_process.business_id)
     * @param formCode 低代码流程 form_code
     * @return 字段值列表
     */
    List<LFMainField> selectLfMainFieldsByMainIds(@Param("mainIds") Collection<Long> mainIds,
                                                  @Param("formCode") String formCode);

    /**
     * 人员管理分页列表(关联部门名称/直属领导姓名/HRBP姓名)
     *
     * @param page     分页参数
     * @param userName 姓名模糊(可选)
     * @param mobile   手机号模糊(可选)
     */
    Page<DemoDataUserVo> selectUserPage(Page<DemoDataUserVo> page,
                                        @Param("userName") String userName,
                                        @Param("mobile") String mobile);

    /**
     * 部门管理分页列表(关联上级部门名称/负责人姓名)
     *
     * @param page    分页参数
     * @param deptName 部门名称模糊(可选)
     */
    Page<DemoDataDepartmentVo> selectDepartmentPage(Page<DemoDataDepartmentVo> page,
                                                    @Param("deptName") String deptName);

    /**
     * 角色管理分页列表(含角色下关联人数)
     *
     * @param page     分页参数
     * @param roleName 角色名称模糊(可选)
     */
    Page<DemoDataRoleVo> selectRolePage(Page<DemoDataRoleVo> page,
                                        @Param("roleName") String roleName);

    /**
     * 角色详情:角色下人员分页列表
     *
     * @param page   分页参数
     * @param roleId 角色ID
     */
    Page<DemoDataRoleUserVo> selectRoleUsers(Page<DemoDataRoleUserVo> page,
                                             @Param("roleId") Long roleId);
}
