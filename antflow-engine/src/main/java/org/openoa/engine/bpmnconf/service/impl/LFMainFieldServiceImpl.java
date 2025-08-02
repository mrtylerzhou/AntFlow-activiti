package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.engine.bpmnconf.mapper.LFMainFieldMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.LFMainFieldService;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.springframework.stereotype.Repository;

@Repository
public class LFMainFieldServiceImpl extends ServiceImpl<LFMainFieldMapper, LFMainField> implements LFMainFieldService {

}
