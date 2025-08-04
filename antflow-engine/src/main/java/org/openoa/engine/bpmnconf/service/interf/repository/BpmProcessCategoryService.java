package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessCategory;
import org.openoa.engine.vo.BpmProcessCategoryVo;

import java.util.List;

public interface BpmProcessCategoryService extends IService<BpmProcessCategory> {
    boolean editProcessCategory(BpmProcessCategoryVo vo);

    boolean categoryOperation(Integer type, Long id);

    boolean moveUp(Long id);

    boolean moveDown(Long id);

    List<BpmProcessCategory> processCategoryList(BpmProcessCategoryVo vo);

    List<BpmProcessCategoryVo> bpmProcessAppApplicationVoList(List<BpmProcessCategoryVo> list);

    BpmProcessCategory getProcessCategory(Long id);
}
