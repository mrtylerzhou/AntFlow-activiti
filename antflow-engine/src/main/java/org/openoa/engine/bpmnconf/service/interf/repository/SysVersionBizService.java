package org.openoa.engine.bpmnconf.service.interf.repository;

import org.openoa.base.entity.SysVersion;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.SysVersionMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BizService;
import org.openoa.engine.vo.SysVersionVo;
import org.springframework.transaction.annotation.Transactional;

public interface SysVersionBizService extends BizService<SysVersionMapper,SysVersionService, SysVersion> {
    ResultAndPage<SysVersionVo> list(SysVersionVo vo);

    @Transactional
    Boolean edit(SysVersionVo vo);
}
