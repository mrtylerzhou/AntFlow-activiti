package org.openoa.base.mapper;

import org.apache.ibatis.annotations.Param;
import org.openoa.base.entity.Employee;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserMapper {
    // a nonce method
    List<BaseIdTranStruVo> queryCompanyByNameFuzzy(@Param("companyName") String companyName);
    //must be implemented
    List<BaseIdTranStruVo> queryByNameFuzzy(@Param("userName") String userName);
    //must be implemented
    List<BaseIdTranStruVo> queryByIds(@Param("userIds") Collection<Long> userIds);
    //must be implemented
    Employee getEmployeeDetailById(@Param("id") Long id);
    //must be implemented
    List<Employee> getEmployeeDetailByIds(@Param("ids")Collection<Long> ids);
    long checkEmployeeEffective(@Param("id") Long id);

    //if you want to use level leader sign functions,you must implement it
    List<BaseIdTranStruVo> getLevelLeadersByEmployeeIdAndTier(@Param("employeeId") Long employeeId,@Param("tier") Integer tier);
    BaseIdTranStruVo getHrpbByEmployeeId(@Param("employeeId") Long employeeId);
    BaseIdTranStruVo getDirectLeaderByEmployeeId(@Param("employeeId") Long employeeId);
}
