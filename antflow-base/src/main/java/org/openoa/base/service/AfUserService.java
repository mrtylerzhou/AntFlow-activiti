package org.openoa.base.service;

import org.openoa.base.entity.Employee;
import org.openoa.base.vo.BaseIdTranStruVo;

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

    BaseIdTranStruVo queryEmployeeHrpbByEmployeeId(String employeeId);

    List<BaseIdTranStruVo> queryEmployeeDirectLeaderByIds(List<String> employeeIds);


    Employee getEmployeeDetailById(String id);

    List<Employee> getEmployeeDetailByIds(Collection<String> ids);

    long checkEmployeeEffective(String id);
}
