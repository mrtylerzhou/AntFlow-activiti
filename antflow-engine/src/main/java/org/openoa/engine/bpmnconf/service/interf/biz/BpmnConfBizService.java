package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BpmnConfBizService extends BizService<BpmnConfMapper, BpmnConfService, BpmnConf>{
    @Transactional
    void edit(BpmnConfVo bpmnConfVo);

    BpmnConf getBpmnConfByFormCode(String formCode);

    List<BpmnConf> getIsAllConfs();

    BpmnConfVo detail(long id);

    BpmnConfVo detail(String bpmnCode);

    BpmnConfVo detailByFormCode(String formCode);

    void setBpmnTemplateVos(BpmnConfVo bpmnConfVo);

    void effectiveBpmnConf(Integer id);

    ResultAndPage<BpmnConfVo> selectPage(PageDto pageDto, BpmnConfVo vo);
}
