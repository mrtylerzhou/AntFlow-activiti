package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.constant.enums.ProcessJurisdictionEnum;
import org.openoa.base.entity.BpmProcessPermissions;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.engine.bpmnconf.mapper.BpmProcessPermissionsMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmProcessPermissionsService;
import org.openoa.engine.vo.GenericEmployee;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;


/**
 * this service is about process's permission,it is your choise to use it or not,you can also add your own logic
 *
 * @since 0.5
 */
@Repository
public class BpmProcessPermissionsServiceImpl extends ServiceImpl<BpmProcessPermissionsMapper, BpmProcessPermissions> implements BpmProcessPermissionsService {


    public void saveProcessPermissions(BpmProcessDeptVo vo) {
        GenericEmployee genericEmployee =new GenericEmployee();
        genericEmployee.setUserId(SecurityUtils.getLogInEmpIdSafe());
        genericEmployee.setUsername(SecurityUtils.getLogInEmpNameSafe());
        QueryWrapper<BpmProcessPermissions> permissionsWrapper = new QueryWrapper<>();
        permissionsWrapper.eq("process_key", vo.getProcessKey());
        getBaseMapper().delete(permissionsWrapper);

        //employee's create permission
        if (!CollectionUtils.isEmpty(vo.getCreateUserIds())) {
            vo.getCreateUserIds().forEach(o -> {
                getBaseMapper().insert(BpmProcessPermissions.builder()
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
                getBaseMapper().insert(BpmProcessPermissions.builder()
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
                getBaseMapper().insert(BpmProcessPermissions.builder()
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
                getBaseMapper().insert(BpmProcessPermissions.builder()
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
                getBaseMapper().insert(BpmProcessPermissions.builder()
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
                getBaseMapper().insert(BpmProcessPermissions.builder()
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
                getBaseMapper().insert(BpmProcessPermissions.builder()
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
                getBaseMapper().insert(BpmProcessPermissions.builder()
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
    @Override
    public List<BpmProcessPermissions> permissionsList(String processKey, Integer permissionsType, boolean isUser) {
        QueryWrapper<BpmProcessPermissions> permissionsWrapper = new QueryWrapper<>();
        permissionsWrapper.eq("process_key", processKey);
        permissionsWrapper.eq("permissions_type", permissionsType);
        if (isUser) {
            permissionsWrapper.ne("user_id", ' ');
        } else {
            permissionsWrapper.ne("dep_id", ' ');
        }
        return getBaseMapper().selectList(permissionsWrapper);
    }

    /**
     * permission list
     *
     * @param processKey
     * @param permissionsType
     * @return
     */
    @Override
    public List<BpmProcessPermissions> permissionsList(String processKey, Integer permissionsType) {
        QueryWrapper<BpmProcessPermissions> wrapper = new QueryWrapper<>();
        wrapper.eq("process_key", processKey);
        wrapper.eq("permissions_type", permissionsType);
        wrapper.ne("office_id", ' ');
        return getBaseMapper().selectList(wrapper);
    }


}
