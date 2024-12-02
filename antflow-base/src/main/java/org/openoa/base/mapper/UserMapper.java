package org.openoa.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.Employee;
import org.openoa.base.vo.BaseIdTranStruVo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


@Mapper
public interface UserMapper {
    // a nonce method
    List<BaseIdTranStruVo> queryCompanyByNameFuzzy(@Param("companyName") String companyName);
    //must be implemented
    List<BaseIdTranStruVo> queryByNameFuzzy(@Param("userName") String userName);
    //must be implemented
    List<BaseIdTranStruVo> queryByIds(@Param("userIds") Collection<String> userIds);
    //must be implemented
    Employee getEmployeeDetailById(@Param("employeeId") String id);
    //must be implemented
    List<Employee> getEmployeeDetailByIds(@Param("employeeIds")Collection<String> ids);
    long checkEmployeeEffective(@Param("employeeId") String id);

    //if you want to use level leader sign functions,you must implement it
    List<BaseIdTranStruVo> getLevelLeadersByEmployeeIdAndTier(@Param("employeeId") String employeeId,@Param("tier") Integer tier);
    BaseIdTranStruVo getHrpbByEmployeeId(@Param("employeeId") String employeeId);
    BaseIdTranStruVo getDirectLeaderByEmployeeId(@Param("employeeId") String employeeId);

    LinkedList<BaseIdTranStruVo> selectAll(@Param("roleId") Integer roleId);

    BaseIdTranStruVo getLeaderByLeventDepartment(@Param("startUserId") String startUserId,@Param("assignLevelGrade")Integer departmentLevel);
}
