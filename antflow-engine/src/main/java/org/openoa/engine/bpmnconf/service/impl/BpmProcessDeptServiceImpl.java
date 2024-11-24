package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.activiti.engine.RepositoryService;
import org.openoa.base.constant.enums.ProcessJurisdictionEnum;
import org.openoa.base.constant.enums.ProcessNoticeEnum;
import org.openoa.base.constant.enums.SortTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.service.BpmProcessDeptService;
import org.openoa.base.service.empinfoprovider.BpmnEmployeeInfoProviderService;
import org.openoa.base.util.AntCollectionUtil;
import org.openoa.base.util.JiMuCommonUtils;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmProcessDeptVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.confentity.BpmProcessDept;
import org.openoa.engine.bpmnconf.confentity.BpmProcessNotice;
import org.openoa.engine.bpmnconf.confentity.BpmProcessPermission;
import org.openoa.engine.bpmnconf.confentity.BpmnConf;
import org.openoa.base.entity.Department;
import org.openoa.engine.bpmnconf.mapper.BpmProcessDeptMapper;
import org.openoa.engine.bpmnconf.mapper.DepartmentMapper;
import org.openoa.engine.bpmnconf.service.biz.BpmProcessNameServiceImpl;
import org.openoa.engine.bpmnconf.service.biz.BpmnConfCommonServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.openoa.base.constant.enums.FilterDataEnum.FD_PROCESS_CONF;
import static org.openoa.base.constant.enums.ProcessJurisdictionEnum.CREATE_TYPE;

/**
 * //todo process and department connection service,the module is deprecated and will be redesigned
 */
@Service
public class BpmProcessDeptServiceImpl extends ServiceImpl<BpmProcessDeptMapper, BpmProcessDept> implements BpmProcessDeptService {

    private static Map<String, BpmnConf> bpmnConfMap = new ConcurrentHashMap<>();


    @Autowired
    private BpmProcessDeptMapper bpmProcessDeptMapper;
    @Autowired
    private BpmProcessNodeOvertimeServiceImpl processNodeOvertimeService;
    @Autowired
    private BpmProcessNoticeServiceImpl processNoticeService;

    @Autowired
    private BpmnEmployeeInfoProviderService employeeInfoProviderService;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private BpmnConfCommonServiceImpl confCommonService;
    @Autowired
    private BpmnConfServiceImpl bpmnConfService;
    @Autowired
    private BpmProcessNameServiceImpl bpmProcessNameService;
    @Autowired
    private BpmProcessPermissionServiceImpl permissionsService;
    @Autowired
    private  DepartmentServiceImpl departmentService;
    @Autowired
    private DepartmentMapper departmentMapper;
    /**
     * 查找最大流程编号
     *
     * @return
     */
    public String maxProcessCode() {
        return bpmProcessDeptMapper.maxProcessCode();
    }

    /**
     * 生成流程编号
     *
     * @param testStr
     * @return
     */
    public String getProcessCode(String testStr) {
        String[] strs = testStr.split("[^0-9]");//根据不是数字的字符拆分字符串
        String numStr = strs[strs.length - 1];//取出最后一组数字
        if (numStr != null && !numStr.isEmpty()) {//如果最后一组没有数字(也就是不以数字结尾)，抛NumberFormatException异常
            int n = numStr.length();//取出字符串的长度
            int num = Integer.parseInt(numStr) + 1;//将该数字加一
            String added = String.valueOf(num);
            n = Math.min(n, added.length());
            //拼接字符串
            return testStr.subSequence(0, testStr.length() - n) + added;
        } else {
            throw new NumberFormatException();
        }
    }
    /**
     * 查询全员创建权限
     */
    public List<String> getAllProcess() {
        QueryWrapper<BpmProcessDept> wrapper = new QueryWrapper<>();
        wrapper.eq("is_all", 1);
        List<BpmProcessDept> deptList = bpmProcessDeptMapper.selectList(wrapper);
        return Optional.ofNullable(deptList)
                .map(o ->
                        deptList.stream().map(BpmProcessDept::getProcessKey).collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());
    }

    /**
     * 根据编号修改权限关联
     *
     * @param bpmnConf
     * @return
     */
    public void editRelevance(BpmnConf bpmnConf) {
        bpmProcessNameService.editProcessName(bpmnConf);
    }
    public List<String> findProcessKey() {
        List<String> processKeyList = Optional.ofNullable(permissionsService.getProcessKey(SecurityUtils.getLogInEmpIdSafe(), CREATE_TYPE.getCode())).orElse(Arrays.asList());
        List<BpmnConf> confList = Optional.ofNullable(confCommonService.getIsAllConfs()).orElse(Arrays.asList());
        List<String> collect = confList.stream().map(BpmnConf::getFormCode).collect(Collectors.toList());
        List<String> processList = Optional.ofNullable(this.getAllProcess()).orElse(Arrays.asList());
        processList.addAll(processKeyList);
        processList.addAll(collect);
        return processList;
    }


