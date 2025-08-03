package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmnViewPageButton;

public interface BpmnViewPageButtonService extends IService<BpmnViewPageButton> {
    Integer deleteByConfId(Long confId);
}
