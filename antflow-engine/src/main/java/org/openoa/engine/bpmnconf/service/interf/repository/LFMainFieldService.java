package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.engine.lowflow.entity.LFMainField;

import java.util.List;

public interface LFMainFieldService extends IService<LFMainField> {
    List<LFMainField> listByMainIdAndFormCode(Long mainId, String formCode);

    /**
     * 按mainId查询全部表单字段(多表单模式使用)
     */
    List<LFMainField> listByMainId(Long mainId);
}
