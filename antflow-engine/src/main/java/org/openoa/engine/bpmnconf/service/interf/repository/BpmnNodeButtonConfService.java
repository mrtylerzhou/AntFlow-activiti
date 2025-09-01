package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.constant.enums.ButtonPageTypeEnum;
import org.openoa.base.entity.BpmnNodeButtonConf;
import org.openoa.base.vo.BpmnNodeVo;

import java.util.List;

public interface BpmnNodeButtonConfService extends IService<BpmnNodeButtonConf> {
    void editButtons(BpmnNodeVo bpmnNodeVo, Long bpmnNodeId);
    List<BpmnNodeButtonConf> queryByNodeIds(List<String> nodeIds, ButtonPageTypeEnum type);
}
