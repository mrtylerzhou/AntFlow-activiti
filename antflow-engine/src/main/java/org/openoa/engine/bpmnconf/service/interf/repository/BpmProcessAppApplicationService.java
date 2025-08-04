package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessAppApplication;
import org.openoa.base.interf.IAFService;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.engine.bpmnconf.mapper.BpmProcessAppApplicationMapper;
import org.openoa.engine.vo.BpmProcessAppApplicationVo;
import org.openoa.engine.vo.ProcessTypeInforVo;

import java.util.List;

public interface BpmProcessAppApplicationService extends IAFService<BpmProcessAppApplicationMapper,BpmProcessAppApplication> {
    boolean addBpmProcessAppApplication(BpmProcessAppApplicationVo vo);

    boolean deleteAppIcon(Long id);

    List<ProcessTypeInforVo> list(String version);

    List<BpmProcessAppApplicationVo> listProcessApplication();

    List<BaseIdTranStruVo> listProcessAppApplication(String search, Integer limitSize);

    List<BpmProcessAppApplicationVo> selectThirdPartyApplications(String businessPartyMark);

    List<BpmProcessAppApplicationVo> selectAllByPartMarkId(Integer partyMarkId);

    List<BpmProcessAppApplication> selectApplicationList();
}
