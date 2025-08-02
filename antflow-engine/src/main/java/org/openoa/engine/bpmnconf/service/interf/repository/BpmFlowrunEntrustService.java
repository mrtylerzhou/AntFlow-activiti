package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.vo.BpmFlowrunEntrustVo;
import org.openoa.engine.bpmnconf.confentity.BpmFlowrunEntrust;
import org.openoa.engine.bpmnconf.confentity.UserEntrust;

import java.util.List;

public interface BpmFlowrunEntrustService extends IService<BpmFlowrunEntrust> {
    void addFlowrunEntrust(String actual, String actualName, String original, String originalName, String runtaskid, Integer type, String ProcessInstanceId, String processKey);

    boolean addFlowrunEntrust(BpmFlowrunEntrust flowrunEntrust);

    UserEntrust getBpmEntrust(String receiverId, String processKey);

    Boolean updateBpmFlowrunEntrust(String processInstanceId, Integer loginUserId);

    boolean editFlowrunEntrustState(String processInstanceId);

    List<BpmFlowrunEntrust> findFlowrunEntrustByProcessInstanceId(BpmFlowrunEntrustVo vo);
}
