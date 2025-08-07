package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openoa.base.constant.enums.ProcessJurisdictionEnum;
import org.openoa.base.entity.BpmProcessPermissions;
import org.openoa.base.entity.Department;
import org.openoa.base.util.SecurityUtils;
import org.openoa.engine.bpmnconf.service.impl.DepartmentServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.BpmProcessPermissionsBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BpmProcessPermissionsBizServiceImpl implements BpmProcessPermissionsBizService {
    @Autowired
    private DepartmentServiceImpl departmentService;

    /**
     * get a list of specified user's permissions
     */
    @Override
    public List<String> getProcessKey(String userId, Integer type) {
        QueryWrapper<BpmProcessPermissions> permissionsWrapper = new QueryWrapper<>();
        permissionsWrapper.eq("permissions_type", type);
        permissionsWrapper.eq("user_id", userId);
        List<BpmProcessPermissions> list = getMapper().selectList(permissionsWrapper);
        //根据员工获取下级部门
        List<Department> departmentVos = departmentService.ListSubDepartmentByEmployeeId(userId);
        List<Integer> depList = departmentVos.stream().map(Department::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(depList)) {
            QueryWrapper<BpmProcessPermissions> wrapper = new QueryWrapper<>();
            wrapper.eq("permissions_type", type);
            wrapper.in("dep_id", depList);
            List<BpmProcessPermissions> permissionsList = getMapper().selectList(wrapper);
            list.addAll(permissionsList);
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
