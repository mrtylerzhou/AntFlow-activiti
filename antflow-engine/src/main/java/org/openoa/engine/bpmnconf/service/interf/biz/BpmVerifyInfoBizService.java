package org.openoa.engine.bpmnconf.service.interf.biz;

import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.vo.BpmVerifyInfoVo;
import org.openoa.engine.bpmnconf.mapper.BpmVerifyInfoMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmVerifyInfoService;

import java.util.List;
import java.util.Map;

public interface BpmVerifyInfoBizService extends BizService<BpmVerifyInfoMapper, BpmVerifyInfoService, BpmVerifyInfo>{
    List<BpmVerifyInfoVo> getVerifyInfoList(String processCode);

    List<BpmVerifyInfoVo> getBpmVerifyInfoVos(String processNumber, boolean finishFlag);

    String  findCurrentNodeIds(String processNumber);

    void addVerifyInfo(BpmVerifyInfo verifyInfo);

    void addVerifyInfo(String businessId, String taskId, String remark, Integer businessType, Integer status);

    List<BpmVerifyInfoVo> getBpmVerifyInfoVoList(List<BpmVerifyInfoVo> list, String procInstId);

    Map<String, String> getSignUpNodeCollectionNameMap(Long variableId);

    List<BpmVerifyInfoVo> verifyInfoList(BpmBusinessProcess bpmBusinessProcess);

    List<BpmVerifyInfoVo> verifyInfoList(String processNumber);

    List<BpmVerifyInfoVo> verifyInfoList(String processNumber, String procInstId);
}
