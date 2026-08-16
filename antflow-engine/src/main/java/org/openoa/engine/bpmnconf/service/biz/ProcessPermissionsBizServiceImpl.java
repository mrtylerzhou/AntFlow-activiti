package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.openoa.base.constant.enums.ProcessJurisdictionEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.dto.ProcessPermissionsPageReq;
import org.openoa.base.entity.BpmProcessPermissions;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.Department;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.mapper.RoleMapper;
import org.openoa.base.service.AfDepartmentService;
import org.openoa.base.service.AfRoleService;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.MultiTenantUtil;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.ProcessPermissionsListVo;
import org.openoa.base.vo.ProcessPermissionsSaveResult;
import org.openoa.base.vo.ProcessPermissionsSaveVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.impl.BpmProcessPermissionsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程权限管理 业务服务
 * 管理 bpm_process_permissions 表(查看/创建/监控权限)
 */
@Service
@Slf4j
public class ProcessPermissionsBizServiceImpl {

    @Autowired
    private BpmProcessPermissionsServiceImpl processPermissionsService;
    @Autowired
    private BpmnConfBizServiceImpl bpmnConfBizService;
    @Autowired
    private AfUserService afUserService;
    @Autowired
    private AfDepartmentService afDepartmentService;
    @Autowired
    private AfRoleService afRoleService;
    @Autowired
    private RoleMapper roleMapper;

    // ==================== 列表 ====================

    public ResultAndPage<ProcessPermissionsListVo> listPage(ProcessPermissionsPageReq req) {
        PageDto pageDto = req.getPageDto() == null ? PageDto.first() : req.getPageDto();
        Page<BpmProcessPermissions> page = PageUtils.getPageByPageDto(pageDto);

        QueryWrapper<BpmProcessPermissions> qw = new QueryWrapper<>();
        qw.like(StringUtils.hasText(req.getFormCode()), "process_key", req.getFormCode());
        qw.eq(req.getPermissionsType() != null, "permissions_type", req.getPermissionsType());
        //授权对象精确过滤: 下拉搜索选中后传 objectType + objectId(优先于 objectName)
        if (req.getObjectType() != null && req.getObjectType() == 4) {
            //全员: 只按 object_type=4 过滤,无对象 id
            qw.eq("object_type", 4);
        } else if (StringUtils.hasText(req.getObjectId())) {
            qw.eq(req.getObjectType() != null, "object_type", req.getObjectType());
            qw.eq("object_id", req.getObjectId());
        } else if (StringUtils.hasText(req.getObjectName())) {
            //授权对象名称关键字 -> 后置查询用户/部门/角色 id 集合(不 join demo 表)
            List<String> userIds = resolveUserIdsByName(req.getObjectName());
            List<String> depIds = resolveDepIdsByName(req.getObjectName());
            List<String> roleIds = resolveRoleIdsByName(req.getObjectName());
            if (CollectionUtils.isEmpty(userIds) && CollectionUtils.isEmpty(depIds) && CollectionUtils.isEmpty(roleIds)) {
                return PageUtils.getResultAndPage(new Page<>(page.getCurrent(), page.getSize()));
            }
            qw.and(w -> {
                boolean first = true;
                if (!CollectionUtils.isEmpty(userIds)) {
                    w.eq("object_type", 1).in("object_id", userIds);
                    first = false;
                }
                if (!CollectionUtils.isEmpty(depIds)) {
                    if (!first) {
                        w.or();
                    }
                    w.eq("object_type", 2).in("object_id", depIds);
                    first = false;
                }
                if (!CollectionUtils.isEmpty(roleIds)) {
                    if (!first) {
                        w.or();
                    }
                    w.eq("object_type", 3).in("object_id", roleIds);
                }
            });
        }
        qw.orderByDesc("create_time");

        Page<BpmProcessPermissions> result = processPermissionsService.getBaseMapper().selectPage(page, qw);
        List<BpmProcessPermissions> records = result.getRecords();
        List<ProcessPermissionsListVo> vos = CollectionUtils.isEmpty(records)
                ? Collections.emptyList()
                : buildListVos(records);
        return PageUtils.getResultAndPage(vos, PageUtils.getPageDto(result));
    }

