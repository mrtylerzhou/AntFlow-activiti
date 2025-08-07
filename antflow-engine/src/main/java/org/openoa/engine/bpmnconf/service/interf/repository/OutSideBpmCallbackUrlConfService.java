package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.OutSideBpmCallbackUrlConf;
import org.openoa.engine.vo.OutSideBpmCallbackUrlConfVo;

import java.util.List;

public interface OutSideBpmCallbackUrlConfService extends IService<OutSideBpmCallbackUrlConf> {
    List<OutSideBpmCallbackUrlConf> selectListByFormCode(String formCode);

    void edit(OutSideBpmCallbackUrlConfVo vo);

    OutSideBpmCallbackUrlConf getOutSideBpmCallbackUrlConf(Long bpmnConfId, Long businessPartyId);
}
