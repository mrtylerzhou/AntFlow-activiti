package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.base.vo.TaskMgmtVO;

import java.util.List;

public interface LowCodeFlowBizService {
    //todo cbcbu
    List<BaseKeyValueStruVo> getLowCodeFlowFormCodes();
    //获取LF FormCode Page List 模板列表使用
    ResultAndPage<BaseKeyValueStruVo> selectLFFormCodePageList(PageDto pageDto, TaskMgmtVO taskMgmtVO);

    ResultAndPage<BaseKeyValueStruVo> selectLFActiveFormCodePageList(PageDto pageDto, TaskMgmtVO taskMgmtVO);

    Integer addFormCode(BaseKeyValueStruVo vo);
}
