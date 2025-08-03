package org.openoa.engine.bpmnconf.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.constant.enums.AdminPersonnelTypeEnum;
import org.openoa.base.constant.enums.BusinessPartyTypeEnum;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.Employee;
import org.openoa.base.entity.OutSideBpmAdminPersonnel;
import org.openoa.base.entity.OutSideBpmBusinessParty;
import org.openoa.base.entity.OutSideBpmCallbackUrlConf;
import org.openoa.base.service.AfUserService;
import org.openoa.base.util.PageUtils;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmAdminPersonnelServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmBusinessPartyServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.biz.OutSideBpmCallbackUrlConfBizService;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.openoa.engine.vo.OutSideBpmCallbackUrlConfVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OutSideBpmCallbackUrlConfBizServiceImpl implements OutSideBpmCallbackUrlConfBizService {


    @Autowired
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;

    @Autowired
    private OutSideBpmAdminPersonnelServiceImpl outSideBpmAdminPersonnelService;

    @Autowired
    private OutSideBpmBaseServiceImpl outSideBpmBaseService;

    @Autowired
    private AfUserService employeeService;

    /**
     * query by page
     *
     * @param pageDto
     * @param vo
     * @return
     */
    @Override
    public ResultAndPage<OutSideBpmCallbackUrlConfVo> listPage(PageDto pageDto, OutSideBpmCallbackUrlConfVo vo) {

        Page<OutSideBpmCallbackUrlConfVo> page = PageUtils.getPageByPageDto(pageDto);


        //query login user business party list,control access right
        List<OutSideBpmBusinessPartyVo> emplBusinessPartys = outSideBpmBaseService.getEmplBusinessPartys(StringUtils.EMPTY, AdminPersonnelTypeEnum.ADMIN_PERSONNEL_TYPE_INTERFACE.getPermCode());
        vo.setBusinessPartyIds(emplBusinessPartys.stream().map(OutSideBpmBusinessPartyVo::getId).collect(Collectors.toList()));

        //querying result
        List<OutSideBpmCallbackUrlConfVo> records = getMapper().selectPageList(page, vo);


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
    @Override
    public OutSideBpmCallbackUrlConfVo detail(Integer id) {
        OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = this.getMapper().selectById(id);

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

}
