package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jodd.bean.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.AdminPersonnelTypeEnum;
import org.openoa.base.constant.enums.BusinessPartyTypeEnum;
import org.openoa.base.constant.enums.NodePropertyEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.entity.Employee;
import org.openoa.engine.bpmnconf.confentity.*;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmBusinessPartyMapper;
import org.openoa.engine.vo.NodeRolePersonVo;
import org.openoa.engine.vo.OutSideBpmApplicationVo;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * third party process service-business party config
 *
 * @since 0.5
 */
@Service
public class OutSideBpmBusinessPartyServiceImpl extends ServiceImpl<OutSideBpmBusinessPartyMapper, OutSideBpmBusinessParty> {

    @Autowired
    private OutSideBpmBusinessPartyMapper outSideBpmBusinessPartyMapper;

    @Autowired
    private OutSideBpmAdminPersonnelServiceImpl outSideBpmAdminPersonnelService;

    @Autowired
    private OutSideBpmCallbackUrlConfServiceImpl outSideBpmCallbackUrlConfService;

    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private BpmProcessAppApplicationServiceImpl bpmProcessAppApplicationService;

    @Autowired
    private BpmProcessAppDataServiceImpl bpmProcessAppDataServiceImpl;

    @Autowired
    private BpmnConfServiceImpl bpmnConfService;

    @Autowired
    private BpmnNodeServiceImpl bpmnNodeService;

    @Autowired
    private BpmnNodeRoleConfServiceImpl bpmnNodeRoleConfServiceImpl;

    @Autowired
    private BpmnNodeRoleOutsideEmpConfServiceImpl bpmnNodeRoleOutsideEmpConfService;
    /**
     * querying business's info by page
     *
     * @param pageDto
     * @param vo
     * @return
     */
    public ResultAndPage<OutSideBpmBusinessPartyVo> listPage(PageDto pageDto, OutSideBpmBusinessPartyVo vo) {

        Page<OutSideBpmBusinessPartyVo> page = PageUtils.getPageByPageDto(pageDto);

        //querying result
        List<OutSideBpmBusinessPartyVo> records = outSideBpmBusinessPartyMapper.selectPageList(page, vo);

        //if the records are empty then return an empty page list;
        if (CollectionUtils.isEmpty(records)) {
            return PageUtils.getResultAndPage(page);
        }


        //querying all associated business party admin
        List<OutSideBpmAdminPersonnel> outSideBpmAdminPersonnels = outSideBpmAdminPersonnelService.getBaseMapper().selectList(new QueryWrapper<OutSideBpmAdminPersonnel>()
                .in("business_party_id", records
                        .stream()
                        .map(OutSideBpmBusinessPartyVo::getId)
                        .distinct()
                        .collect(Collectors.toList())));


        //if the outSideBpmAdminPersonnels is empty then return the result;
        if (CollectionUtils.isEmpty(outSideBpmAdminPersonnels)) {
            return PageUtils.getResultAndPage(page.setRecords(records));
        }

        //set result
        page.setRecords(records
                .stream()
                .map(o -> reBuildVo(o, outSideBpmAdminPersonnels, false))
                .collect(Collectors.toList()));

        return PageUtils.getResultAndPage(page);
    }

    /**
     * query detail info by business party 's id
     *
     * @param id
     * @return
     */
    public OutSideBpmBusinessPartyVo detail(Integer id) {

        OutSideBpmBusinessParty outSideBpmBusinessParty = this.getBaseMapper().selectById(id);

        OutSideBpmBusinessPartyVo vo = new OutSideBpmBusinessPartyVo();

        BeanUtils.copyProperties(outSideBpmBusinessParty, vo);


        //querying all associated business party admin
        List<OutSideBpmAdminPersonnel> outSideBpmAdminPersonnels = outSideBpmAdminPersonnelService.getBaseMapper().selectList(new QueryWrapper<OutSideBpmAdminPersonnel>()
                .eq("business_party_id", outSideBpmBusinessParty.getId()));


        //if the result is empty then return
        if (CollectionUtils.isEmpty(outSideBpmAdminPersonnels)) {
            return vo;
        }

        return reBuildVo(vo, outSideBpmAdminPersonnels, true);

    }