    public void editProcessConf(BpmProcessDeptVo vo) throws JiMuBizException {
        QueryWrapper<BpmnConf> queryWrapper = new QueryWrapper<BpmnConf>().eq("is_del", 0).eq("effective_status", 1).eq("app_id", vo.getIconId());
        long count = bpmnConfService.count(queryWrapper);
        if (count > 0) {
            throw new JiMuBizException("应用已经关联已发布流程，请修改应用！");
        }
        confCommonService.updateBpmnConfByCode(vo.getIconId(), vo.getProcessType(), vo.getIsAll(), vo.getProcessKey());
        vo.setProcessKey(vo.getProcessCode());
        //保存权限
        permissionsService.saveProcessPermissions(vo);
        //保存通知类型
        processNoticeService.saveProcessNotice(vo.getProcessCode(), vo.getNotifyTypeIds());
    }
    /**
     * 分页查询流程配置列表
     *
     * @param vo
     * @return
     */
    public ResultAndPage<BpmProcessDeptVo> listPage(BpmProcessDeptVo vo) {
        //排序字段链表
        LinkedHashMap<String, SortTypeEnum> orderFieldMap = new LinkedHashMap<>();
        PageDto pageDto = PageUtils.getPageDtoByVo(vo);
        //1、使用MyBatis-Plus原生分页插件查询
        Page<BpmProcessDeptVo> page = PageUtils.getPageByPageDto(pageDto, orderFieldMap);
        page.setRecords(bpmProcessDeptMapper.listConfigure(page, vo));
        return PageUtils.getResultAndPage(page, FD_PROCESS_CONF);
    }

    @Override
    public List<BpmProcessDeptVo> allConfigure(BpmProcessDeptVo vo) {
        return bpmProcessDeptMapper.allConfigure(vo);
    }
    /**
     * 保存流程配置
     *
     * @param vo
     * @return
     */
    public boolean editProcess(BpmProcessDeptVo vo) throws JiMuBizException {
        BpmProcessDept bpmProcessDept = new BpmProcessDept();
        BeanUtils.copyProperties(vo,bpmProcessDept);
        //保存权限
        permissionsService.saveProcessPermissions(vo);
        //保存通知类型
        processNoticeService.saveProcessNotice(vo.getProcessKey(), vo.getNotifyTypeIds());
        //保存超时提醒
        processNodeOvertimeService.saveNodeOvertime(vo);
        bpmProcessDeptMapper.updateById(bpmProcessDept);
        return true;
    }

