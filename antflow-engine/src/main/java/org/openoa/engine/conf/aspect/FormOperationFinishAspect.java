package org.openoa.engine.conf.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.openoa.base.vo.BusinessDataVo;
import org.openoa.engine.bpmnconf.activitilistener.WorkflowButtonOperationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class FormOperationFinishAspect {
    @Autowired
    private WorkflowButtonOperationHandler workflowButtonHandler;

    /**
     * 对所有实现了 FormOperationAdaptor 接口的类的 finishData 方法做切面
     */
    @Around("execution(* org.openoa.base.interf.ProcessFinishListener+.finishData(..))")
    public Object aroundFinishData(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Object[] args = joinPoint.getArgs();
        if (args.length != 1 || !(args[0] instanceof BusinessDataVo)) {
            log.error("[FormOperationFinishAspect] 参数不合法，类名：{} 方法：{}", className, methodName);
            throw new IllegalArgumentException("finishData 参数非法，必须是 BusinessDataVo 类型");
        }
        workflowButtonHandler.onFinishData((BusinessDataVo) args[0]);
        log.info("[FormOperationFinishAspect] >>> {}.{} 开始执行", className, methodName);
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            log.info("[FormOperationFinishAspect] <<< {}.{} 执行完成，耗时 {} ms", className, methodName, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable t) {
            log.error("[FormOperationFinishAspect] !!! {}.{} 执行异常: {}", className, methodName, t.getMessage(), t);
            throw t;
        }
    }
}
