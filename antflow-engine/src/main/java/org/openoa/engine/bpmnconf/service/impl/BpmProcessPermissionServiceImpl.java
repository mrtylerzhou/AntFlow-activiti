package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.constant.enums.ProcessJurisdictionEnum;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.base.vo.DepartmentVo;
import org.openoa.engine.bpmnconf.confentity.BpmProcessPermission;
import org.openoa.base.entity.Department;
import org.openoa.engine.bpmnconf.mapper.BpmProcessPermissionMapper;
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
public class BpmProcessPermissionServiceImpl extends ServiceImpl<BpmProcessPermissionMapper, BpmProcessPermission> {

    @Autowired
    private BpmProcessPermissionMapper bpmProcessPermissionMapper;
    @Autowired
    private DepartmentServiceImpl departmentService;

    public void saveProcessPermissions(BpmProcessDeptVo vo) {
        GenericEmployee genericEmployee =new GenericEmployee();
        genericEmployee.setUserId(SecurityUtils.getLogInEmpIdSafe());
        genericEmployee.setUsername(SecurityUtils.getLogInEmpNameSafe());
        QueryWrapper<BpmProcessPermission> permissionsWrapper = new QueryWrapper<>();
        permissionsWrapper.eq("process_key", vo.getProcessKey());
        bpmProcessPermissionMapper.delete(permissionsWrapper);

        //employee's create permission
        if (!CollectionUtils.isEmpty(vo.getCreateUserIds())) {
            vo.getCreateUserIds().forEach(o -> {
                bpmProcessPermissionMapper.insert(BpmProcessPermission.builder()
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
                bpmProcessPermissionMapper.insert(BpmProcessPermission.builder()
                        .permissionsType(ProcessJurisdictionEnum.CREATE_TYPE.getCode())
                        .depId(o.toString())
                        .processKey(vo.getProcessKey())
                        .createUser(genericEmployee.getUserId())
                        .createTime(new Date())
                        .build());
            });
        }
        //process department readonly permissions
        if (!CollectionUtils.isEmpty(vo.getViewdeptIds())) {
            vo.getViewdeptIds().forEach(o -> {
                bpmProcessPermissionMapper.insert(BpmProcessPermission.builder()
                        .permissionsType(ProcessJurisdictionEnum.VIEW_TYPE.getCode())
                        .depId(o.toString())
                        .processKey(vo.getProcessKey())
                        .createUser(genericEmployee.getUserId())
                        .createTime(new Date())
                        .build());
            });
        }
        //employee readonly permissions
        if (!CollectionUtils.isEmpty(vo.getViewUserIds())) {
            vo.getViewUserIds().forEach(o -> {
                bpmProcessPermissionMapper.insert(BpmProcessPermission.builder()
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
                bpmProcessPermissionMapper.insert(BpmProcessPermission.builder()
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
                bpmProcessPermissionMapper.insert(BpmProcessPermission.builder()
                        .permissionsType(ProcessJurisdictionEnum.CONTROL_TYPE.getCode())
                        .depId(o.toString())
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
    public List<BpmProcessPermission> permissionsList(String processKey, Integer permissionsType, boolean isUser) {
        QueryWrapper<BpmProcessPermission> permissionsWrapper = new QueryWrapper<>();
        permissionsWrapper.eq("process_key", processKey);
        permissionsWrapper.eq("permissions_type", permissionsType);
        if (isUser) {
            permissionsWrapper.ne("user_id", ' ');
        } else {
            permissionsWrapper.ne("dep_id", ' ');
        }
        return bpmProcessPermissionMapper.selectList(permissionsWrapper);
    }

    /**
     * permission list
     *
     * @param processKey
     * @param permissionsType
     * @return
     */
    public List<BpmProcessPermission> permissionsList(String processKey, Integer permissionsType) {
        QueryWrapper<BpmProcessPermission> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        wrapper.eq("permissions_type", permissionsType);
        wrapper.ne("office_id", ' ');
        return bpmProcessPermissionMapper.selectList(wrapper);
    }

    /**
     * check whether the user has permission
     */
    public boolean getJurisdiction(String processKey) {
        List<String> processKeyList = this.getProcessKey(SecurityUtils.getLogInEmpIdSafe(), ProcessJurisdictionEnum.CONTROL_TYPE.getCode());
        if (processKeyList.contains(processKey)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * get a list of specified user's permissions
     */
    public List<String> getProcessKey(String userId, Integer type) {
        QueryWrapper<BpmProcessPermission> permissionsWrapper = new QueryWrapper<>();
        permissionsWrapper.eq("permissions_type", type);
        permissionsWrapper.eq("user_id", userId);
        List<BpmProcessPermission> list = bpmProcessPermissionMapper.selectList(permissionsWrapper);
        //根据员工获取下级部门
        List<DepartmentVo> departmentVos = departmentService.listSubDepartmentByEmployeeId(userId);
        List<Integer> depList = departmentVos.stream().map(DepartmentVo::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(depList)) {
            QueryWrapper<BpmProcessPermission> wrapper = new QueryWrapper<>();
            wrapper.eq("permissions_type", type);
            wrapper.in("dep_id", depList);
            List<BpmProcessPermission> permissionsList = bpmProcessPermissionMapper.selectList(wrapper);
            list.addAll(permissionsList);
        }

        //deduplication
        List<String> processKeyList = list.stream().map(BpmProcessPermission::getProcessKey).distinct().collect(Collectors.toList());
        return processKeyList;
    }

}
