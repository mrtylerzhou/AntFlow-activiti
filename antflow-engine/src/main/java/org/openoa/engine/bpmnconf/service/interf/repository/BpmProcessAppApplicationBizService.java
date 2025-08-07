package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAppApplicationMapper;
import org.openoa.engine.bpmnconf.service.interf.biz.BizService;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.IconInforVo;
import org.openoa.engine.vo.ProcessTypeInforVo;

import java.util.List;

public interface BpmProcessAppApplicationBizService extends BizService<BpmProcessAppApplicationMapper,BpmProcessAppApplicationService, BpmProcessAppApplication> {
    List<String> getAppEntrance(String version);

    Page<BpmProcessAppApplicationVo> getPcProcessData(Page<BpmProcessAppApplicationVo> page);

    List<ProcessTypeInforVo> processApplicationList();

    IconInforVo iconConfig(Integer isApp, Integer parentId, Integer processCategoryId);

    List<ProcessTypeInforVo> homePageIcon(BpmProcessAppApplicationVo vo);

    List<BpmProcessAppApplicationVo> bpmProcessAppApplicationVoList(List<BpmProcessAppApplicationVo> list);

    ProcessTypeInforVo iconCommon();

    ResultAndPage<BpmProcessAppApplicationVo> applicationsNewList(PageDto pageDto, BpmProcessAppApplicationVo vo);

    ResultAndPage<BpmProcessAppApplicationVo> applicationsList(PageDto pageDto, BpmProcessAppApplicationVo vo);
}
