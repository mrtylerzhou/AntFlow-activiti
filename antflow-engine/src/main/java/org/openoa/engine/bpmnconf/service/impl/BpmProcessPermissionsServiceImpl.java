package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.constant.enums.ProcessJurisdictionEnum;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.engine.bpmnconf.confentity.BpmProcessPermissions;
import org.openoa.engine.bpmnconf.confentity.Department;
import org.openoa.engine.bpmnconf.mapper.BpmProcessPermissionsMapper;
import org.openoa.engine.vo.GenericEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * this service is about process's permission,it is your choise to use it or not,you can also add your own logic
 *
 * @since 0.5
 */
@Service
public class BpmProcessPermissionsServiceImpl extends ServiceImpl<BpmProcessPermissionsMapper, BpmProcessPermissions> {

    @Autowired
    private BpmProcessPermissionsMapper bpmProcessPermissionsMapper;
    @Autowired
    private DepartmentServiceImpl departmentService;

    public void saveProcessPermissions(BpmProcessDeptVo vo) {
        GenericEmployee genericEmployee =new GenericEmployee();
        genericEmployee.setUserId(SecurityUtils.getLogInEmpIdSafe());
        genericEmployee.setUsername(SecurityUtils.getLogInEmpNameSafe());
        QueryWrapper<BpmProcessPermissions> permissionsWrapper = new QueryWrapper<>();
        permissionsWrapper.eq("process_key", vo.getProcessKey());
        bpmProcessPermissionsMapper.delete(permissionsWrapper);

        //employee's create permission
        if (!CollectionUtils.isEmpty(vo.getCreateUserIds())) {
            vo.getCreateUserIds().forEach(o -> {
                bpmProcessPermissionsMapper.insert(BpmProcessPermissions.builder()
                        .permissionsType(ProcessJurisdictionEnum.CREATE_TYPE.getCode())
                        .userId(o)
                        .processKey(vo.getProcessKey())
                        .createUser(genericEmployee.getUserId())
                        .createTime(new Date())
                        .build());
            });
        }
        //process department permissions
        if (!CollectionUtils.isEmpty(vo.getCreateDeptIds())) {
            vo.getCreateDeptIds().forEach(o -> {
                bpmProcessPermissionsMapper.insert(BpmProcessPermissions.builder()
                        .permissionsType(ProcessJurisdictionEnum.CREATE_TYPE.getCode())
                        .depId(o)
                        .processKey(vo.getProcessKey())
                        .createUser(genericEmployee.getUserId())
                        .createTime(new Date())
                        .build());
            });
        }
        //process department readonly permissions
        if (!CollectionUtils.isEmpty(vo.getViewdeptIds())) {
            vo.getViewdeptIds().forEach(o -> {
                bpmProcessPermissionsMapper.insert(BpmProcessPermissions.builder()
                        .permissionsType(ProcessJurisdictionEnum.VIEW_TYPE.getCode())
                        .depId(o)
                        .processKey(vo.getProcessKey())
                        .createUser(genericEmployee.getUserId())
                        .createTime(new Date())
                        .build());
            });
        }
        //employee readonly permissions
        if (!CollectionUtils.isEmpty(vo.getViewUserIds())) {
            vo.getViewUserIds().forEach(o -> {
                bpmProcessPermissionsMapper.insert(BpmProcessPermissions.builder()
                        .permissionsType(ProcessJurisdictionEnum.VIEW_TYPE.getCode())
                        .userId(o)
                        .processKey(vo.getProcessKey())
                        .createUser(genericEmployee.getUserId())
                        .createTime(new Date())
                        .build());
            });
        }
        //employee control permissions
        if (!CollectionUtils.isEmpty(vo.getControlUserIds())) {
            vo.getControlUserIds().forEach(o -> {
                bpmProcessPermissionsMapper.insert(BpmProcessPermissions.builder()
                        .permissionsType(ProcessJurisdictionEnum.CONTROL_TYPE.getCode())
                        .userId(o)
                        .processKey(vo.getProcessKey())
                        .createUser(genericEmployee.getUserId())
                        .createTime(new Date())
                        .build());
            });
        }
        //department control permissions
        if (!CollectionUtils.isEmpty(vo.getControlDeptIds())) {
            vo.getControlDeptIds().forEach(o -> {
                bpmProcessPermissionsMapper.insert(BpmProcessPermissions.builder()
                        .permissionsType(ProcessJurisdictionEnum.CONTROL_TYPE.getCode())
                        .depId(o)
                        .processKey(vo.getProcessKey())
                        .createUser(genericEmployee.getUserId())
                        .createTime(new Date())
                        .build());
            });
        }
        //permissions based on working place
        if (!CollectionUtils.isEmpty(vo.getCreateOfficeIds())) {
            vo.getCreateOfficeIds().forEach(o -> {
                bpmProcessPermissionsMapper.insert(BpmProcessPermissions.builder()
                        .permissionsType(ProcessJurisdictionEnum.CREATE_TYPE.getCode())
                        .officeId(o)
                        .processKey(vo.getProcessKey())
                        .createUser(genericEmployee.getUserId())
                        .createTime(new Date())
                        .build());
            });
        }
        //permissions based on working place
        if (!CollectionUtils.isEmpty(vo.getViewOfficeIds())) {
            vo.getViewOfficeIds().forEach(o -> {
                bpmProcessPermissionsMapper.insert(BpmProcessPermissions.builder()
                        .permissionsType(ProcessJurisdictionEnum.VIEW_TYPE.getCode())
                        .officeId(o)
                        .processKey(vo.getProcessKey())
                        .createUser(genericEmployee.getUserId())
                        .createTime(new Date())
                        .build());
            });
        }
    }

