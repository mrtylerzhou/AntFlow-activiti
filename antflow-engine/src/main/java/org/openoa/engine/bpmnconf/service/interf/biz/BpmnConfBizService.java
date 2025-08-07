package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.dto.PageDto;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.vo.*;
import org.openoa.engine.bpmnconf.mapper.BpmnConfMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BpmnConfBizService extends BizService<BpmnConfMapper, BpmnConfService, BpmnConf>{
    @Transactional
    void edit(BpmnConfVo bpmnConfVo);

    BpmnConf getBpmnConfByFormCode(String formCode);

    List<BpmnConf> getBpmnConfByFormCodeBatch(List<String> formCodes);

    void updateBpmnConfByCode(Integer appId, Integer bpmnType, Integer isAll, String bpmnCode);

    void startProcess(String bpmnCode, BpmnStartConditionsVo bpmnStartConditions);

    PreviewNode previewNode(String params);

    PreviewNode startPagePreviewNode(String params);

    List<BpmVerifyInfoVo> appStartPagePreviewNode(String params);

    boolean migrationCheckConditionsChange(BusinessDataVo vo);

    List<BpmnNodeVo> setNodeFrom(List<BpmnNodeVo> nodeList);

    List<BpmnNodeVo> setNodeFromV2(List<BpmnNodeVo> nodeList);

    List<BpmnConf> getIsAllConfs();

    BpmnConfVo detail(long id);

    BpmnConfVo detail(String bpmnCode);

    BpmnConfVo detailByFormCode(String formCode);

    void setBpmnTemplateVos(BpmnConfVo bpmnConfVo);

    void effectiveBpmnConf(Integer id);

    ResultAndPage<BpmnConfVo> selectPage(PageDto pageDto, BpmnConfVo vo);
}
