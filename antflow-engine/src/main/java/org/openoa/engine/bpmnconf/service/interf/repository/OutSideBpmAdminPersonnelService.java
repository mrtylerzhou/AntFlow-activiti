package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.OutSideBpmAdminPersonnel;

import java.util.List;

public interface OutSideBpmAdminPersonnelService extends IService<OutSideBpmAdminPersonnel> {
    List<Integer> getBusinessPartyIdByEmployeeId(String employeeId, String... permCodes);
}
