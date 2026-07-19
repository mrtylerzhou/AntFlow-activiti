package org.openoa.engine.lowflow.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.entity.BpmnConf;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.interf.LFFormOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.UDLFApplyVo;
import org.openoa.engine.bpmnconf.mapper.BpmnConfLfFormdataMapper;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfLfFormdataService;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnConfService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractFreeRideFomOperationAdaptor implements LFFormOperationAdaptor<UDLFApplyVo> {

    @Autowired
    private BpmBusinessProcessService businessProcessService;
    @Autowired
    private BpmnConfService bpmnConfService;

    @Autowired
    private BpmnConfLfFormdataService lfFormdataService;
    @Autowired
    private BpmnConfLfFormdataMapper lfFormdataMapper;

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

        // 外部表单模式: 按 lf_formdata_ids 加载多表单(含已软删,保证运行中流程可读)
        if(BpmnConfFlagsEnum.USE_EXTERNAL_FORM.flagsContainsCurrent(bpmnConf.getExtraFlags())){
            String lfFormdataIds = bpmnConf.getLfFormdataIds();
            if(Strings.isNullOrEmpty(lfFormdataIds)){
                throw new AFBizException(Strings.lenientFormat("external form mode but lf_formdata_ids is empty, confId:%s", confId));
            }
            List<Long> ids = Arrays.stream(lfFormdataIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<BpmnConfLfFormdata> forms = lfFormdataMapper.listByIdsIgnoreDeleted(ids);
            if(CollectionUtils.isEmpty(forms)){
                throw new AFBizException(Strings.lenientFormat("can not get external forms by ids:%s", lfFormdataIds));
            }
            vo.setLfFormdataList(forms);
            queryBusinessData(vo);
            return;
        }

        // 内联表单模式
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
