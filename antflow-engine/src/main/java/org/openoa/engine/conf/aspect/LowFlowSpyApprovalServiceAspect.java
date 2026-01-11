package org.openoa.engine.conf.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.exception.BusinessErrorEnum;
import org.openoa.base.vo.BpmnStartConditionsVo;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.service.interf.repository.BpmnNodeConditionsConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class LowFlowSpyApprovalServiceAspect {

    @Autowired
    BpmnNodeConditionsConfService bpmnNodeConditionsConfService;

    @Around("execution(* org.openoa.engine.bpmnconf.adp.processoperation.AbstractLowFlowSpyFormOperationAdaptor+.previewSetCondition(..))")
    public Object aroundPreviewSetCondition(ProceedingJoinPoint pjp) throws Throwable {
        BusinessDataVo businessDataVo = transFormArg(pjp);
        BpmnStartConditionsVo result =(BpmnStartConditionsVo) pjp.proceed();
        try{
            List<String> fieldNames = bpmnNodeConditionsConfService.queryConditionParamNameByProcessNumber(businessDataVo);
            Map<String,Object> conditions=formConditions(pjp.getArgs()[0],fieldNames);
            result.setLfConditions(conditions);
        }catch (Throwable t){
            log.error("查询条件参数处理失败",t);
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"查询条件参数处理失败,请联系管理员!");
        }
        return result;
    }

    @Around("execution(* org.openoa.engine.bpmnconf.adp.processoperation.AbstractLowFlowSpyFormOperationAdaptor+.launchParameters(..))")
    public Object aroundInitData(ProceedingJoinPoint pjp) throws Throwable {
        BusinessDataVo businessDataVo = transFormArg(pjp);
        BpmnStartConditionsVo result =(BpmnStartConditionsVo) pjp.proceed();
        try{
            List<String> fieldNames = bpmnNodeConditionsConfService.queryConditionParamNameByProcessNumber(businessDataVo);
            Map<String,Object> conditions=formConditions(pjp.getArgs()[0],fieldNames);
            result.setLfConditions(conditions);
        }catch (Throwable t){
            log.error("查询条件参数处理失败",t);
            throw new AFBizException(BusinessErrorEnum.STATUS_ERROR.getCodeStr(),"查询条件参数处理失败,请联系管理员!");
        }
        return result;
    }
    private BusinessDataVo  transFormArg(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        if(args.length>1){
            throw new AFBizException("切面方法错误,请联系管理员!");
        }
        return (BusinessDataVo) args[0];
    }
    private Map<String,Object> formConditions(Object businessDataVo, List<String> fieldNames) throws Throwable {
        if(CollectionUtils.isEmpty(fieldNames)){
            return null;
        }
        Map<String,Object> conditions=new HashMap<>();
        Class<?> clazz = businessDataVo.getClass();
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (String fieldName : fieldNames) {
            Field field = clazz.getDeclaredField(fieldName);
            Class<?> fieldType = field.getType();
            MethodHandle getter =
                    lookup.findGetter(clazz, fieldName, fieldType);

            Object fieldValue = getter.invokeExact(businessDataVo);
            conditions.put(fieldName,fieldValue);
        }
        return conditions;
    }
}
