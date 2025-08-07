package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmnNodeSignUpConf;
import org.openoa.base.vo.BpmnNodeVo;

public interface BpmnNodeSignUpConfService extends IService<BpmnNodeSignUpConf> {
    void editSignUpConf(BpmnNodeVo bpmnNodeVo, Long bpmnNodeId);
}
