package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.OutSideBpmAccessBusiness;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.vo.ResultAndPage;
import org.openoa.engine.bpmnconf.mapper.OutSideBpmAccessBusinessMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.OutSideBpmAccessBusinessService;
import org.openoa.engine.vo.OutSideBpmAccessBusinessVo;
import org.openoa.engine.vo.OutSideBpmAccessProcessRecordVo;
import org.openoa.engine.vo.OutSideBpmAccessRespVo;

import java.util.List;

public interface OutSideBpmAccessBusinessBizService extends BizService<OutSideBpmAccessBusinessMapper, OutSideBpmAccessBusinessService, OutSideBpmAccessBusiness>{
    OutSideBpmAccessRespVo accessBusinessStart(OutSideBpmAccessBusinessVo vo);

    ResultAndPage<BpmnConfVo> selectOutSideFormCodePageList(PageDto pageDto, BpmnConfVo vo);

    List<OutSideBpmAccessProcessRecordVo> outSideProcessRecord(String processNumber);

    List<OutSideBpmAccessProcessRecordVo> accessBusinessPreview(OutSideBpmAccessBusinessVo vo);

    void processBreak(OutSideBpmAccessBusinessVo vo);
}
