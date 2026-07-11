package org.openoa.engine.lowflow.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.AntFlowOrderPreProcessor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.engine.bpmnconf.mapper.BpmnConfLfFormdataMapper;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LFFormDataPreProcessor implements AntFlowOrderPreProcessor<BpmnConfVo> {
    @Autowired
    private BpmnConfLfFormdataServiceImpl lfFormdataService;
    @Autowired
    private BpmnConfLfFormdataFieldServiceImpl lfFormdataFieldService;
    @Autowired
    private BpmnConfLfFormdataMapper lfFormdataMapper;


    @Override
    public void preWriteProcess(BpmnConfVo confVo) {
        if(confVo==null){
            return;
        }
        Integer isLowCodeFlow = confVo.getIsLowCodeFlow();
        boolean lowCodeFlowFlag=isLowCodeFlow!=null&&isLowCodeFlow==1;
        if(!lowCodeFlowFlag){
            return;
        }
        // 外部表单模式: 表单由独立表单管理模块维护,此处不保存内联表单数据
        if(BpmnConfFlagsEnum.USE_EXTERNAL_FORM.flagsContainsCurrent(confVo.getExtraFlags())){
            return;
        }
        Long confId = confVo.getId();
        String lfForm = confVo.getLfFormData();
        BpmnConfLfFormdata lfFormdata=new BpmnConfLfFormdata();
        lfFormdata.setBpmnConfId(confId);
        lfFormdata.setFormdata(lfForm);
        lfFormdata.setCreateUser(SecurityUtils.getLogInEmpName());
        lfFormdataService.save(lfFormdata);
        confVo.setLfFormDataId(lfFormdata.getId());
        List<BpmnConfLfFormdataField> formdataFields = LfFormWidgetParser.parseFields(lfForm, confId, lfFormdata.getId());
        lfFormdataFieldService.saveBatch(formdataFields);
    }

    @Override
    public void preReadProcess(BpmnConfVo confVo) {
        if(confVo==null){
            return;
        }
        Integer isLowCodeFlow = confVo.getIsLowCodeFlow();
        boolean lowCodeFlowFlag=isLowCodeFlow!=null&&isLowCodeFlow==1;
        if(!lowCodeFlowFlag){
            return;
        }
        // 外部表单模式: 按 CSV 加载引用的表单版本(含已软删,保证运行中流程可读)
        if(BpmnConfFlagsEnum.USE_EXTERNAL_FORM.flagsContainsCurrent(confVo.getExtraFlags())){
            String lfFormdataIds = confVo.getLfFormdataIds();
            if(Strings.isNullOrEmpty(lfFormdataIds)){
                throw new AFBizException(Strings.lenientFormat("external form mode but lf_formdata_ids is empty, confId:%s", confVo.getId()));
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
            confVo.setLfFormdataList(forms);
            return;
        }
        // 内联表单模式: 兼容旧逻辑,加载单个表单
        Long confId = confVo.getId();
        List<BpmnConfLfFormdata> bpmnConfLfFormdataList = lfFormdataService.list(Wrappers.<BpmnConfLfFormdata>lambdaQuery().eq(BpmnConfLfFormdata::getBpmnConfId, confId));
        if(CollectionUtils.isEmpty(bpmnConfLfFormdataList)){
            throw  new AFBizException(Strings.lenientFormat("can not get lowcode flow formdata by confId:%s",confId));
        }
        BpmnConfLfFormdata lfFormdata = bpmnConfLfFormdataList.get(0);
        confVo.setLfFormData(lfFormdata.getFormdata());
        confVo.setLfFormDataId(lfFormdata.getId());
        // 同时填充 lfFormdataList,供前端统一渲染多tab表单视图
        confVo.setLfFormdataList(bpmnConfLfFormdataList);
    }

    @Override
    public int order() {
        return 0;
    }
}
