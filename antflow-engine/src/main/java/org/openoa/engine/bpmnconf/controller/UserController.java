package org.openoa.engine.bpmnconf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;

import org.activiti.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Result;
import org.openoa.base.interf.anno.IgnoreLog;
import org.openoa.base.mapper.RoleMapper;
import org.openoa.base.mapper.UserMapper;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.*;
import org.openoa.common.mapper.BpmVariableMultiplayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@IgnoreLog
@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private AfUserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired(required = false)
    private TenantInfoHolder infoHolder;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private BpmVariableMultiplayerMapper bpmVariableMultiplayerMapper;

    @RequestMapping("/queryUserByNameFuzzy")
    public Result queryUserByNameFuzzy(String userName){
        if(StringUtils.isEmpty(userName)){
            return Result.newSuccessResult(Lists.newArrayList());
        }
        List<BaseIdTranStruVo> users = userService.queryByNameFuzzy(userName);
        return Result.newSuccessResult(users);
    }
    @RequestMapping("/queryCompanyByNameFuzzy")
    public Result queryCompanyByNameFuzzy(String companyName){
        if(StringUtils.isEmpty(companyName)){
            return Result.newSuccessResult(Lists.newArrayList());
        }
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
    @GetMapping("/queryNodeAssigneesByNodeId")
    public Result<List<BaseIdTranStruVo>> queryNodeAssigneesByNodeId(@RequestParam("processNumber")String processNumber,@RequestParam("nodeId")String nodeId){
        List<BaseInfoTranStructVo> baseInfoTranStructVos = bpmVariableMultiplayerMapper.getAssigneeAndVariableByNodeId(processNumber, nodeId);
        List<BaseIdTranStruVo> nodeAssignees = baseInfoTranStructVos.stream().map(a -> BaseIdTranStruVo.builder().id(a.getId()).name(a.getName()).build()).collect(Collectors.toList());
        return Result.newSuccessResult(nodeAssignees);
    }
    @GetMapping("/queryNodeAssigneesByElementId")
    public Result<List<BaseIdTranStruVo>> queryNodeAssigneeByElementId(@RequestParam("processNumber")String processNumber,@RequestParam("elementId")String elementId){
        List<BaseIdTranStruVo> assigneeByElementId = bpmVariableMultiplayerMapper.getAssigneeByElementId(processNumber, elementId);
        List<BaseIdTranStruVo> nodeAssignees = assigneeByElementId.stream().map(a -> BaseIdTranStruVo.builder().id(a.getId()).name(a.getName()).build()).collect(Collectors.toList());
        return Result.newSuccessResult(nodeAssignees);
    }
}
