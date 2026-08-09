package org.openoa.engine.lowflow.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.LFFormOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.UDLFApplyVo;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfLfFormdataService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public abstract class AbstractFreeRideFomOperationAdaptor implements LFFormOperationAdaptor<UDLFApplyVo> {

    @Autowired
    private BpmBusinessProcessService businessProcessService;
    @Autowired
    private BpmnConfService bpmnConfService;

    @Autowired
    private BpmnConfLfFormdataService lfFormdataService;

    @Override
    public final void submitData(UDLFApplyVo vo) {
        if(StringUtils.isEmpty(vo.getBusinessId())){
            throw new AFBizException(BusinessErrorEnum.PARAMS_IS_NULL.getCodeStr(),"便车流程必须传入业务Id");
        }
        vo.setProcessDigest(vo.getRemark());
        vo.setEntityName(this.getClass().getSimpleName());
    }

    @Override
    public final void queryData(UDLFApplyVo vo) {
        BpmBusinessProcess bpmBusinessProcess = businessProcessService.getBpmBusinessProcess(vo.getProcessNumber());
        String businessId = bpmBusinessProcess.getBusinessId();
        vo.setBusinessId(businessId);
        BpmnConf bpmnConf = bpmnConfService.getOne(Wrappers.<BpmnConf>lambdaQuery().eq(BpmnConf::getBpmnCode, bpmBusinessProcess.getVersion()));
        String confId=bpmnConf.getId().toString();
        List<BpmnConfLfFormdata> bpmnConfLfFormdataList = lfFormdataService.list(Wrappers.<BpmnConfLfFormdata>lambdaQuery().eq(BpmnConfLfFormdata::getBpmnConfId, confId));
        if(CollectionUtils.isEmpty(bpmnConfLfFormdataList)){
            throw  new AFBizException(Strings.lenientFormat("can not get lowcode flow formdata by confId:%s",confId));
        }
        BpmnConfLfFormdata lfFormdata = bpmnConfLfFormdataList.get(0);
        vo.setLfFormData(lfFormdata.getFormdata());
        queryBusinessData(vo);
    }
    abstract void  queryBusinessData(UDLFApplyVo vo);
}