    private List<ProcessPermissionsListVo> buildListVos(List<BpmProcessPermissions> records) {
        //1. 流程名称: formCode -> bpmn_name(effective_status=1)
        Set<String> formCodes = records.stream().map(BpmProcessPermissions::getProcessKey).collect(Collectors.toSet());
        Map<String, String> formCode2Name = new HashMap<>();
        List<BpmnConf> confs = bpmnConfBizService.getBpmnConfByFormCodeBatch(new ArrayList<>(formCodes));
        if (!CollectionUtils.isEmpty(confs)) {
            for (BpmnConf conf : confs) {
                formCode2Name.putIfAbsent(conf.getFormCode(), conf.getBpmnName());
            }
        }
        //2. 人员名称: objectType=1 -> name
        List<String> userIds = records.stream()
                .filter(e -> Integer.valueOf(1).equals(e.getObjectType()))
                .map(BpmProcessPermissions::getObjectId)
                .filter(StringUtils::hasText).distinct().collect(Collectors.toList());
        Map<String, String> userId2Name = new HashMap<>();
        if (!CollectionUtils.isEmpty(userIds)) {
            List<BaseIdTranStruVo> users = afUserService.queryUserByIds(userIds);
            if (!CollectionUtils.isEmpty(users)) {
                for (BaseIdTranStruVo u : users) {
                    userId2Name.putIfAbsent(u.getId(), u.getName());
                }
            }
        }
        //3. 部门名称: objectType=2 -> name
        List<Integer> depIds = records.stream()
                .filter(e -> Integer.valueOf(2).equals(e.getObjectType()))
                .map(BpmProcessPermissions::getObjectId)
                .filter(StringUtils::hasText).map(Integer::valueOf).distinct().collect(Collectors.toList());
        Map<Integer, String> depId2Name = new HashMap<>();
        if (!CollectionUtils.isEmpty(depIds)) {
            List<Department> deps = afDepartmentService.getByIds(depIds);
            if (!CollectionUtils.isEmpty(deps)) {
                for (Department d : deps) {
                    depId2Name.putIfAbsent(d.getId(), d.getName());
                }
            }
        }
        //4. 角色名称: objectType=3 -> name
        List<String> roleIds = records.stream()
                .filter(e -> Integer.valueOf(3).equals(e.getObjectType()))
                .map(BpmProcessPermissions::getObjectId)
                .filter(StringUtils::hasText).distinct().collect(Collectors.toList());
        Map<String, String> roleId2Name = new HashMap<>();
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<BaseIdTranStruVo> roles = afRoleService.queryRoleByIds(roleIds);
            if (!CollectionUtils.isEmpty(roles)) {
                for (BaseIdTranStruVo r : roles) {
                    roleId2Name.putIfAbsent(r.getId(), r.getName());
                }
            }
        }

        //5. 创建人名称: createUser id -> name
        List<String> createUserIds = records.stream()
                .map(BpmProcessPermissions::getCreateUser)
                .filter(StringUtils::hasText).distinct().collect(Collectors.toList());
        Map<String, String> createUserId2Name = new HashMap<>();
        if (!CollectionUtils.isEmpty(createUserIds)) {
            List<BaseIdTranStruVo> creators = afUserService.queryUserByIds(createUserIds);
            if (!CollectionUtils.isEmpty(creators)) {
                for (BaseIdTranStruVo c : creators) {
                    createUserId2Name.putIfAbsent(c.getId(), c.getName());
                }
            }
        }

