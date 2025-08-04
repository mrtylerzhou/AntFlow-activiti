package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessNotice;
import org.openoa.base.vo.BpmProcessDeptVo;

import java.util.List;
import java.util.Map;

public interface BpmProcessNoticeService extends IService<BpmProcessNotice> {
    void saveProcessNotice(BpmProcessDeptVo vo);

    List<BpmProcessNotice> processNoticeList(String processKey);

    Map<String,List<BpmProcessNotice>> processNoticeMap(List<String> processKeys);
}
