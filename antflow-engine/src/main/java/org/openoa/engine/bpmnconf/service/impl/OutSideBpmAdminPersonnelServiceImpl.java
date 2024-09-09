package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.openoa.base.constant.enums.AdminPersonnelTypeEnum;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmAdminPersonnel;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmAdminPersonnelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * thirdy party process service-business party admin personnel management
 * @since 0.5
 */
@Service
public class OutSideBpmAdminPersonnelServiceImpl extends ServiceImpl<OutSideBpmAdminPersonnelMapper, OutSideBpmAdminPersonnel> {

    @Autowired
    private OutSideBpmAdminPersonnelMapper outSideBpmAdminPersonnelMapper;

    /**
     * query business party id list by employee id
     * @param employeeId
     * @return
     */
    public List<Integer> getBusinessPartyIdByEmployeeId(String employeeId, String... permCodes) {

        List<Integer> types = Lists.newArrayList();
        if(permCodes.length>0){
            for (String permCode : permCodes) {
                AdminPersonnelTypeEnum enumByPermCode = AdminPersonnelTypeEnum.getEnumByPermCode(permCode);
                types.add(enumByPermCode.getCode());
            }
        }

        return outSideBpmAdminPersonnelMapper.getBusinessPartyIdByEmployeeId(employeeId, types);
    }


}
