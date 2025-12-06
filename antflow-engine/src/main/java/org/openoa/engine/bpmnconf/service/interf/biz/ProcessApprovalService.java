package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.vo.TaskMgmtVO;

public interface ProcessApprovalService {
    BusinessDataVo buttonsOperation(String params, String formCode);

    ResultAndPage<TaskMgmtVO> findPcProcessList(PageDto pageDto, TaskMgmtVO vo) throws AFBizException;

    BusinessDataVo getBusinessInfo(String params, String formCode);

    BusinessDataVo getBusinessInfo(BusinessDataVo vo);

    TaskMgmtVO processStatistics();
}
