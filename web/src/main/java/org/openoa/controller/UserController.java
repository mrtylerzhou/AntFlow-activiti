package org.openoa.controller;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.openoa.base.entity.Employee;
import org.openoa.base.entity.Result;
import org.openoa.base.interf.anno.IgnoreLog;
import org.openoa.base.mapper.RoleMapper;
import org.openoa.base.mapper.UserMapper;
import org.openoa.base.service.UserServiceImpl;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@IgnoreLog
@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private RoleMapper roleMapper;

    @RequestMapping("/queryUserByNameFuzzy")
    public Result queryUserByNameFuzzy(String userName){
        Employee employeeDetailById = userMapper.getEmployeeDetailById("1");
        if(StringUtils.isEmpty(userName)){
            return Result.newSuccessResult(Lists.newArrayList());
        }
        List<BaseIdTranStruVo> users = userService.queryByNameFuzzy(userName);
        return Result.newSuccessResult(users);
    }
    @RequestMapping("/queryCompanyByNameFuzzy")
    public Result queryCompanyByNameFuzzy(String companyName){
        List<BaseIdTranStruVo> codeTranStruVos = userService.queryCompanyByNameFuzzy(companyName);
        return Result.newSuccessResult(codeTranStruVos);
    }


    @GetMapping(value ={"/getUser/{roleId}","/getUser"})
    public Result<List<BaseIdTranStruVo>> getUsers(@PathVariable(required = false) Integer roleId) {
        LinkedList<BaseIdTranStruVo> list = userMapper.selectAll(roleId);
        return Result.newSuccessResult(list);

    }

    @GetMapping("/getRoleInfo")
    public Result<List<BaseIdTranStruVo>> getRoleInfo(){
        LinkedList<BaseIdTranStruVo> list = roleMapper.selectAll();
        return Result.newSuccessResult(list);
    }
}
