package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.vo.BaseKeyValueStruVo;
import org.openoa.base.vo.DIYProcessInfoDTO;
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

    //page-added DIY(LF 后端 + 自定义 Vue 前端, dict_type='diylowcodeflow')
    ResultAndPage<BaseKeyValueStruVo> selectDIYFormCodePageList(PageDto pageDto, TaskMgmtVO taskMgmtVO);

    Integer addDIYFormCode(BaseKeyValueStruVo vo);

    /** page-added DIY(有效版本): 供"流程中心-可用流程(DIY)"合并展示 */
    List<DIYProcessInfoDTO> getDIYActiveFormCodes();
}
