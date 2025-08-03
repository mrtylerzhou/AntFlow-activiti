package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmnNodeOutSideAccessConf;
import org.openoa.engine.bpmnconf.mapper.BpmnNodeOutSideAccessConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeOutSideAccessConfService;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 *  thirdy party process service access service
 * @since 0.5
 */
@Repository
public class BpmnNodeOutSideAccessConfServiceImpl extends ServiceImpl<BpmnNodeOutSideAccessConfMapper, BpmnNodeOutSideAccessConf> implements BpmnNodeOutSideAccessConfService {

}
