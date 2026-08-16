package org.openoa.base.service;

import org.openoa.base.entity.DetailedUser;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.UserAvailableVo;

import java.util.Collection;
import java.util.List;

public interface AfUserService {
    List<BaseIdTranStruVo> queryByNameFuzzy(String userName);

    List<BaseIdTranStruVo> queryCompanyByNameFuzzy(String companyName);

    List<BaseIdTranStruVo> queryUserByIds(Collection<String> userIds);

    BaseIdTranStruVo getById(String id);

    List<BaseIdTranStruVo> queryLeadersByEmployeeIdAndTier(String employeeId, Integer tier);

    List<BaseIdTranStruVo> queryLeadersByEmployeeIdAndGrade(String employeeId, Integer grade);

    BaseIdTranStruVo queryLeaderByEmployeeIdAndLevel(String employeeId, Integer level);

    List<BaseIdTranStruVo> queryEmployeeHrpbByEmployeeIds(List<String> employeeIds);

    List<BaseIdTranStruVo> queryEmployeeDirectLeaderByIds(List<String> employeeIds);


    DetailedUser getEmployeeDetailById(String id);

    List<DetailedUser> getEmployeeDetailByIds(Collection<String> ids);

    /**
     * 查询员工可用性(办公状态),并返回不可用时需要转办的目标人。
     * 默认实现为骨架空实现(恒返回可用),真实数据由使用方对接员工表/工作日历表等自行实现。
     *
     * @param id 用户id
     * @return 可用性结果:available(是否可用)、unavailableBeginTime/unavailableEndTime(不可用时间窗口)、
     *         delegateUser(不可用且需要转办时的目标人)
     */
    UserAvailableVo checkEmployeeEffective(String id);
    List<BaseIdTranStruVo> queryDepartmentLeaderByIds(List<String> employeeIds);

    /**
     * 查询用户拥有的角色列表(id+name)
     *
     * @param userId 用户 id
     */
    List<BaseIdTranStruVo> getUserRolesById(String userId);

    /**
     * 查询用户所在部门及其全部子部门(id+name),用于权限匹配(从主体出发,不展开部门全量人员)
     *
     * @param userId 用户 id
     */
    List<BaseIdTranStruVo> getUserDepartmentsById(String userId);
}