        return records.stream().map(e -> {
            int objectType = e.getObjectType() == null ? 1 : e.getObjectType();
            boolean isDepartment = objectType == 2;
            String objectName;
            if (objectType == 4) {
                //全员: 对象固定 ALL
                objectName = "全员";
            } else if (objectType == 3) {
                objectName = roleId2Name.getOrDefault(e.getObjectId(), e.getObjectId());
            } else if (objectType == 2) {
                objectName = depId2Name.getOrDefault(Integer.valueOf(e.getObjectId()), e.getObjectId());
            } else {
                objectName = userId2Name.getOrDefault(e.getObjectId(), e.getObjectId());
            }
            return ProcessPermissionsListVo.builder()
                    .id(e.getId())
                    .processKey(e.getProcessKey())
                    .bpmnName(formCode2Name.get(e.getProcessKey()))
                    .permissionsType(e.getPermissionsType())
                    .isDepartment(isDepartment)
                    .objectType(objectType)
                    .objectName(objectName)
                    .createUser(e.getCreateUser())
                    .createUserName(createUserId2Name.getOrDefault(e.getCreateUser(), e.getCreateUser()))
                    .createTime(e.getCreateTime())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * 根据人员名称关键字查询匹配的用户id集合
     */
    private List<String> resolveUserIdsByName(String name) {
        List<BaseIdTranStruVo> users = afUserService.queryByNameFuzzy(name);
        return CollectionUtils.isEmpty(users)
                ? Collections.emptyList()
                : users.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
    }

    /**
     * 根据部门名称关键字查询匹配的部门id集合
     */
    private List<String> resolveDepIdsByName(String name) {
        List<Department> deps = afDepartmentService.queryByNameFuzzy(name);
        return CollectionUtils.isEmpty(deps)
                ? Collections.emptyList()
                : deps.stream().map(d -> String.valueOf(d.getId())).collect(Collectors.toList());
    }

    /**
     * 根据角色名称关键字查询匹配的角色id集合
     */
    private List<String> resolveRoleIdsByName(String name) {
        List<BaseIdTranStruVo> roles = roleMapper.queryRoleByNameFuzzy(name);
        return CollectionUtils.isEmpty(roles)
                ? Collections.emptyList()
                : roles.stream().map(BaseIdTranStruVo::getId).collect(Collectors.toList());
    }

    // ==================== 保存(三层笛卡尔积,幂等跳过) ====================

    @Transactional
    public ProcessPermissionsSaveResult save(ProcessPermissionsSaveVo vo) {
        if (CollectionUtils.isEmpty(vo.getProcessKeys())) {
            throw new AFBizException("400001", "请选择流程");
        }
        if (CollectionUtils.isEmpty(vo.getPermissionsTypes())) {
            throw new AFBizException("400002", "请选择权限类型");
        }
        //对象类型: 兼容旧调用(isDepartment)与新增的 objectType(1=人员 2=部门 3=角色 4=全员)
        Integer objectType = vo.getObjectType();
        if (objectType == null) {
            objectType = Boolean.TRUE.equals(vo.getIsDepartment()) ? 2 : 1;
        }
        if (objectType != 1 && objectType != 2 && objectType != 3 && objectType != 4) {
            throw new AFBizException("400008", "授权对象类型不合法");
        }
        //全员(object_type=4):仅支持创建权限,对象固定为 ALL
        List<String> objectIds = vo.getObjectIds();
        if (objectType == 4) {
            if (vo.getPermissionsTypes().size() != 1
                    || !vo.getPermissionsTypes().contains(ProcessJurisdictionEnum.CREATE_TYPE.getCode())) {
                throw new AFBizException("400009", "全员权限仅支持选择创建权限");
            }
            objectIds = Collections.singletonList("ALL");
        } else {
            //部门权限禁止监控/模板编辑
            if (objectType == 2 && (vo.getPermissionsTypes().contains(ProcessJurisdictionEnum.CONTROL_TYPE.getCode())
                    || vo.getPermissionsTypes().contains(ProcessJurisdictionEnum.TEMPLATE_EDIT_TYPE.getCode()))) {
                throw new AFBizException("400003", "部门权限不支持选择监控/模板编辑权限");
            }
            if (CollectionUtils.isEmpty(objectIds)) {
                String msg = objectType == 1 ? "请选择人员" : (objectType == 2 ? "请选择部门" : "请选择角色");
                throw new AFBizException(objectType == 1 ? "400005" : (objectType == 2 ? "400004" : "400007"), msg);
            }
        }

        String loginUserId = SecurityUtils.getLogInEmpIdSafe();
        List<BpmProcessPermissions> toInsert = new ArrayList<>();
        int skipCount = 0;
        for (String processKey : vo.getProcessKeys()) {
            for (Integer permissionsType : vo.getPermissionsTypes()) {
                for (String objectId : objectIds) {
                    if (exists(processKey, permissionsType, objectType, objectId)) {
                        skipCount++;
                    } else {
                        toInsert.add(BpmProcessPermissions.builder()
                                .processKey(processKey)
                                .permissionsType(permissionsType)
                                .objectType(objectType)
                                .objectId(objectId)
                                .createUser(loginUserId)
                                .createTime(new Date())
                                .tenantId(MultiTenantUtil.getCurrentTenantId())
                                .build());
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(toInsert)) {
            processPermissionsService.saveBatch(toInsert);
        }
        return ProcessPermissionsSaveResult.builder()
                .insertCount(toInsert.size())
                .skipCount(skipCount)
                .build();
    }

    /**
     * 幂等判断: process_key + permissions_type + object_type + object_id 四者一致视为已存在
     */
    private boolean exists(String processKey, Integer permissionsType, Integer objectType, String objectId) {
        QueryWrapper<BpmProcessPermissions> qw = new QueryWrapper<>();
        qw.eq("process_key", processKey)
                .eq("permissions_type", permissionsType)
                .eq("object_type", objectType)
                .eq("object_id", objectId);
        return processPermissionsService.getBaseMapper().selectCount(qw) > 0;
    }

    // ==================== 删除(物理) ====================

    public void delete(Long id) {
        BpmProcessPermissions permission = processPermissionsService.getBaseMapper().selectById(id);
        if (permission == null) {
            throw new AFBizException("400006", "权限记录不存在");
        }
        processPermissionsService.getBaseMapper().deleteById(id);
    }
}
