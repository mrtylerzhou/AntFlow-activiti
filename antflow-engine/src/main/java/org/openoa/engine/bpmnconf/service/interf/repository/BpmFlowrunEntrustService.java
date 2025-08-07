package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmFlowrunEntrust;
import org.openoa.base.entity.UserEntrust;
import org.openoa.base.interf.IAFService;
import org.openoa.base.vo.BpmFlowrunEntrustVo;
import org.openoa.engine.bpmnconf.mapper.BpmFlowrunEntrustMapper;


import java.util.List;

public interface BpmFlowrunEntrustService extends IAFService<BpmFlowrunEntrustMapper,BpmFlowrunEntrust> {
    void addFlowrunEntrust(String actual, String actualName, String original, String originalName, String runtaskid, Integer type, String ProcessInstanceId, String processKey);

    boolean addFlowrunEntrust(BpmFlowrunEntrust flowrunEntrust);

    UserEntrust getBpmEntrust(String receiverId, String processKey);

    Boolean updateBpmFlowrunEntrust(String processInstanceId, Integer loginUserId);

    boolean editFlowrunEntrustState(String processInstanceId);

    List<BpmFlowrunEntrust> findFlowrunEntrustByProcessInstanceId(BpmFlowrunEntrustVo vo);
}