    /**
     * rebuild vo for representing more detailed information
     *
     * @param outSideBpmBusinessPartyVo
     * @param outSideBpmAdminPersonnels
     * @param isDetail
     */
    private OutSideBpmBusinessPartyVo reBuildVo(OutSideBpmBusinessPartyVo outSideBpmBusinessPartyVo, List<OutSideBpmAdminPersonnel> outSideBpmAdminPersonnels, Boolean isDetail) {

        //map bysiness party's type
        if (outSideBpmBusinessPartyVo.getType() != null) {
            outSideBpmBusinessPartyVo.setTypeName(BusinessPartyTypeEnum.getDescByCode(outSideBpmBusinessPartyVo.getType()));
        }

        //get emp list
        Map<String, Employee> employeeMap = employeeService.getEmployeeDetailByIds(outSideBpmAdminPersonnels
                        .stream()
                        .map(OutSideBpmAdminPersonnel::getEmployeeId)
                        .distinct()
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Employee::getId, o -> o));


        List<OutSideBpmAdminPersonnel> adminPersonnels = outSideBpmAdminPersonnels
                .stream()
                .filter(o -> o.getBusinessPartyId().equals(outSideBpmBusinessPartyVo.getId()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(adminPersonnels)) {
            return outSideBpmBusinessPartyVo;
        }

        for (AdminPersonnelTypeEnum personnelTypeEnum : AdminPersonnelTypeEnum.values()) {
            List<OutSideBpmAdminPersonnel> bpmAdminPersonnels = adminPersonnels
                    .stream()
                    .filter(o -> o.getType().equals(personnelTypeEnum.getCode()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(bpmAdminPersonnels)) {
                continue;
            }

            if (isDetail) {
                //admin list
                List<BaseIdTranStruVo> personnelsList = bpmAdminPersonnels.stream().map(o -> BaseIdTranStruVo
                                .builder()
                                .id(o.getEmployeeId())
                                .name(!StringUtils.isEmpty(o.getEmployeeName())?o.getEmployeeName():Optional.ofNullable(employeeMap.get(o.getEmployeeId()))
                                        .orElse(new Employee()).getUsername())
                                .build())
                        .collect(Collectors.toList());
                BeanUtil.pojo.setProperty(outSideBpmBusinessPartyVo, personnelTypeEnum.getListField(), personnelsList);

                //admin's id list
                List<String> idsList = bpmAdminPersonnels
                        .stream()
                        .map(OutSideBpmAdminPersonnel::getEmployeeId)
                        .distinct()
                        .collect(Collectors.toList());
                BeanUtil.pojo.setProperty(outSideBpmBusinessPartyVo, personnelTypeEnum.getIdsField(), idsList);

            } else {
                //format name
                String personnelsStr = StringUtils.join(bpmAdminPersonnels
                        .stream()
                        .map(o -> Optional.ofNullable(employeeMap.get(o.getEmployeeId()))
                                .orElse(new Employee()).getUsername())
                        .collect(Collectors.toList()), ",");
                BeanUtil.pojo.setProperty(outSideBpmBusinessPartyVo, personnelTypeEnum.getStrField(), personnelsStr);
            }

        }

        return outSideBpmBusinessPartyVo;
    }

    /**
     * querying business party mark by id
     *
     * @param id
     * @return
     */
    public String getBusinessPartyMarkById(Integer id) {
        return outSideBpmBusinessPartyMapper.getBusinessPartyMarkById(id);
    }

    /**
     * edit
     *
     * @param vo
     */
    public void edit(OutSideBpmBusinessPartyVo vo) {

        //check whether the data is repeated
        if (outSideBpmBusinessPartyMapper.checkData(vo) > 0) {
            throw new JiMuBizException("业务方标识或业务方名称重复");
        }

        OutSideBpmBusinessParty outSideBpmBusinessParty = this.getBaseMapper().selectById(vo.getId());
        if (outSideBpmBusinessParty != null) {
            BeanUtils.copyProperties(vo, outSideBpmBusinessParty);
            outSideBpmBusinessParty.setUpdateTime(new Date());
            outSideBpmBusinessParty.setUpdateUser(SecurityUtils.getLogInEmpName());
            this.updateById(outSideBpmBusinessParty);
        } else {
            outSideBpmBusinessParty = new OutSideBpmBusinessParty();
            BeanUtils.copyProperties(vo, outSideBpmBusinessParty);
            outSideBpmBusinessParty.setIsDel(0);
            outSideBpmBusinessParty.setCreateTime(new Date());
            outSideBpmBusinessParty.setCreateUser(SecurityUtils.getLogInEmpName());
            outSideBpmBusinessParty.setUpdateTime(new Date());
            outSideBpmBusinessParty.setUpdateUser(SecurityUtils.getLogInEmpName());
            this.save(outSideBpmBusinessParty);
        }

        Long id = outSideBpmBusinessParty.getId();

        if (id != null && id > 0) {


            //delete related data
            outSideBpmAdminPersonnelService.getBaseMapper().delete(new QueryWrapper<OutSideBpmAdminPersonnel>()
                    .eq("business_party_id", id));


            //add records in batch
            for (AdminPersonnelTypeEnum typeEnum : AdminPersonnelTypeEnum.values()) {
                Object property = BeanUtil.pojo.getProperty(vo, typeEnum.getIdsField());
                if (property != null && property instanceof List) {
                    List<String> ids = (List<String>) property;
                    outSideBpmAdminPersonnelService.saveBatch(ids
                            .stream()
                            .map(o -> OutSideBpmAdminPersonnel
                                    .builder()
                                    .businessPartyId(id)
                                    .employeeId(o)
                                    .type(typeEnum.getCode())
                                    .createTime(new Date())
                                    .createUser(SecurityUtils.getLogInEmpName())
                                    .updateTime(new Date())
                                    .updateUser(SecurityUtils.getLogInEmpName())
                                    .build())
                            .collect(Collectors.toList()));
                }
            }


            //if the party has no call back conf info,then add one
            long count = outSideBpmCallbackUrlConfService.count(new QueryWrapper<OutSideBpmCallbackUrlConf>()
                    .eq("business_party_id", id));
            if (count == 0) {
                outSideBpmCallbackUrlConfService.getBaseMapper().insert(OutSideBpmCallbackUrlConf
                        .builder()
                        .businessPartyId(id)
                        .build());
            }
        }
    }

    public Long editApplication(OutSideBpmApplicationVo vo) {

        OutSideBpmBusinessParty outSideBpmBusinessParty = null;

        // step 1
        if (vo.getThirdCode() == null) {
            throw new JiMuBizException("第三方业务方标识不能为空");
        } else {
            outSideBpmBusinessParty = this.getOne(Wrappers.<OutSideBpmBusinessParty>lambdaQuery().eq(OutSideBpmBusinessParty::getBusinessPartyMark, vo.getThirdCode())
                    .eq(OutSideBpmBusinessParty::getIsDel, 0), false);
            if (outSideBpmBusinessParty == null) {
                outSideBpmBusinessParty = new OutSideBpmBusinessParty();
                outSideBpmBusinessParty.setBusinessPartyMark(vo.getThirdCode());
                outSideBpmBusinessParty.setType(2);
                outSideBpmBusinessParty.setName(vo.getThirdName());
                outSideBpmBusinessParty.setCreateTime(new Date());
                outSideBpmBusinessParty.setCreateUser(SecurityUtils.getLogInEmpIdSafe());
                outSideBpmBusinessParty.setUpdateTime(new Date());
                outSideBpmBusinessParty.setUpdateUser(SecurityUtils.getLogInEmpIdSafe());
                this.save(outSideBpmBusinessParty);
            }
        }

        Long id = vo.getThirdId() == null ? outSideBpmBusinessParty.getId() : vo.getThirdId();
        //if the party has no call back conf info,then add one
        long count = outSideBpmCallbackUrlConfService.count(new QueryWrapper<OutSideBpmCallbackUrlConf>()
                .eq("business_party_id", id));
        if (count == 0) {
            outSideBpmCallbackUrlConfService.getBaseMapper().insert(OutSideBpmCallbackUrlConf
                    .builder()
                    .businessPartyId(id)
                    .status(0)
                    .build());
        }


        // step 2
        BpmProcessAppApplication app = bpmProcessAppApplicationService.getOne(Wrappers.<BpmProcessAppApplication>lambdaQuery().eq(BpmProcessAppApplication::getBusinessCode, vo.getThirdCode())
                .eq(BpmProcessAppApplication::getProcessKey, vo.getProcessKey()), false);
        if (app == null) {
            app = BpmProcessAppApplication.builder()
                    .businessCode(vo.getThirdCode())
                    .processKey(vo.getProcessKey())
                    .title(vo.getProcessName())
                    .applyType(2)
                    .build();
            bpmProcessAppApplicationService.save(app);
        }

        // step 3
        BpmProcessAppData appData = bpmProcessAppDataServiceImpl.getOne(Wrappers.<BpmProcessAppData>lambdaQuery().eq(BpmProcessAppData::getApplicationId, app.getId())
                .eq(BpmProcessAppData::getProcessKey, vo.getProcessKey()), false);
        if (appData == null) {
            appData = BpmProcessAppData.builder()
                    .applicationId(app.getId().longValue())
                    .processKey(vo.getProcessKey())
                    .processName(vo.getProcessName())
                    .state(1)
                    .build();
            bpmProcessAppDataServiceImpl.save(appData);
        }


        return id;
    }

    /**
     * get bpm conf by active of list
     *
     * @param businessPartyMark
     * @return
     */
    public List<BpmnConfVo> getBpmConf(String businessPartyMark) {
        List<BpmnConfVo> bpmnConfVos = bpmnConfService.getBaseMapper().selectThirdBpmnConfList(BpmnConfVo.builder()
                .businessPartyMark(businessPartyMark).build());
        return bpmnConfVos;
    }


    public void syncRolePersonnel(String businessPartyMark, NodeRolePersonVo userList) {
        List<BpmnConfVo> bpmConf = getBpmConf(businessPartyMark);
        if (StringUtils.isBlank(userList.getRoleId())) {
            throw new JiMuBizException("500", "角色id不能为空");
        }

        List<BaseIdTranStruVo> users = userList.getUserList();
        if (CollectionUtils.isEmpty(users)) {
            throw new JiMuBizException("500", "角色人员列表不能为空");
        }

        for (BpmnConfVo bpmnConfVo : bpmConf) {

            //step 1 get  node by role type
            List<BpmnNode> bpmnNodes = bpmnNodeService.list(Wrappers.<BpmnNode>lambdaQuery().eq(BpmnNode::getConfId, bpmnConfVo.getId())
                    .eq(BpmnNode::getNodeProperty, NodePropertyEnum.NODE_PROPERTY_ROLE.getCode()));
            for (BpmnNode bpmnNode : bpmnNodes) {

                //step 2 get role list
                List<BpmnNodeRoleConf> nodeRoleList = bpmnNodeRoleConfServiceImpl.list(Wrappers.<BpmnNodeRoleConf>lambdaQuery()
                        .eq(BpmnNodeRoleConf::getBpmnNodeId, bpmnNode.getId()).eq(BpmnNodeRoleConf::getIsDel, 0));
                for (BpmnNodeRoleConf bpmnNodeRoleConf : nodeRoleList) {

                    //step 3 update role user list
                    String roleId = bpmnNodeRoleConf.getRoleId();
                    if (roleId.equals(userList.getRoleId())) {
                        bpmnNodeRoleOutsideEmpConfService.update(Wrappers.<BpmnNodeRoleOutsideEmpConf>lambdaUpdate().set(BpmnNodeRoleOutsideEmpConf::getIsDel, 1)
                                .set(BpmnNodeRoleOutsideEmpConf::getUpdateTime, new Date())
                                .set(BpmnNodeRoleOutsideEmpConf::getUpdateUser, SecurityUtils.getLogInEmpIdSafe())
                                .eq(BpmnNodeRoleOutsideEmpConf::getNodeId, bpmnNode.getId()));

                        // step 4 add  role user list
                        List<BpmnNodeRoleOutsideEmpConf> newPersonnelList = new ArrayList<>();
                        for (BaseIdTranStruVo user : users) {
                            BpmnNodeRoleOutsideEmpConf outsideEmpConf = getRoleOutsideEmpConf(bpmnNode, user);
                            newPersonnelList.add(outsideEmpConf);
                        }
                        bpmnNodeRoleOutsideEmpConfService.saveBatch(newPersonnelList);
                    }

                }

            }
        }
    }

    private  BpmnNodeRoleOutsideEmpConf getRoleOutsideEmpConf(BpmnNode bpmnNode, BaseIdTranStruVo user) {
        BpmnNodeRoleOutsideEmpConf outsideEmpConf = new BpmnNodeRoleOutsideEmpConf();
        outsideEmpConf.setNodeId(bpmnNode.getId());
        outsideEmpConf.setEmplId(user.getId());
        outsideEmpConf.setEmplName(user.getName());
        outsideEmpConf.setCreateUser(SecurityUtils.getLogInEmpIdSafe());
        outsideEmpConf.setUpdateUser(SecurityUtils.getLogInEmpIdSafe());
        outsideEmpConf.setCreateTime(new Date());
        outsideEmpConf.setUpdateTime(new Date());
        outsideEmpConf.setIsDel(0);
        return outsideEmpConf;
    }
}
