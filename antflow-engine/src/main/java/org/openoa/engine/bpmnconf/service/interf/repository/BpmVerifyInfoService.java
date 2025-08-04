package org.openoa.engine.bpmnconf.service.interf.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.openoa.base.entity.BpmVerifyInfo;
import org.openoa.base.vo.BpmVerifyInfoVo;

import java.util.List;
import java.util.Map;

public interface BpmVerifyInfoService extends IService<BpmVerifyInfo> {

    void addVerifyInfo(String businessId, String remark, Integer businessType, String taskName, Integer verifyStatuss);

    Map<String,BpmVerifyInfo> getByProcInstIdAndTaskDefKey(String processNumber, String taskDefKey);

    Map<String, List<BpmVerifyInfoVo>> getBpmVerifyInfoBatch(List<String> processCodes);

    Map<String, String> listBpmVerifyInfoVo(List<String> processCodes);
}
