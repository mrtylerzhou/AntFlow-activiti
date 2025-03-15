package org.openoa.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;

import org.activiti.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Employee;
import org.openoa.base.entity.Result;
import org.openoa.base.interf.anno.IgnoreLog;
import org.openoa.base.mapper.RoleMapper;
import org.openoa.base.mapper.UserMapper;
import org.openoa.base.service.UserServiceImpl;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
    private TenantInfoHolder infoHolder;

    @Autowired
    private RoleMapper roleMapper;

    @RequestMapping("/queryUserByNameFuzzy")
    public Result queryUserByNameFuzzy(String userName){
        int i = RandomUtils.nextInt();
        if(i%2==0){
            infoHolder.setCurrentTenantId("tenantA");
        }else{
            infoHolder.setCurrentTenantId("");
        }
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

    /**
     * 获取全部人员信息
     * @param roleId
     * @return
     */
    @GetMapping(value ={"/getUser/{roleId}","/getUser"})
    public Result<List<BaseIdTranStruVo>> getUsers(@PathVariable(required = false) Integer roleId) {
        LinkedList<BaseIdTranStruVo> list = userMapper.selectAll(roleId);
        return Result.newSuccessResult(list);

    }

    /**
     * 获取人员分页信息
     * @param requestDto
     * @return
     */
    @PostMapping("/getUserPageList")
    public ResultAndPage<BaseIdTranStruVo> getUserPageList( @RequestBody DetailRequestDto requestDto){
        PageDto pageDto = requestDto.getPageDto();
        Page<BaseIdTranStruVo> page = PageUtils.getPageByPageDto(pageDto);
        TaskMgmtVO taskMgmtVO = requestDto.getTaskMgmtVO();
        List<BaseIdTranStruVo> results = userMapper.selectUserPageList(page, taskMgmtVO);
        page.setRecords(results);
        return PageUtils.getResultAndPage(page);
    }

    /**
     * 获取角色信息
     * @return
     */
    @GetMapping("/getRoleInfo")
    public Result<List<BaseIdTranStruVo>> getRoleInfo(){
        LinkedList<BaseIdTranStruVo> list = roleMapper.selectAll();
        return Result.newSuccessResult(list);
    }
}
