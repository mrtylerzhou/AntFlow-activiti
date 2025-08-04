package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.openoa.base.constant.enums.AdminPersonnelTypeEnum;
import org.openoa.base.entity.OutSideBpmAdminPersonnel;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmAdminPersonnelMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmAdminPersonnelService;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * thirdy party process service-business party admin personnel management
 * @since 0.5
 */
@Repository
public class OutSideBpmAdminPersonnelServiceImpl extends ServiceImpl<OutSideBpmAdminPersonnelMapper, OutSideBpmAdminPersonnel> implements OutSideBpmAdminPersonnelService {

    /**
     * query business party id list by employee id
     * @param employeeId
     * @return
     */
    @Override
    public List<Integer> getBusinessPartyIdByEmployeeId(String employeeId, String... permCodes) {

        List<Integer> types = Lists.newArrayList();
        if(permCodes.length>0){
            for (String permCode : permCodes) {
                AdminPersonnelTypeEnum enumByPermCode = AdminPersonnelTypeEnum.getEnumByPermCode(permCode);
                types.add(enumByPermCode.getCode());
            }
        }

        return getBaseMapper().getBusinessPartyIdByEmployeeId(employeeId, types);
    }


}
