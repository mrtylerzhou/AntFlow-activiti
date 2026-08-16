package org.openoa.base.service;

import org.openoa.base.entity.DetailedUser;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.LabelBasedApproverRuleVo;

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

    long checkEmployeeEffective(String id);
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

    /**
     * "根据标签选择"审批人规则找人接口(nodeProperty=20)
     * 在流程发起时由 LabelBasedPersonnelProvider 调用,根据标签和自定义变量从业务数据中解析审批人
     *
     * 默认实现返回空集合(表示无审批人,由节点"审批人为空时"策略处理)
     * 用户应在 AfUserServiceImpl 中改写此方法,实现真实的找人逻辑
     *
     * @param businessDataVo 流程运行时业务数据(含表单数据、发起人、被审批人等)
     * @param ruleConfig     根据标签选择规则配置(标签名、标签key、自定义变量组)
     * @return 审批人集合(id+name);空集合表示无人,由调用方按"审批人为空时"策略处理
     */
    List<BaseIdTranStruVo> queryApproversByLabel(BusinessDataVo businessDataVo, LabelBasedApproverRuleVo ruleConfig);
}
