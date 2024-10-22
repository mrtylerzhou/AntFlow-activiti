package org.openoa.base.aspect;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.entity.MethodReplayEntity;
import org.openoa.base.interf.MethodReplay;
import org.openoa.base.mapper.MethodReplayMapper;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    @Autowired
    private MethodReplayMapper methodReplayMapper;
    @Value("${methodreplay.on:true}")
    private boolean methodReplayOn;

    @Around("@annotation(methodReplay)")
    public Object aroundMethodExecution(ProceedingJoinPoint joinPoint, MethodReplay methodReplay) throws Throwable {
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {

           if(methodReplayOn){
               // 如果抓取到异常就记录 类全路径 方法名 参数 异常信息 当前时间 全都存表

               // 获取类全路径
               String className = joinPoint.getTarget().getClass().getName();
               if(Proxy.isProxyClass(joinPoint.getTarget().getClass())){
                   Class<?>[] classes = AopProxyUtils.proxiedUserInterfaces(joinPoint.getTarget());
                   if(classes.length>0){
                       className=classes[0].getName();
                   }
               }else if(AopUtils.isCglibProxy(joinPoint.getTarget())){
                   className=ClassUtils.getUserClass(joinPoint.getTarget()).getName();
               }
               // 获取方法名
               String methodName = joinPoint.getSignature().getName();
               // 获取参数类型
               MethodSignature signature = (MethodSignature) joinPoint.getSignature();
               Method method = signature.getMethod();
               Class<?>[] parameterTypes = method.getParameterTypes();
               // 获取参数
               Object[] args = joinPoint.getArgs();
               // 打印类全路径、方法名和参数
               log.info("MethodReplay接受到异常,Class name:{},Method name:{},parameterTypes:{}, Arguments:{}", className, methodName, JSON.toJSONString(parameterTypes), JSON.toJSONString(args));
               // 入库
               MethodReplayEntity methodReplayEntity = new MethodReplayEntity();
               methodReplayEntity.setProjectName(StringConstants.PROJECT_NAME);
               methodReplayEntity.setClassName(className);
               methodReplayEntity.setMethodName(methodName);
               methodReplayEntity.setParamType(JSON.toJSONString(parameterTypes));
               methodReplayEntity.setArgs(JSON.toJSONString(args));
               String message=e.getMessage();
               if(message.length()>100){
                   message=message.substring(0,100);
               }
               methodReplayEntity.setErrorMsg(message);
               methodReplayEntity.setAlreadyReplayTimes(0);
               methodReplayEntity.setMaxReplayTimes(methodReplay.maxReplayTimes());
               String sum=StringConstants.PROJECT_NAME + className + methodName + methodReplayEntity.getParamType() + methodReplayEntity.getArgs();
               CRC32 crc32=new CRC32();
               crc32.update(sum.getBytes(StandardCharsets.UTF_8));
               methodReplayEntity.setId(crc32.getValue());
               MethodReplayEntity exist = methodReplayMapper.selectById(methodReplayEntity);
               if (exist == null) {
                   methodReplayMapper.insert(methodReplayEntity);
               }
           }
            throw e;
        }
        return result;
    }


}
