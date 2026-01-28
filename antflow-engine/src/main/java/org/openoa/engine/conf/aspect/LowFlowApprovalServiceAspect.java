package org.openoa.engine.conf.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.base.interf.LFFormOperationAdaptor;
import org.openoa.base.vo.UDLFApplyVo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@Component
public class LowFlowApprovalServiceAspect implements ApplicationContextAware {
    @Autowired(required = false)
    private List<LFFormOperationAdaptor> lfFormOperationAdaptors;
    private ApplicationContext applicationContext;

    @Around("execution(* org.openoa.engine.lowflow.service.LowFlowApprovalService.previewSetCondition(..))")
    public Object aroundPreviewSetCondition(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAndProceed(joinPoint, "previewSetCondition");
    }

    @Around("execution(* org.openoa.engine.lowflow.service.LowFlowApprovalService.initData(..))")
    public Object aroundInitData(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAndProceed(joinPoint, "initData");
    }

    @Around("execution(* org.openoa.engine.lowflow.service.LowFlowApprovalService.launchParameters(..))")
    public Object aroundLaunchParameters(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAndProceed(joinPoint, "launchParameters");
    }

    @Around("execution(* org.openoa.engine.lowflow.service.LowFlowApprovalService.queryData(..))")
    public Object aroundQueryData(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAndProceed(joinPoint, "queryData");
    }

    @Around("execution(* org.openoa.engine.lowflow.service.LowFlowApprovalService.submitData(..))")
    public Object aroundSubmitData(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAndProceed(joinPoint, "submitData");
    }

    @Around("execution(* org.openoa.engine.lowflow.service.LowFlowApprovalService.consentData(..))")
    public Object aroundConsentData(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAndProceed(joinPoint, "consentData");
    }

    @Around("execution(* org.openoa.engine.lowflow.service.LowFlowApprovalService.backToModifyData(..))")
    public Object aroundBackToModifyData(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAndProceed(joinPoint, "backToModifyData");
    }

    @Around("execution(* org.openoa.engine.lowflow.service.LowFlowApprovalService.cancellationData(..))")
    public Object aroundCancellationData(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAndProceed(joinPoint, "cancellationData");
    }
    @Around("execution(* org.openoa.engine.lowflow.service.LowFlowApprovalService.finishData(..))")
    public Object aroundFinishData(ProceedingJoinPoint joinPoint) throws Throwable {
        return logAndProceed(joinPoint, "finishData");
    }




    private Object logAndProceed(ProceedingJoinPoint joinPoint, String methodName) throws Throwable {
        log.info("[LowFlowAOP] >>> 调用方法: {}", methodName);
        long start = System.currentTimeMillis();
        try {
            Object[] args = joinPoint.getArgs();
            if(args.length>1){
                throw new AFBizException("切面方法错误,请联系管理员!");
            }
            UDLFApplyVo arg = null;
            String formCode="";
            if(!"finishData".equals(methodName)){
                arg=(UDLFApplyVo)args[0];
                formCode=arg.formCode;
            }else{
                formCode=((BusinessDataVo)args[0]).formCode;
            }
            if(CollectionUtils.isEmpty(lfFormOperationAdaptors)){
                lfFormOperationAdaptors=new ArrayList<>();
            }

            for (LFFormOperationAdaptor lfFormOperationAdaptor : lfFormOperationAdaptors) {
                String[] beanNamesForType = applicationContext.getBeanNamesForType(lfFormOperationAdaptor.getClass());
                if(beanNamesForType.length == 0){
                    continue;
                }
                String beanName=beanNamesForType[0];
                if(!formCode.equals(beanName)){
                    continue;
                }
                if ("queryData".equals(methodName)) {
                    // 查询前处理
                    lfFormOperationAdaptor.queryData(arg);
                }

                if ("submitData".equals(methodName)) {
                    // 提交前处理
                   lfFormOperationAdaptor.submitData(arg);
                }

                if ("consentData".equals(methodName)) {
                    // 同意前处理
                    lfFormOperationAdaptor.consentData(arg);
                }

                if ("initData".equals(methodName)) {
                    // 初始化数据前处理
                   lfFormOperationAdaptor.initData(arg);
                }

                if ("previewSetCondition".equals(methodName)) {
                    // 条件预览前处理
                    lfFormOperationAdaptor.previewSetCondition(arg);
                }

                if ("launchParameters".equals(methodName)) {
                    // 启动参数前处理
                    lfFormOperationAdaptor.launchParameters(arg);
                }

                if ("backToModifyData".equals(methodName)) {
                    // 退回前处理
                    lfFormOperationAdaptor.backToModifyData(arg);
                }

                if ("cancellationData".equals(methodName)) {
                    // 作废前处理
                    lfFormOperationAdaptor.cancellationData(arg);
                }
                if("finishData".equals(methodName)){
                    lfFormOperationAdaptor.finishData((BusinessDataVo)args[0]);
                }

            }
            Object result = joinPoint.proceed();
            log.info("[LowFlowAOP] <<< 方法 {} 执行完成，耗时 {}ms", methodName, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable t) {
            log.error("[LowFlowAOP] !!! 方法 {} 抛出异常: {}", methodName, t.getMessage(), t);
            throw t;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
