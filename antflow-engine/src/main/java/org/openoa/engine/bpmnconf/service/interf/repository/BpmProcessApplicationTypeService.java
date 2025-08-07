package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessApplicationType;
import org.openoa.engine.vo.BpmProcessApplicationTypeVo;

import java.util.List;

public interface BpmProcessApplicationTypeService extends IService<BpmProcessApplicationType> {
    List<BpmProcessApplicationType> applicationTypes(BpmProcessApplicationTypeVo vo);

    boolean deletProcessApplicationType(BpmProcessApplicationTypeVo vo);

    boolean sortProcessApplicationType(Integer sort, Long processTypeId);

    boolean sortProcessApplicationType(Integer sort, Integer countTotal, List<BpmProcessApplicationType> list);

    boolean editProcessApplicationType(BpmProcessApplicationTypeVo vo);

    Long addProcessApplicationType(BpmProcessApplicationTypeVo vo);

    boolean hiddenApplication(Long id, boolean isHidden, Long commonUseId);

    List<BpmProcessApplicationType> getProcessApplicationType(BpmProcessApplicationTypeVo vo);
}
