package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.ProcessTypeInforVo;

import java.util.List;

public interface BpmProcessAppApplicationService extends IService<BpmProcessAppApplication> {
    boolean addBpmProcessAppApplication(BpmProcessAppApplicationVo vo);

    boolean deleteAppIcon(Long id);

    List<ProcessTypeInforVo> list(String version);

    List<BpmProcessAppApplicationVo> listProcessApplication();

    List<BaseIdTranStruVo> listProcessAppApplication(String search, Integer limitSize);

    List<BpmProcessAppApplicationVo> selectThirdPartyApplications(String businessPartyMark);

    List<BpmProcessAppApplicationVo> selectAllByPartMarkId(Integer partyMarkId);

    List<BpmProcessAppApplication> selectApplicationList();
}
