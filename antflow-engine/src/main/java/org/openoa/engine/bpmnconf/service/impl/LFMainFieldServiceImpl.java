package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdataField;
import org.openoa.engine.bpmnconf.mapper.LFMainFieldMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.LFMainFieldService;
import org.openoa.engine.bpmnconf.util.JsonUtils;
import org.openoa.engine.lowflow.entity.LFMainField;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
public class LFMainFieldServiceImpl extends ServiceImpl<LFMainFieldMapper, LFMainField> implements LFMainFieldService {

}
