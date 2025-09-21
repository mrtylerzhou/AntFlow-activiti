package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAppApplicationMapper;
import org.openoa.engine.bpmnconf.service.interf.ApplicationService;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.BpmProcessCategoryVo;

import java.util.List;

public interface ApplicationBizService extends BizService<BpmProcessAppApplicationMapper,ApplicationService, BpmProcessAppApplication>{
    void edit(BpmProcessAppApplicationVo vo);

    ResultAndPage<BpmProcessAppApplicationVo> pageList(PageDto pageDto, BpmProcessAppApplicationVo vo);

    List<BpmProcessAppApplicationVo> getParentApplicationList(BpmProcessAppApplicationVo applicationVo);

    List<BpmProcessCategoryVo> getProcessTypeList(BpmProcessAppApplicationVo vo);
}
