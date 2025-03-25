package org.openoa.engine.bpmnconf.service.impl;

import lombok.Data;
import org.openoa.base.entity.Employee;
import org.openoa.base.mapper.UserMapper;
import org.openoa.base.service.AfUserService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.util.EmployeeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Data
@Service
public class EmployeeServiceImpl {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AfUserService userService;
   public List<Employee> qryLiteEmployeeInfoByIds(Collection<String> ids){
        List<BaseIdTranStruVo> baseIdTranStruVos = userService.queryUserByIds(ids);
        return  EmployeeUtil.basicEmployeeInfos(baseIdTranStruVos);
    }
   public Employee qryLiteEmployeeInfoById(String id){
        BaseIdTranStruVo baseIdTranStruVo = userService.getById(id);
        return  EmployeeUtil.basicEmployeeInfo(baseIdTranStruVo);
    }
    public Employee getEmployeeDetailById(String id){
        return userMapper.getEmployeeDetailById(id);
    }
    public List<Employee> getEmployeeDetailByIds(Collection<String> ids){
        return userMapper.getEmployeeDetailByIds(ids);
    }
    public long checkEmployeeEffective(String id){
        return userMapper.checkEmployeeEffective(id);
    }
    public List<BaseIdTranStruVo> getLevelLeadersByEmployeeIdAndTier(String employeeId,Integer tier){
        return userMapper.getLevelLeadersByEmployeeIdAndTier(employeeId,tier);
    }
}
