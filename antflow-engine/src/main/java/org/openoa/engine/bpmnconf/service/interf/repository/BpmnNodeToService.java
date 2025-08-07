package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmnNodeTo;
import org.openoa.base.vo.BpmnNodeVo;

public interface BpmnNodeToService extends IService<BpmnNodeTo> {
    void editNodeTo(BpmnNodeVo bpmnNodeVo, Long bpmnNodeId);
}
