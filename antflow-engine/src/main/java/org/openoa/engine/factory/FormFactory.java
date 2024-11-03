package org.openoa.engine.factory;

import com.alibaba.fastjson2.JSON;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.confentity.OutSideBpmAccessBusiness;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmAccessBusinessServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @Classname FormFactory
 * @Description TODO
 * @Date 2021-11-09 6:58
 * @Created by AntOffice
 */
@Component
public class FormFactory implements ApplicationContextAware {
    @Autowired
    private IAdaptorFactory adaptorFactory;
    @Autowired
    private OutSideBpmAccessBusinessServiceImpl outSideBpmAccessBusinessService;
    private ApplicationContext applicationContext;

    public FormOperationAdaptor getFormAdaptor(String formCode){
        return getFormAdaptor(BusinessDataVo.builder().formCode(formCode).build());
    }
    public FormOperationAdaptor getFormAdaptor(BusinessDataVo vo) {
        if (ObjectUtils.isEmpty(vo)) {
            return null;
        }
        ActivitiService activitiService = adaptorFactory.getActivitiService(vo);
        if (ObjectUtils.isEmpty(activitiService)) {
            throw new JiMuBizException("form code does not have a processing bean！");
        }
        return (FormOperationAdaptor) activitiService;
    }

    /**
     *
     * @param params the request body string
     * @param formCode if caller can't provide,pass null
     * @return
     */
    public BusinessDataVo dataFormConversion(String params,String formCode) {
        BusinessDataVo vo = JSON.parseObject(params, BusinessDataVo.class);
        if(formCode==null){
            formCode=vo.getFormCode();
        }
        if(vo.getIsOutSideAccessProc()){
            LambdaQueryWrapper<OutSideBpmAccessBusiness> qryWrapper = Wrappers
                    .<OutSideBpmAccessBusiness>lambdaQuery()
                    .eq(OutSideBpmAccessBusiness::getProcessNumber, vo.getProcessNumber());
            List<OutSideBpmAccessBusiness> bpmAccessBusinesses = outSideBpmAccessBusinessService.list(qryWrapper);
            if(!CollectionUtils.isEmpty(bpmAccessBusinesses)){
                vo.setFormData(bpmAccessBusinesses.get(0).getFormDataPc());
            }
            return vo;
        }
        Object bean = applicationContext.getBean(formCode);
        if (ObjectUtils.isEmpty(bean)) {
            if(Boolean.TRUE.equals(vo.getIsLowCodeFlow())){
                bean= applicationContext.getBean(StringConstants.LOWFLOW_FORM_CODE);
            }
            throw new JiMuBizException("can not get the processing bean by form code:{}!"+formCode);
        }
        return JSON.parseObject(params, (Type) getFormTClass(formCode));

    }

    private Class<?> getFormTClass(String key) {
        FormOperationAdaptor bean = getFormAdaptor(BusinessDataVo.builder().formCode(key).build());
        if (!ObjectUtils.isEmpty(bean)) {
            Type[] interfacesTypes = bean.getClass().getGenericInterfaces();
            ParameterizedType p = (ParameterizedType) interfacesTypes[0];
            Class<?> cls = (Class) p.getActualTypeArguments()[0];
            if (!ObjectUtils.isEmpty(cls)) {
                return cls;
            }
        }
        throw new JiMuBizException("该表单未关联业务实现类或未关联实现类泛型！");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
