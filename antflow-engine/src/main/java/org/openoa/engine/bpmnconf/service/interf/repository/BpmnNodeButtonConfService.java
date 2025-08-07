package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmnNodeButtonConf;
import org.openoa.base.vo.BpmnNodeVo;

public interface BpmnNodeButtonConfService extends IService<BpmnNodeButtonConf> {
    void editButtons(BpmnNodeVo bpmnNodeVo, Long bpmnNodeId);
}
