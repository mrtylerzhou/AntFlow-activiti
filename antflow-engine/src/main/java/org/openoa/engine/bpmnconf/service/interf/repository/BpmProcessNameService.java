package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessName;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmProcessVo;

import java.util.List;
import java.util.Map;

public interface BpmProcessNameService extends IService<BpmProcessName> {
    List<BaseIdTranStruVo> listResult();

    BpmProcessName findProcessName(String processName);

    Map<String, BpmProcessVo> loadProcessName();

    BpmProcessVo get(String processKey);
}
