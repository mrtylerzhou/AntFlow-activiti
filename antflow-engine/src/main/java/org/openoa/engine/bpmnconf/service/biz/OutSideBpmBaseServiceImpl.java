package org.openoa.engine.bpmnconf.service.biz;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import jodd.util.StringUtil;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.OutSideBpmBusinessParty;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.OutSideBpmProcesses;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmBusinessPartyServiceImpl;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmAdminPersonnelService;
import org.openoa.engine.vo.GenericEmployee;
import org.openoa.engine.vo.OutSideBpmBusinessPartyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class OutSideBpmBaseServiceImpl {

    @Autowired
    @Lazy
    private OutSideBpmBusinessPartyServiceImpl outSideBpmBusinessPartyService;

    @Autowired
    private OutSideBpmAdminPersonnelService outSideBpmAdminPersonnelService;

    @Autowired
    @Lazy
    private BpmnConfService bpmnConfService;


    /**
     * get current user's business party list
     *
     * @param name
     * @return
     */
    public List<OutSideBpmBusinessPartyVo> getEmplBusinessPartys(String name, String... permCodes) {

        GenericEmployee loginedEmployee =GenericEmployee.builder().userId(SecurityUtils.getLogInEmpId()).username(SecurityUtils.getLogInEmpName()).build();

        List<OutSideBpmBusinessParty> outSideBpmBusinessPartys = Lists.newArrayList();
        //todo to be redesigned
        if (loginedEmployee.getPermissions().contains("3060101")) {
            //if he/she has all business party permission,then list all
            outSideBpmBusinessPartys = outSideBpmBusinessPartyService.getBaseMapper().selectList(new QueryWrapper<>());
        } else {
            // if he/she is a normal business party administrator, then query the configuration of the employee's managed business party list
            List<Integer> businessPartyIds = outSideBpmAdminPersonnelService.getBusinessPartyIdByEmployeeId(loginedEmployee.getUserId(), permCodes);
            if (!CollectionUtils.isEmpty(businessPartyIds)) {
                outSideBpmBusinessPartys = outSideBpmBusinessPartyService.getBaseMapper().selectBatchIds(businessPartyIds);
            } else {
                outSideBpmBusinessPartys.add(OutSideBpmBusinessParty
                        .builder()
                        .id(-1L)
                        .build());
            }
        }

        //list mapping
        if (!CollectionUtils.isEmpty(outSideBpmBusinessPartys)) {
            List<OutSideBpmBusinessPartyVo> outSideBpmBusinessPartyVos = outSideBpmBusinessPartys
                    .stream()
                    .map(o -> OutSideBpmBusinessPartyVo
                            .builder()
                            .id(o.getId())
                            .name(o.getName())
                            .businessPartyMark(o.getBusinessPartyMark())
                            .type(o.getType())
                            .build())
                    .collect(Collectors.toList());


            //if name is not empty,then filter by name
            if (!StringUtil.isEmpty(name)) {
                outSideBpmBusinessPartyVos = outSideBpmBusinessPartyVos
                        .stream()
                        .filter(o -> o.getName().contains(name))
                        .collect(Collectors.toList());
            }

            return outSideBpmBusinessPartyVos;
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * get a third party's process by business party id and name,notice that name is not the business party's name but the process' name
     *
     * @param businessPartyId
     * @return
     */
    public List<OutSideBpmProcesses> getProcessesByBusinessParty(Integer businessPartyId, String name) {

        QueryWrapper<BpmnConf> wrapper = new QueryWrapper<BpmnConf>()
                .eq("business_party_id", businessPartyId)
                .eq("effective_status", 1);


        //if name is not empty,then filter data by name
        if (!StringUtil.isEmpty(name)) {
            wrapper.like("bpmn_name", name);
        }

        List<BpmnConf> bpmnConfs = bpmnConfService.getBaseMapper().selectList(wrapper);

        if (!CollectionUtils.isEmpty(bpmnConfs)) {
            return bpmnConfs
                    .stream()
                    .map(o -> OutSideBpmProcesses
                            .builder()
                            .id(o.getId().intValue())
                            .name(o.getBpmnName())
                            .formCode(o.getFormCode())
                            .build())
                    .collect(Collectors.toList());
        }

        return Collections.EMPTY_LIST;
    }

}
