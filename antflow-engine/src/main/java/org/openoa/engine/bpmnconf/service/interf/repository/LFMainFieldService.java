package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.engine.lowflow.entity.LFMainField;

import java.util.List;

public interface LFMainFieldService extends IService<LFMainField> {
    List<LFMainField> listByMainIdAndFormCode(Long mainId, String formCode);
}
