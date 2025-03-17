package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.AdminPersonnelTypeEnum;
import org.openoa.base.constant.enums.BusinessPartyTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.util.PageUtils;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.entity.Employee;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmAdminPersonnel;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmBusinessParty;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmCallbackUrlConf;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmCallbackUrlConfMapper;
import org.openoa.engine.vo.GenericEmployee;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.openoa.engine.vo.OutSideBpmCallbackUrlConfVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



/**
 * third party process service callback url conf
 * @since 0.5
 */
@Service
public class OutSideBpmCallbackUrlConfServiceImpl extends ServiceImpl<OutSideBpmCallbackUrlConfMapper, OutSideBpmCallbackUrlConf> {

    @Autowired
    private OutSideBpmCallbackUrlConfMapper outSideBpmCallbackUrlConfMapper;

    @Autowired
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;

    @Autowired
    private OutSideBpmAdminPersonnelServiceImpl outSideBpmAdminPersonnelService;

    @Autowired
    private OutSideBpmBaseServiceImpl outSideBpmBaseService;

    @Autowired
    private EmployeeServiceImpl employeeService;

    /**
     * query by page
     *
     * @param pageDto
     * @param vo
     * @return
     */
    public ResultAndPage<OutSideBpmCallbackUrlConfVo> listPage(PageDto pageDto, OutSideBpmCallbackUrlConfVo vo) {

        Page<OutSideBpmCallbackUrlConfVo> page = PageUtils.getPageByPageDto(pageDto);


        //query login user business party list,control access right
        List<OutSideBpmBusinessPartyVo> emplBusinessPartys = outSideBpmBaseService.getEmplBusinessPartys(StringUtils.EMPTY, AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_INTERFACE.getPermCode());
        vo.setBusinessPartyIds(emplBusinessPartys.stream().map(OutSideBpmBusinessPartyVo::getId).collect(Collectors.toList()));

        //querying result
        List<OutSideBpmCallbackUrlConfVo> records = outSideBpmCallbackUrlConfMapper.selectPageList(page, vo);


        //if the querying result is empty,then return empty page list;
        if (CollectionUtils.isEmpty(records)) {
            return PageUtils.getResultAndPage(page);
        }

        List<Long> businessPartyIds = records
                .stream()
                .map(OutSideBpmCallbackUrlConfVo::getBusinessPartyId)
                .distinct()
                .collect(Collectors.toList());


        //query business party's info,for assemble the result
        Map<Long, OutSideBpmBusinessParty> bpmBusinessPartyMap = outSideBpmBusinessPartyService.getBaseMapper().selectBatchIds(businessPartyIds)
                .stream()
                .collect(Collectors.toMap(OutSideBpmBusinessParty::getId, o -> o));

        page.setRecords(records
                .stream()
                .map(o -> reBuildVo(o, bpmBusinessPartyMap.get(o.getBusinessPartyId())))
                .collect(Collectors.toList()));

        return PageUtils.getResultAndPage(page);
    }

    /**
     * query detial by id
     *
     * @param id
     * @return
     */
    public OutSideBpmCallbackUrlConfVo detail(Integer id) {
        OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = this.getBaseMapper().selectById(id);

        OutSideBpmCallbackUrlConfVo vo = new OutSideBpmCallbackUrlConfVo();

        BeanUtils.copyProperties(outSideBpmCallbackUrlConf, vo);


        //query business party's info,for assemble the result
        OutSideBpmBusinessParty outSideBpmBusinessParty = outSideBpmBusinessPartyService.getBaseMapper().selectById(vo.getBusinessPartyId());

        //rebuild the vo to give it detailed information for representing
        return reBuildVo(vo, outSideBpmBusinessParty);
    }

