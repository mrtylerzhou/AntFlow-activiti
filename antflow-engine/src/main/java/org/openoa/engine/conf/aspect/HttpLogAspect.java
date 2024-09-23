package org.openoa.engine.conf.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openoa.base.constant.enums.OpLogFlagEnum;
import org.openoa.engine.bpmnconf.confentity.OpLog;
import org.openoa.base.vo.SignatureRequest;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.engine.bpmnconf.service.impl.OpLogServiceImpl;
import org.openoa.base.util.EnvUtil;
import org.openoa.base.util.JimuJsonUtil;
import org.openoa.base.util.MDCLogUtil;
import org.openoa.base.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Aspect
@Component
public class HttpLogAspect {

    @Autowired
    private OpLogServiceImpl opLogServiceImpl;



    @Pointcut("@within(org.springframework.stereotype.Controller) ||@within(org.springframework.web.bind.annotation.RestController)&&!@annotation(org.openoa.base.interf.anno.IgnoreLog)&&!@within(org.openoa.base.interf.anno.IgnoreLog)&&within(org.openoa..*)")
    public void httpAspect() {
    }

    @Around("httpAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        OpLogFlagEnum flagEnum = OpLogFlagEnum.SUCCESS;
        Object resp = null;
        try {
            return resp = joinPoint.proceed();
        } catch (Throwable throwable) {
            flagEnum = throwable instanceof JiMuBizException ? OpLogFlagEnum.BusinessException : OpLogFlagEnum.FAILURE;
            resp = throwable.getClass().getSimpleName() + " : " + throwable.getMessage();
            throw throwable;
        } finally {
            String reqStr = "@IgnoreLog";
            String respStr = "@IgnoreLog";
            try {

                    String className = joinPoint.getTarget().getClass().getName();
                    String methodName = joinPoint.getSignature().getName();
                    if (!isIgnoreLog(joinPoint)) {
                        reqStr = JimuJsonUtil.toJsonString(checkArgs(joinPoint.getArgs()));
                        respStr = JimuJsonUtil.toJsonString(resp);
                    }
                    long useTime = System.currentTimeMillis() - startTime;
                    SignatureRequest signatureReq = EnvUtil.getSignatureReq();
                    OpLog opLog = OpLog.builder()
                            .msgId(MDCLogUtil.getLogId())
                            .opFlag(flagEnum.getCode())
                            .opUserName(SecurityUtils.getLogInEmpNameSafe())
                            .opMethod(String.format("%s_%s", className, methodName))
                            .opParam(reqStr)
                            .opResult(respStr)
                            .systemType(signatureReq.getSystemType())
                            .systemVersion(signatureReq.getSystemVersion())
                            .appVersion(signatureReq.getAppVersion())
                            .hardware(signatureReq.getHardware())
                            .remark("操作日志")
                            .opTime(new Date())
                            .opUseTime(useTime)
                            .build();
                    opLogServiceImpl.asyncInsert(opLog);

            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    /**
     * //todo 待实现,后面结合注册中心实现
     * @param joinPoint
     * @return
     */
    private static boolean isIgnoreLog(ProceedingJoinPoint joinPoint){
    return false;
    }
    private static Object[] checkArgs(Object... args) {
        Object[] result = Arrays.copyOf(args, args.length);
        for (int i = 0; i < result.length; i++) {
            if (result[i] instanceof ServletRequest) {
                result[i] = "req";
            }
            if (result[i] instanceof ServletResponse) {
                result[i] = "resp";
            }
            if (result[i] instanceof InputStreamSource) {
                result[i] = "stream";
            }
        }
        return result;
    }

    /**
     * 报错后处理
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "httpAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {

        OpLogFlagEnum flagEnum = e instanceof JiMuBizException ? OpLogFlagEnum.BusinessException : OpLogFlagEnum.FAILURE;

        //非业务报错则发送通知邮件
        if (flagEnum.equals(OpLogFlagEnum.FAILURE)) {
           //todo 待实现,如果失败可以做一些动作
        }
    }

}