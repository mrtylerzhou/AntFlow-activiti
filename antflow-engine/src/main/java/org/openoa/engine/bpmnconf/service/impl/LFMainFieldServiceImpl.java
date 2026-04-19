package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.mapper.LFMainFieldMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.LFMainFieldService;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LFMainFieldServiceImpl extends ServiceImpl<LFMainFieldMapper, LFMainField> implements LFMainFieldService {
    @Override
    public List<LFMainField> listByMainIdAndFormCode(Long mainId, String formCode) {
        return this.list(Wrappers.<LFMainField>lambdaQuery()
                .eq(LFMainField::getMainId, mainId)
                .eq(LFMainField::getFormCode, formCode));
    }
}
