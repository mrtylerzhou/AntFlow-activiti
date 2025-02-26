package org.openoa.engine.bpmnconf.service.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.vo.TaskMgmtVO;

import java.util.List;

public interface LowCodeFlowBizService {
    //todo cbcbu
    List<BaseKeyValueStruVo> getLowCodeFlowFormCodes();

    List<BaseKeyValueStruVo> getLFActiveFormCodes();

    ResultAndPage<BaseKeyValueStruVo> selectLFActiveFormCodePageList(PageDto pageDto, TaskMgmtVO taskMgmtVO);

    Integer addFormCode(BaseKeyValueStruVo vo);
}
