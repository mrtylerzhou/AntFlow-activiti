package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.vo.BpmnNodeVo;
import org.openoa.base.entity.BpmnApproveRemind;

public interface BpmnApproveRemindService extends IService<BpmnApproveRemind> {
    void editBpmnApproveRemind(BpmnNodeVo bpmnNodeVo);
}
