package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmProcessNodeOvertime;
import org.openoa.base.vo.BpmProcessDeptVo;

import java.util.List;

public interface BpmProcessNodeOvertimeService extends IService<BpmProcessNodeOvertime> {
    boolean saveNodeOvertime(BpmProcessDeptVo vo);

    List<BpmProcessNodeOvertime> nodeOvertimeList(String processKey);

    List<Integer> selectNoticeType(String processKey);
}
