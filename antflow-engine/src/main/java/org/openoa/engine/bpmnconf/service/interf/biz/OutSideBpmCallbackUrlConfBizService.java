package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.OutSideBpmCallbackUrlConf;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmCallbackUrlConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmCallbackUrlConfService;
import org.openoa.engine.vo.OutSideBpmCallbackUrlConfVo;

public interface OutSideBpmCallbackUrlConfBizService extends BizService<OutSideBpmCallbackUrlConfMapper, OutSideBpmCallbackUrlConfService, OutSideBpmCallbackUrlConf>{
    ResultAndPage<OutSideBpmCallbackUrlConfVo> listPage(PageDto pageDto, OutSideBpmCallbackUrlConfVo vo);

    OutSideBpmCallbackUrlConfVo detail(Integer id);
}
