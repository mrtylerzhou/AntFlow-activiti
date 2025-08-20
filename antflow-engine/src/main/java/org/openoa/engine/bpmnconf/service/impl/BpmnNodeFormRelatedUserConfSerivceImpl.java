package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeFormRelatedUserConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeFormRelatedUserConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeFormRelatedUserConfService;
import org.springframework.stereotype.Repository;

@Repository
public class BpmnNodeFormRelatedUserConfSerivceImpl extends ServiceImpl<BpmnNodeFormRelatedUserConfMapper, BpmnNodeFormRelatedUserConf> implements BpmnNodeFormRelatedUserConfService {
}
