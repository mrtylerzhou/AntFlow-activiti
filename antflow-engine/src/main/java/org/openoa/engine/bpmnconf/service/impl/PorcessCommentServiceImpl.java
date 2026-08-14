package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.openoa.base.entity.BpmProcessComment;
import org.openoa.engine.bpmnconf.mapper.BpmProcessCommentMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.ProcessCommentService;
import org.springframework.stereotype.Repository;

@Repository
public class PorcessCommentServiceImpl extends ServiceImpl<BpmProcessCommentMapper, BpmProcessComment> implements ProcessCommentService {
}
