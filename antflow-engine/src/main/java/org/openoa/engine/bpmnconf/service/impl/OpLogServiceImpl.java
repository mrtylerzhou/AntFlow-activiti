package org.openoa.engine.bpmnconf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.openoa.engine.bpmnconf.confentity.OpLog;
import org.openoa.engine.bpmnconf.mapper.OpLogMapper;
import org.openoa.base.util.JimuJsonUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpLogServiceImpl extends ServiceImpl<OpLogMapper, OpLog> {


    @Async
    public void asyncInsert(OpLog opLog) {
        try {
            this.getBaseMapper().insert(opLog);
        } catch (Throwable t) {
            log.error(JimuJsonUtil.toJsonString(opLog), t);
        }
    }

}