    /**
     * readonly permissions
     *
     * @param processKey
     * @param permissionsType
     * @param isUser
     * @return
     */
    public List<BpmProcessPermissions> permissionsList(String processKey, Integer permissionsType, boolean isUser) {
        QueryWrapper<BpmProcessPermissions> permissionsWrapper = new QueryWrapper<>();
        permissionsWrapper.eq("process_key", processKey);
        permissionsWrapper.eq("permissions_type", permissionsType);
        if (isUser) {
            permissionsWrapper.ne("user_id", ' ');
        } else {
            permissionsWrapper.ne("dep_id", ' ');
        }
        return bpmProcessPermissionsMapper.selectList(permissionsWrapper);
    }

    /**
     * permission list
     *
     * @param processKey
     * @param permissionsType
     * @return
     */
    public List<BpmProcessPermissions> permissionsList(String processKey, Integer permissionsType) {
        QueryWrapper<BpmProcessPermissions> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        wrapper.eq("permissions_type", permissionsType);
        wrapper.ne("office_id", ' ');
        return bpmProcessPermissionsMapper.selectList(wrapper);
    }

    /**
     * check whether the user has permission
     */
    public boolean getJurisdiction(String processKey) {
        List<String> processKeyList = this.getProcessKey(SecurityUtils.getLogInEmpIdSafe().intValue(), ProcessJurisdictionEnum.CONTROL_TYPE.getCode());
        if (processKeyList.contains(processKey)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * get a list of specified user's permissions
     */
    public List<String> getProcessKey(Integer userId, Integer type) {
        QueryWrapper<BpmProcessPermissions> permissionsWrapper = new QueryWrapper<>();
        permissionsWrapper.eq("permissions_type", type);
        permissionsWrapper.eq("user_id", userId);
        List<BpmProcessPermissions> list = bpmProcessPermissionsMapper.selectList(permissionsWrapper);
        //根据员工获取下级部门
        List<Department> departmentVos = departmentService.ListSubDepartmentByEmployeeId(userId.longValue());
        List<Integer> depList = departmentVos.stream().map(Department::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(depList)) {
            QueryWrapper<BpmProcessPermissions> wrapper = new QueryWrapper<>();
            wrapper.eq("permissions_type", type);
            wrapper.in("dep_id", depList);
            List<BpmProcessPermissions> permissionsList = bpmProcessPermissionsMapper.selectList(wrapper);
            list.addAll(permissionsList);
        }

        //deduplication
        List<String> processKeyList = list.stream().map(BpmProcessPermissions::getProcessKey).distinct().collect(Collectors.toList());
        return processKeyList;
    }

}
