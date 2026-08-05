package org.openoa.engine.factory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.interf.ActivitiService;
import org.openoa.base.interf.FormOperationAdaptor;
import org.openoa.base.util.AfTypeUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.vo.UDLFApplyVo;
import org.openoa.base.entity.OutSideBpmAccessBusiness;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmAccessBusinessServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @Lazy
    private OutSideBpmAccessBusinessServiceImpl outSideBpmAccessBusinessService;
    private ApplicationContext applicationContext;

    /**
     * UDLFApplyVo(含父类 BusinessDataVo/PageDto)的属性名集合,用于识别"扁平业务字段"。
     * 顶层 JSON key 命中此集合的视为引擎控制/结构字段(不折进 lfFields),其余视为自定义表单业务字段。
     *
     * 双性 special-case: {@code remark} 被显式排除。
     * remark 既是 UDLFApplyVo 属性(被 LowFlowApprovalService.submitData 用作 processDigest 来源),
     * 又是"备注"表单字段的最自然命名。排除后 remark 既折进 lfFields(存表+往返回显),
     * 又因仍是 UDLFApplyVo 真实属性、会被 target parse 映射到 vo.remark(喂 digest),两全。
     * 前提: 辅助 vform 须声明 field_id=remark 的 STRING 字段,否则 parseFromMap 因无 fieldConfig 静默跳过。
     */
    private static final Set<String> RESERVED_PROPS_UDLF = collectReservedProps();

    private static Set<String> collectReservedProps() {
        Set<String> names = new HashSet<>();
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(UDLFApplyVo.class).getPropertyDescriptors()) {
                names.add(pd.getName());
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException("init RESERVED_PROPS_UDLF failed", e);
        }
        names.remove("remark"); // 双性: remark 既要喂 digest 又要存表
        return Collections.unmodifiableSet(names);
    }

    /**
     * page-added DIY 辅助: 把顶层"扁平业务字段"(非 UDLFApplyVo 保留属性)折进 lfFields Map,
     * 使自定义 Vue 表单(直接发扁平顶层字段)的数据能被 LowFlowApprovalService 按 lfFields 存储。
     *
     * 仅在 isLowCodeFlow==1 时调用。纯 LF 前端发嵌套 lfFields、无顶层扁平字段 → extra 为空 → 原样返回(no-op)。
     * 合并优先级: 扁平字段覆盖嵌套(扁平是自定义表单的显式数据源)。
     * 覆盖调用点: 发起/重提/审批(DoButtonOperationAspect)、查看(getBusinessInfo)、草稿加载、conf 编辑。
     */
    @SuppressWarnings("unchecked")
    private String foldFlatFieldsIntoLfFields(String params) {
        JSONObject jsonObj = JSON.parseObject(params);
        Map<String, Object> extra = new LinkedHashMap<>();
        for (String key : jsonObj.keySet()) {
            if (!RESERVED_PROPS_UDLF.contains(key)) {
                extra.put(key, jsonObj.get(key));
            }
        }
        if (extra.isEmpty()) {
            return params; // 纯 LF: 无扁平业务字段,原样返回
        }
        Map<String, Object> merged = new LinkedHashMap<>();
        Object existing = jsonObj.get("lfFields");
        if (existing instanceof Map) {
            for (Map.Entry<String, Object> e : ((Map<String, Object>) existing).entrySet()) {
                merged.put(e.getKey(), e.getValue());
            }
        }
        merged.putAll(extra); // 扁平覆盖嵌套
        jsonObj.put("lfFields", merged);
        return jsonObj.toJSONString();
    }

    public FormOperationAdaptor getFormAdaptor(String formCode){
        return getFormAdaptor(BusinessDataVo.builder().formCode(formCode).build());
    }
    public FormOperationAdaptor getFormAdaptor(BusinessDataVo vo) {
        if (ObjectUtils.isEmpty(vo)) {
            return null;
        }
        ActivitiService activitiService = adaptorFactory.getActivitiService(vo);
        if (ObjectUtils.isEmpty(activitiService)) {
            throw new AFBizException("form code does not have a processing bean！");
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

        }
        if(vo.getIsLowCodeFlow()!=null&&vo.getIsLowCodeFlow()==1){
            formCode=StringConstants.LOWFLOW_FORM_CODE;
            // page-added DIY: 自定义表单发扁平顶层字段,这里折进 lfFields 供 LowFlowApprovalService 存储。
            // 纯 LF 无扁平字段时 no-op。仅 String 重载需要(4 个调用点都走此重载)。
            params = foldFlatFieldsIntoLfFields(params);
        }
        Object bean = applicationContext.getBean(formCode);
        if (ObjectUtils.isEmpty(bean)) {
            throw new AFBizException("can not get the processing bean by form code:{}!"+formCode);
        }
        return JSON.parseObject(params, (Type) getFormTClass(formCode));
    }
    public BusinessDataVo dataFormConversion(BusinessDataVo vo) {
        String formCode=vo.getFormCode();
        if(vo.getIsOutSideAccessProc()){
            LambdaQueryWrapper<OutSideBpmAccessBusiness> qryWrapper = Wrappers
                    .<OutSideBpmAccessBusiness>lambdaQuery()
                    .eq(OutSideBpmAccessBusiness::getProcessNumber, vo.getProcessNumber());
            List<OutSideBpmAccessBusiness> bpmAccessBusinesses = outSideBpmAccessBusinessService.list(qryWrapper);
            if(!CollectionUtils.isEmpty(bpmAccessBusinesses)){
                vo.setFormData(bpmAccessBusinesses.get(0).getFormDataPc());
            }

        }
        if(vo.getIsLowCodeFlow()!=null&&vo.getIsLowCodeFlow()==1){
            formCode=StringConstants.LOWFLOW_FORM_CODE;
        }
        Object bean = applicationContext.getBean(formCode);
        if (ObjectUtils.isEmpty(bean)) {
            throw new AFBizException("can not get the processing bean by form code:{}!"+formCode);
        }
        Class<? extends BusinessDataVo> actualClass= (Class<? extends BusinessDataVo>) getFormTClass(formCode);
        try {
            BusinessDataVo businessDataVo = actualClass.newInstance();
            BeanUtils.copyProperties(vo,businessDataVo);
            return businessDataVo;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private Class<?> getFormTClass(String key) {
        FormOperationAdaptor bean = getFormAdaptor(BusinessDataVo.builder().formCode(key).build());
        if (!ObjectUtils.isEmpty(bean)) {
            ParameterizedType p=null;
            Set<ResolvableType> allTypes = AfTypeUtils.getAllTypes(ResolvableType.forClass(ClassUtils.getUserClass(bean)));
            for (ResolvableType rType : allTypes) {
                if (rType.getType() instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) rType.getType();
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if(FormOperationAdaptor.class.isAssignableFrom(rType.resolve())){
                        for (Type actualTypeArgument : actualTypeArguments) {
                            if(actualTypeArgument instanceof Class){
                                if (BusinessDataVo.class.isAssignableFrom((Class<?>) actualTypeArgument)) {
                                    p=(ParameterizedType)rType.getType();
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            Class<?> cls = (Class) p.getActualTypeArguments()[0];
            if (!ObjectUtils.isEmpty(cls)) {
                return cls;
            }
        }
        throw new AFBizException("该表单未关联业务实现类或未关联实现类泛型！");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
