package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.UserEntrust;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.DataVo;
import org.openoa.base.vo.Entrust;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserEntrustService extends IService<UserEntrust> {
    //获get current login employee's entrust list
    List<Entrust> getEntrustList();

    //batch save or update entrust list
    @Transactional
    void updateEntrustList(DataVo dataVo);

    BaseIdTranStruVo getEntrustEmployee(String employeeId, String employeeName, String powerId);

    BaseIdTranStruVo getEntrustEmployeeOnly(String employeeId, String employeeName, String powerId);
}
