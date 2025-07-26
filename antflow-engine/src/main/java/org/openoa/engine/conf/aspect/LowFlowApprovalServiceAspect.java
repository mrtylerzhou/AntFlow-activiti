package org.openoa.engine.conf.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.engine.lowflow.service.LFFormOperationAdaptor;
import org.openoa.engine.lowflow.vo.UDLFApplyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
public class LowFlowApprovalServiceAspect {
    @Autowired(required = false)
    private List<LFFormOperationAdaptor> lfFormOperationAdaptors;

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




    private Object logAndProceed(ProceedingJoinPoint joinPoint, String methodName) throws Throwable {
        log.info("[LowFlowAOP] >>> 调用方法: {}", methodName);
        long start = System.currentTimeMillis();
        try {
            Object[] args = joinPoint.getArgs();
            if(args.length>1){
                throw new JiMuBizException("切面方法错误,请联系管理员!");
            }
            UDLFApplyVo arg = (UDLFApplyVo)args[0];
            for (LFFormOperationAdaptor lfFormOperationAdaptor : lfFormOperationAdaptors) {
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

            }
            Object result = joinPoint.proceed();
            log.info("[LowFlowAOP] <<< 方法 {} 执行完成，耗时 {}ms", methodName, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable t) {
            log.error("[LowFlowAOP] !!! 方法 {} 抛出异常: {}", methodName, t.getMessage(), t);
            throw t;
        }
    }
}
