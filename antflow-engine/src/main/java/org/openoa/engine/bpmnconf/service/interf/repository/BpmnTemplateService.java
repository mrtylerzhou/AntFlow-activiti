package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmnTemplate;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.BpmnNodeVo;


public interface BpmnTemplateService extends IService<BpmnTemplate> {
    void editBpmnTemplate(BpmnConfVo bpmnConfVo, Long confId);

    void editBpmnTemplate(BpmnNodeVo bpmnNodeVo);
}
