package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openoa.base.constant.enums.ProcessJurisdictionEnum;
import org.openoa.base.entity.BpmProcessPermissions;
import org.openoa.base.mapper.RoleMapper;
import org.openoa.base.service.AfDepartmentService;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessPermissionsBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BpmProcessPermissionsBizServiceImpl implements BpmProcessPermissionsBizService {
    @Autowired
    private AfDepartmentService departmentService;
    @Autowired
    private RoleMapper roleMapper;

    /**
     * get a list of specified user's permissions
     */
    @Override
    public List<String> getProcessKey(String userId, Integer type) {
        QueryWrapper<BpmProcessPermissions> permissionsWrapper = new QueryWrapper<>();
        permissionsWrapper.eq("permissions_type", type);
        permissionsWrapper.eq("object_type", 1);
        permissionsWrapper.eq("object_id", userId);
        List<BpmProcessPermissions> list = getMapper().selectList(permissionsWrapper);
        //根据员工获取下级部门
        List<BaseIdTranStruVo> departmentVos = departmentService.ListSubDepartmentByEmployeeId(userId);
        List<String> depList = departmentVos.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(depList)) {
            QueryWrapper<BpmProcessPermissions> wrapper = new QueryWrapper<>();
            wrapper.eq("permissions_type", type);
            wrapper.eq("object_type", 2);
            wrapper.in("object_id", depList);
            List<BpmProcessPermissions> permissionsList = getMapper().selectList(wrapper);
            list.addAll(permissionsList);
        }
        //根据用户拥有的角色匹配角色权限
        List<String> roleIds = roleMapper.queryRoleIdsByUserId(userId);
        if (!CollectionUtils.isEmpty(roleIds)) {
            QueryWrapper<BpmProcessPermissions> roleWrapper = new QueryWrapper<>();
            roleWrapper.eq("permissions_type", type);
            roleWrapper.eq("object_type", 3);
            roleWrapper.in("object_id", roleIds);
            List<BpmProcessPermissions> rolePermissionsList = getMapper().selectList(roleWrapper);
            list.addAll(rolePermissionsList);
        }

        //deduplication
        List<String> processKeyList = list.stream().map(BpmProcessPermissions::getProcessKey).distinct().collect(Collectors.toList());
        return processKeyList;
    }
    /**
     * check whether the user has permission
     */
    @Override
    public boolean getJurisdiction(String processKey) {
        List<String> processKeyList = this.getProcessKey(SecurityUtils.getLogInEmpIdSafe(), ProcessJurisdictionEnum.CONTROL_TYPE.getCode());
        if (processKeyList.contains(processKey)) {
            return true;
        } else {
            return false;
        }
    }
}
