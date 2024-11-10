package org.openoa.engine.lowflow.service;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Strings;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.service.AntFlowOrderPreProcessor;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdata;
import org.openoa.engine.bpmnconf.confentity.BpmnConfLfFormdataField;
import org.openoa.engine.bpmnconf.controller.BpmnConfController;
import org.openoa.engine.bpmnconf.service.BpmnConfLfFormdataFieldServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.BpmnConfLfFormdataServiceImpl;
import org.openoa.base.vo.FormConfigWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class LFFormDataPreProcessor implements AntFlowOrderPreProcessor<BpmnConfVo> {
    @Autowired
    private BpmnConfLfFormdataServiceImpl lfFormdataService;
    @Autowired
    private BpmnConfLfFormdataFieldServiceImpl lfFormdataFieldService;


    @Override
    public void preProcess(BpmnConfVo confVo) {
        if(confVo==null){
            return;
        }
        Integer isLowCodeFlow = confVo.getIsLowCodeFlow();
        boolean lowCodeFlowFlag=isLowCodeFlow!=null&&isLowCodeFlow==1;
        if(!lowCodeFlowFlag){
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
        FormConfigWrapper formConfigWrapper = JSON.parseObject(lfForm, FormConfigWrapper.class);
        List<FormConfigWrapper.LFWidget> lfWidgetList = formConfigWrapper.getWidgetList();
        if(CollectionUtils.isEmpty(lfWidgetList)){
            throw new JiMuBizException(Strings.lenientFormat("lowcode form has no widget,confId:%d,formCode:%s",confId,confVo.getFormCode()));
        }
        List<BpmnConfLfFormdataField> formdataFields=new ArrayList<>();
        parseWidgetListRecursively(lfWidgetList,confId,lfFormdata.getId(),formdataFields);
        if(CollectionUtils.isEmpty(formdataFields)){
            throw new JiMuBizException(Strings.lenientFormat("lowcode form fields can not be empty,confId:%d,formCode:%s",confId,confVo.getFormCode()));
        }
        lfFormdataFieldService.saveBatch(formdataFields);
    }

    private void parseWidgetListRecursively(List<FormConfigWrapper.LFWidget> widgetList,Long confId,Long formDataId,List<BpmnConfLfFormdataField> result){
        for (FormConfigWrapper.LFWidget lfWidget : widgetList) {
            if(!StringConstants.LOWFLOW_FORM_CONTAINER_TYPE.equals(lfWidget.getCategory())){
                FormConfigWrapper.LFWidget.LFOption lfOption = lfWidget.getOptions();
                BpmnConfLfFormdataField formdataField=new BpmnConfLfFormdataField();
                formdataField.setBpmnConfId(confId);
                formdataField.setFormDataId(formDataId);
                //todo lf field type
                formdataField.setFieldId(lfWidget.getId());
                formdataField.setFieldName(lfOption.getName());
                result.add(formdataField);
            }else{
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
            }
        }
    }

    @Override
    public int order() {
        return 0;
    }
}
