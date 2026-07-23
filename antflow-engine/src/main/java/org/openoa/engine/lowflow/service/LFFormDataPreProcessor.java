package org.openoa.engine.lowflow.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.base.Strings;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.constant.enums.BpmnConfFlagsEnum;
import org.openoa.base.constant.enums.LFFieldTypeEnum;
import org.openoa.base.constant.enums.VariantFormContainerTypeEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.service.AntFlowOrderPreProcessor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.entity.BpmnConfLfFormdata;
import org.openoa.base.entity.BpmnConfLfFormdataField;
import org.openoa.base.vo.FormConfigWrapper;
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
        //DIY辅助表单:启用 USE_AUXILIARY_FORM 时同样需要持久化表单JSON与字段契约
        boolean auxiliaryFormFlag=BpmnConfFlagsEnum.USE_AUXILIARY_FORM.flagsContainsCurrent(confVo.getExtraFlags());
        if(!lowCodeFlowFlag && !auxiliaryFormFlag){
            return;
        }
        // 外部表单模式: 表单由独立表单管理模块维护,此处不保存内联表单数据(仅LF有此模式)
        if(lowCodeFlowFlag && BpmnConfFlagsEnum.USE_EXTERNAL_FORM.flagsContainsCurrent(confVo.getExtraFlags())){
            return;
        }
        Long confId = confVo.getId();
        String lfForm = confVo.getLfFormData();
        //DIY辅助表单未启用或表单为空时不落库,避免空表单污染字段契约
        if(auxiliaryFormFlag && !lowCodeFlowFlag && Strings.isNullOrEmpty(lfForm)){
            return;
        }
        BpmnConfLfFormdata lfFormdata=new BpmnConfLfFormdata();
        lfFormdata.setBpmnConfId(confId);
        lfFormdata.setFormdata(lfForm);
        lfFormdata.setCreateUser(SecurityUtils.getLogInEmpName());
        lfFormdataService.save(lfFormdata);
        confVo.setLfFormDataId(lfFormdata.getId());
        //DIY辅助表单与LF流程一致: 表单至少需要一个控件,否则 parseFields 抛异常
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
        boolean auxiliaryFormFlag=BpmnConfFlagsEnum.USE_AUXILIARY_FORM.flagsContainsCurrent(confVo.getExtraFlags());
        if(!lowCodeFlowFlag && !auxiliaryFormFlag){
            return;
        }
        // 外部表单模式: 按 CSV 加载引用的表单版本(含已软删,保证运行中流程可读),仅LF有此模式
        if(lowCodeFlowFlag && BpmnConfFlagsEnum.USE_EXTERNAL_FORM.flagsContainsCurrent(confVo.getExtraFlags())){
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
        // 内联表单模式: LF内联 或 DIY辅助表单,加载单个表单(供设计期回显)
        Long confId = confVo.getId();
        List<BpmnConfLfFormdata> bpmnConfLfFormdataList = lfFormdataService.list(Wrappers.<BpmnConfLfFormdata>lambdaQuery().eq(BpmnConfLfFormdata::getBpmnConfId, confId));
        if(CollectionUtils.isEmpty(bpmnConfLfFormdataList)){
            //DIY辅助表单: 允许未设计表单(空表单),不抛异常,前端显示空设计器
            if (auxiliaryFormFlag && !lowCodeFlowFlag) {
                return;
            }
            throw  new AFBizException(Strings.lenientFormat("can not get lowcode flow formdata by confId:%s",confId));
        }
        BpmnConfLfFormdata lfFormdata = bpmnConfLfFormdataList.get(0);
        confVo.setLfFormData(lfFormdata.getFormdata());
        confVo.setLfFormDataId(lfFormdata.getId());
        // 同时填充 lfFormdataList,供LF前端统一渲染多tab表单视图;DIY辅助表单不参与审批渲染,无需填充
        if(lowCodeFlowFlag){
            confVo.setLfFormdataList(bpmnConfLfFormdataList);
        }
    }
    private void parseWidgetListRecursively(List<FormConfigWrapper.LFWidget> widgetList, Long confId, Long formDataId, List<BpmnConfLfFormdataField> result){
        for (FormConfigWrapper.LFWidget lfWidget : widgetList) {
            if(!StringConstants.LOWFLOW_FORM_CONTAINER_TYPE.equals(lfWidget.getCategory())){
                FormConfigWrapper.LFWidget.LFOption lfOption = lfWidget.getOptions();
                BpmnConfLfFormdataField formdataField=new BpmnConfLfFormdataField();
                formdataField.setBpmnConfId(confId);
                formdataField.setFormDataId(formDataId);
                formdataField.setFieldType(getFieldTypeByTypeString(lfWidget.getType()));
                formdataField.setFieldId(lfOption.getName());
                formdataField.setFieldName(lfOption.getLabel());
                result.add(formdataField);
            }else{//走到这里一定是容器类型
                String containerType = lfWidget.getType();
                VariantFormContainerTypeEnum containerTypeEnum = VariantFormContainerTypeEnum.getByTypeName(containerType);
                if(containerTypeEnum==null){
                    continue; //未定义低代码表单字段类型，直接跳过
                }
                if(VariantFormContainerTypeEnum.CARD.equals(containerTypeEnum)
                        || VariantFormContainerTypeEnum.SUB_FORM.equals(containerTypeEnum)
                        || VariantFormContainerTypeEnum.TABLE_SUB_FORM.equals(containerTypeEnum)){
                    List<FormConfigWrapper.LFWidget> subWidgetList = lfWidget.getWidgetList();
                    if(!CollectionUtils.isEmpty(subWidgetList)){
                        parseWidgetListRecursively(subWidgetList,confId,formDataId,result);
                    }
                }else if(VariantFormContainerTypeEnum.TAB.equals(containerTypeEnum)){
                    List<FormConfigWrapper.LFWidget> tabs = lfWidget.getTabs();
                    for (FormConfigWrapper.LFWidget tab : tabs) {
                        List<FormConfigWrapper.LFWidget> subWidgetList = tab.getWidgetList();
                        parseWidgetListRecursively(subWidgetList,confId,formDataId,result);
                    }
                }else{

                    List<FormConfigWrapper.TableRow> rows = lfWidget.getRows();
                    if(!CollectionUtils.isEmpty(rows)){//table
                        for (FormConfigWrapper.TableRow row : lfWidget.getRows()) {
                            List<FormConfigWrapper.LFWidget> cols = row.getCols();
                            for (FormConfigWrapper.LFWidget col : cols) {
                                List<FormConfigWrapper.LFWidget> subWidgetList = col.getWidgetList();
                                if(CollectionUtils.isEmpty(subWidgetList)){
                                    continue;
                                }
                                parseWidgetListRecursively(subWidgetList,confId,formDataId,result);
                            }
                        }
                    }else{
                        //grid has no rows,only cols
                        List<FormConfigWrapper.LFWidget> cols = lfWidget.getCols();
                        for (FormConfigWrapper.LFWidget col : cols) {
                            List<FormConfigWrapper.LFWidget> subWidgetList = col.getWidgetList();
                            if(CollectionUtils.isEmpty(subWidgetList)){
                                continue;
                            }
                            parseWidgetListRecursively(subWidgetList,confId,formDataId,result);
                        }
                    }
                }

            }
        }
    }
    private int  getFieldTypeByTypeString(String typeString) {
        switch (typeString) {
            // NUMBER
            case "number":
            case "slider":
                return LFFieldTypeEnum.NUMBER.getType();
            // DATE
            case "date":
                return LFFieldTypeEnum.DATE.getType();
            // DATE_TIME
            case "date-range":
            case "time":
            case "time-range":
                return LFFieldTypeEnum.DATE_TIME.getType();
            // BOOLEAN
            case "switch":
                return LFFieldTypeEnum.BOOLEAN.getType();
            // TEXT (long text)
            case "textarea":
            case "richtext-editor":
                return LFFieldTypeEnum.TEXT.getType();
            // STRING (short text) - default for most form fields
            case "select":
            case "radio":
            case "checkbox":
            case "cascader":
            case "tree-select":
            case "color-picker":
            case "rate":
            case "input":
            case "number-range":
            case "picture-upload":
            case "file-upload":
            case "icon-picker":
            case "transfer":
                return LFFieldTypeEnum.STRING.getType();
            default:
                return LFFieldTypeEnum.STRING.getType();
        }
    }
    @Override
    public int order() {
        return 0;
    }
}