    /**
     * rebuild the vo
     *
     * @param outSideBpmCallbackUrlConfVo
     */
    private OutSideBpmCallbackUrlConfVo reBuildVo(OutSideBpmCallbackUrlConfVo outSideBpmCallbackUrlConfVo, OutSideBpmBusinessParty outSideBpmBusinessParty) {

        //map status
        if (outSideBpmCallbackUrlConfVo.getStatus()!=null) {
            if (outSideBpmCallbackUrlConfVo.getStatus() == 1) {
                outSideBpmCallbackUrlConfVo.setStatusName("启用");
            } else if (outSideBpmCallbackUrlConfVo.getStatus() == 2) {
                outSideBpmCallbackUrlConfVo.setStatusName("封存");
            }
        }

        //map business party's info
        if (outSideBpmBusinessParty!=null) {
            outSideBpmCallbackUrlConfVo.setBusinessPartyName(outSideBpmBusinessParty.getName());
            outSideBpmCallbackUrlConfVo.setAccessType(outSideBpmBusinessParty.getType());
            outSideBpmCallbackUrlConfVo.setAccessTypeName(BusinessPartyTypeEnum.getDescByCode(outSideBpmBusinessParty.getType()));

            List<OutSideBpmAdminPersonnel> outSideBpmAdminPersonnels = outSideBpmAdminPersonnelService.getBaseMapper().selectList(new QueryWrapper<OutSideBpmAdminPersonnel>()
                    .eq("business_party_id", outSideBpmBusinessParty.getId())
                    .eq("type", AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_INTERFACE.getCode()));

            if (!CollectionUtils.isEmpty(outSideBpmAdminPersonnels)) {

                List<Employee> employees = employeeService.getEmployeeDetailByIds(outSideBpmAdminPersonnels
                        .stream()
                        .map(OutSideBpmAdminPersonnel::getEmployeeId)
                        .collect(Collectors.toList()));

                outSideBpmCallbackUrlConfVo.setInterfaceAdmins(employees.stream().map(o -> Employee
                        .builder()
                        .id(o.getId())
                        .username(o.getUsername())
                        .build())
                        .collect(Collectors.toList()));
            }

        }

        return outSideBpmCallbackUrlConfVo;
    }

    /**
     * edit
     *
     * @param vo
     */
    public void edit(OutSideBpmCallbackUrlConfVo vo) {


        //to check whether the business party can add data
        if (vo.getId()==null) {
            Long count = this.getBaseMapper().selectCount(new QueryWrapper<OutSideBpmCallbackUrlConf>()
                    .eq("business_party_id", vo.getBusinessPartyId()));

            if (count > 0) {
                throw new JiMuBizException("一个业务方只能配置一条接口回调数据");
            }
        }

        OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = this.getBaseMapper().selectById(vo.getId());
        if (outSideBpmCallbackUrlConf!=null) {
            BeanUtils.copyProperties(vo, outSideBpmCallbackUrlConf);
            outSideBpmCallbackUrlConf.setUpdateTime(new Date());
            outSideBpmCallbackUrlConf.setUpdateUser(SecurityUtils.getLogInEmpName());
            this.updateById(outSideBpmCallbackUrlConf);
        } else {
            outSideBpmCallbackUrlConf = new OutSideBpmCallbackUrlConf();
            BeanUtils.copyProperties(vo, outSideBpmCallbackUrlConf);
            GenericEmployee loginedEmployee = new GenericEmployee();
            //todo
            outSideBpmCallbackUrlConf.setStatus(1);
            outSideBpmCallbackUrlConf.setCreateTime(new Date());
            outSideBpmCallbackUrlConf.setCreateUser(SecurityUtils.getLogInEmpName());
            outSideBpmCallbackUrlConf.setUpdateTime(new Date());
            outSideBpmCallbackUrlConf.setUpdateUser(SecurityUtils.getLogInEmpName());
            this.save(outSideBpmCallbackUrlConf);
        }
    }

    /**
     * query call back conf by business party id
     *
     * @param bpmnConfId
     * @param businessPartyId
     * @return
     */
    public OutSideBpmCallbackUrlConf getOutSideBpmCallbackUrlConf(Long bpmnConfId, Long businessPartyId) {

        OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = this.getBaseMapper().selectOne(new QueryWrapper<OutSideBpmCallbackUrlConf>()
                .eq("business_party_id", businessPartyId)
                .eq("status", 1));

//        if (outSideBpmCallbackUrlConf==null) {
//            throw new JiMuBizException("流程回调URL未配置，方法执行失败");
//        }

        return outSideBpmCallbackUrlConf;
    }

}
