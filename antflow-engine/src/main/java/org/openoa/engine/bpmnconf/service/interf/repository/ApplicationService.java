package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.dto.PageDto;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.engine.vo.BaseApplicationVo;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.BpmProcessCategoryVo;

import java.util.List;

public interface ApplicationService extends IService<BpmProcessAppApplication> {
    void edit(BpmProcessAppApplicationVo vo);

    void del(Integer id);

    ResultAndPage<BpmProcessAppApplicationVo> pageList(PageDto pageDto, BpmProcessAppApplicationVo vo);

    BpmProcessAppApplicationVo getApplicationUrl(String businessCode, String processKey);

    List<BpmProcessAppApplicationVo> getParentApplicationList(BpmProcessAppApplicationVo applicationVo);

    List<BpmProcessCategoryVo> getProcessTypeList(BpmProcessAppApplicationVo vo);

    List<BaseApplicationVo> getApplicationKeyList(BpmProcessAppApplicationVo applicationVo);
}