    /**
     * 根据id查询配置信息
     */
    public BpmProcessDeptVo findBpmProcessDeptById(Integer id) {
        BpmProcessDept bpmProcessDept = Optional.ofNullable(this.getById(id)).orElseThrow(() -> {
            return new JiMuBizException("未能根据指定id查询到流程配置信息");
        });
        BpmProcessDeptVo vo = BpmProcessDeptVo.builder()
                .id(bpmProcessDept.getId())
                .processKey(bpmProcessDept.getProcessKey())
                .createTime(bpmProcessDept.getCreateTime())
                .processName(bpmProcessDept.getProcessName())
                .processCode(bpmProcessDept.getProcessCode())
                .deptId(Long.parseLong(bpmProcessDept.getDeptId()))
                .remarks(bpmProcessDept.getRemarks())
                .isAll(bpmProcessDept.getIsAll())
                .processType(bpmProcessDept.getProcessType())
                .deptName(departmentService.getById(bpmProcessDept.getId()).getName())
                .build();
        //用户查看权限
        List<BaseIdTranStruVo> viewUserList = new ArrayList<>();
        List<String> viewUserIds = new ArrayList<>();
        List<BpmProcessPermission> viewUserProcessList = permissionsService.permissionsList(vo.getProcessKey(), ProcessJurisdictionEnum.VIEW_TYPE.getCode(), true);
        if (!CollectionUtils.isEmpty(viewUserProcessList)) {
            viewUserProcessList.forEach(o -> {
                viewUserIds.add(o.getUserId());
                viewUserList.add(BaseIdTranStruVo.builder()
                        .id(o.getUserId())
                        .name(employeeInfoProviderService.provideEmployeeInfo(Lists.newArrayList(o.getUserId())).get(o.getUserId()))
                        .build());
            });
            vo.setViewUserIds(viewUserIds);
            vo.setViewUserList(viewUserList);
        }
        //部门查看权限
        List<BaseIdTranStruVo> viewDeptList = new ArrayList<>();
        List<Long> viewDeptIds = new ArrayList<>();
        List<BpmProcessPermission> viewDeptProcessList = permissionsService.permissionsList(vo.getProcessKey(), ProcessJurisdictionEnum.VIEW_TYPE.getCode(), false);
        if (!CollectionUtils.isEmpty(viewDeptProcessList)) {
            viewDeptProcessList.stream().forEach(o -> {
                viewDeptIds.add(Long.parseLong(o.getDepId()));
                viewDeptList.add(BaseIdTranStruVo.builder()
                        .id(o.getDepId())
                        .name(Optional.ofNullable(departmentService.getById(o.getDepId())).orElse(new Department()).getName())
                        .build());
            });
            vo.setViewdeptIds(viewDeptIds);
            vo.setViewdeptList(viewDeptList);
        }

        //用户创建权限
        List<BaseIdTranStruVo> createUserList = new ArrayList<>();
        List<String> createUserIds = new ArrayList<>();
        List<BpmProcessPermission> createUserProcessList = permissionsService.permissionsList(vo.getProcessKey(), CREATE_TYPE.getCode(), true);
        if (!CollectionUtils.isEmpty(createUserProcessList)) {
            createUserProcessList.forEach(o -> {
                createUserIds.add(o.getUserId());
                createUserList.add(BaseIdTranStruVo.builder()
                        .id(o.getUserId())
                        .name(employeeInfoProviderService.provideEmployeeInfo(Lists.newArrayList(o.getUserId())).get(o.getUserId()))
                        .build());
            });
            vo.setCreateUserIds(createUserIds);
            vo.setCreateUserList(createUserList);
        }

        //部门创建权限
        List<BaseIdTranStruVo> createDeptList = new ArrayList<>();
        List<Long> createDeptIds = new ArrayList<>();
        List<BpmProcessPermission> createDeptProcessList = permissionsService.permissionsList(vo.getProcessKey(), CREATE_TYPE.getCode(), false);
        if (!CollectionUtils.isEmpty(createDeptProcessList)) {
            createDeptProcessList.forEach(o -> {
                createDeptIds.add(Long.parseLong(o.getDepId()));
                createDeptList.add(BaseIdTranStruVo.builder()
                        .id(o.getDepId())
                        .name(Optional.ofNullable(departmentService.getById(o.getDepId())).orElse(new Department()).getName())
                        .build());
            });
            vo.setCreateDeptIds(createDeptIds);
            vo.setCreateDeptList(createDeptList);
        }
        return vo;
    }
    /**
     * PC流程申请入口
     */
    public List<BpmProcessDept> viewBpmProcessDeptVo() {
        List<String> processKeyList = Optional.ofNullable(permissionsService.getProcessKey(SecurityUtils.getLogInEmpId(), CREATE_TYPE.getCode())).orElse(Collections.emptyList());
        List<String> processList = new ArrayList<>(Optional.ofNullable(this.getAllProcess()).orElse(Collections.emptyList()));
        processList.addAll(processKeyList);
        return this.getBpmProcessDept(processList);
    }
    /***
     * 根据流程key查询
     * @param processKeyList
     * @return
     */
    public List<BpmProcessDept> getBpmProcessDept(List<String> processKeyList) {
        QueryWrapper<BpmProcessDept> wrapper = new QueryWrapper<>();
        wrapper.in("process_key", processKeyList);
        return bpmProcessDeptMapper.selectList(wrapper);
    }
    /**
     *查看流程配置数据
     */
    public BpmProcessDeptVo getBpmnConf(Integer bpmnConfId) {
        BpmnConf bpmnConfById = confCommonService.getBpmnConfById(bpmnConfId);
        BpmProcessDeptVo vo = BpmProcessDeptVo.builder()
                .processKey(bpmnConfById.getBpmnCode())
                .createTime(bpmnConfById.getCreateTime())
                .processName(bpmnConfById.getBpmnName())
                .processCode(bpmnConfById.getFormCode())
                .isAll(bpmnConfById.getIsAll())
                .iconId(bpmnConfById.getAppId())
                .processType(bpmnConfById.getBpmnType())
                .build();
        //员工查看权限
        List<BpmProcessPermission> viewUserProcessList = Optional.ofNullable(permissionsService.permissionsList(bpmnConfById.getFormCode(), ProcessJurisdictionEnum.VIEW_TYPE.getCode(), true)).orElse(Collections.emptyList());
        vo.setViewUserList(viewUserProcessList.stream().map(o ->
                BaseIdTranStruVo.builder()
                        .id(o.getUserId())
                        .name(employeeInfoProviderService.provideEmployeeInfo(Lists.newArrayList(o.getUserId())).get(o.getUserId()))
                        .build()
        ).collect(Collectors.toList()));
        vo.setViewUserIds(viewUserProcessList.stream().map(BpmProcessPermission::getUserId).collect(Collectors.toList()));
        //部门查看权限
        List<BpmProcessPermission> viewDeptProcessList = Optional.ofNullable(permissionsService.permissionsList(bpmnConfById.getFormCode(), ProcessJurisdictionEnum.VIEW_TYPE.getCode(), false)).orElse(Arrays.asList());
        vo.setViewdeptList(viewDeptProcessList.stream().map(o -> BaseIdTranStruVo.builder()
                .id(o.getDepId())
                .name(Optional.ofNullable(departmentMapper.selectById(o.getDepId())).orElse(new Department()).getName())
                .build()
        ).collect(Collectors.toList()));
        vo.setViewdeptIds(viewDeptProcessList.stream().map(a->Long.parseLong(a.getDepId())).collect(Collectors.toList()));

        //员工创建权限
        List<BpmProcessPermission> createUserProcessList = Optional.ofNullable(permissionsService.permissionsList(bpmnConfById.getFormCode(), CREATE_TYPE.getCode(), true)).orElse(Arrays.asList());
        vo.setCreateUserList(createUserProcessList.stream().map(o -> BaseIdTranStruVo.builder()
                .id(o.getUserId())
                .name(employeeInfoProviderService.provideEmployeeInfo(Lists.newArrayList(o.getUserId())).get(o.getUserId()))
                .build()
        ).collect(Collectors.toList()));
        vo.setCreateUserIds(createUserProcessList.stream().map(BpmProcessPermission::getUserId).collect(Collectors.toList()));

        //部门创建权限
        List<BpmProcessPermission> createDeptProcessList = Optional.ofNullable(permissionsService.permissionsList(bpmnConfById.getFormCode(), CREATE_TYPE.getCode(), false)).orElse(Arrays.asList());
        vo.setCreateDeptList(createDeptProcessList.stream().map(o -> BaseIdTranStruVo.builder()
                .id(o.getDepId())
                .name(departmentMapper.selectById(o.getDepId()).getName())
                .build()
        ).collect(Collectors.toList()));
        vo.setCreateDeptIds(createDeptProcessList.stream().map(a->Long.parseLong(a.getDepId())).collect(Collectors.toList()));

        //员工监控权限
        List<BpmProcessPermission> controlUserProcessList = Optional.ofNullable(permissionsService.permissionsList(bpmnConfById.getFormCode(), ProcessJurisdictionEnum.CONTROL_TYPE.getCode(), true)).orElse(Arrays.asList());
        vo.setControlUserIdList(controlUserProcessList.stream().map(o -> BaseIdTranStruVo.builder()
                .id(o.getUserId())
                .name(employeeInfoProviderService.provideEmployeeInfo(Lists.newArrayList(o.getUserId())).get(o.getUserId()))
                .build()
        ).collect(Collectors.toList()));
        vo.setControlUserIds(controlUserProcessList.stream().map(BpmProcessPermission::getUserId).collect(Collectors.toList()));

        //部门监控权限
        List<BpmProcessPermission> controlDeptProcessList = Optional.ofNullable(permissionsService.permissionsList(bpmnConfById.getFormCode(), ProcessJurisdictionEnum.CONTROL_TYPE.getCode(), false)).orElse(Arrays.asList());
        vo.setControlDeptIdList(controlDeptProcessList.stream().map(o -> BaseIdTranStruVo.builder()
                .id(o.getDepId())
                .name(departmentMapper.selectById(o.getDepId()).getName())
                .build()
        ).collect(Collectors.toList()));
        vo.setControlDeptIds(controlDeptProcessList.stream().map(a->Long.parseLong(a.getDepId())).collect(Collectors.toList()));
        //通知方式查询
        List<BpmProcessNotice> processNoticeProcessList = Optional.ofNullable(processNoticeService.processNoticeList(bpmnConfById.getFormCode())).orElse(Arrays.asList());
        vo.setNotifyTypeList(processNoticeProcessList.stream().map(o ->
                BaseIdTranStruVo.builder()
                        .id(o.getType().toString())
                        .name(ProcessNoticeEnum.getDescByCode(o.getType()))
                        .build()
        ).collect(Collectors.toList()));

        vo.setNotifyTypeIds(processNoticeProcessList.stream().map(BpmProcessNotice::getType).collect(Collectors.toList()));
        return vo;
    }

}
