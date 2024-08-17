package org.openoa.engine.conf.mvc;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.entity.Result;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@ResponseBody
@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Throwable.class)
    public Object handler(HttpServletRequest req, Throwable throwable) {
        log.error("全局异常捕获: ", throwable);

        if (log.isTraceEnabled()) {
            log.trace(req.getRequestURL().toString(), throwable);
        }



        if (throwable instanceof HttpRequestMethodNotSupportedException) {
            HttpRequestMethodNotSupportedException ex = (HttpRequestMethodNotSupportedException) throwable;
            return buildResponseEntity(405, "请求方法不支持" + ex.getMethod(),throwable);
        }



        if (throwable instanceof HttpMediaTypeException) {
            HttpMediaTypeException ex = (HttpMediaTypeException) throwable;
            if (ex.getSupportedMediaTypes().isEmpty()) {
                return buildResponseEntity(406, ex.getMessage(),throwable);
            } else {
                return buildResponseEntity(406, "支持的Content-Type:" + JSON.toJSONString(ex.getSupportedMediaTypes().stream().map(MediaType::toString).collect(Collectors.toSet())),throwable);
            }
        }
        if (throwable instanceof NoHandlerFoundException) {
            return buildResponseEntity(404, "请求地址不存在!",throwable);
        }
        // 请求对象中字段校验失败报错捕获
        if (throwable instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) throwable;
            List<String> validMsgs = new ArrayList<>();
            for (ObjectError error : e.getBindingResult().getAllErrors()) {
                validMsgs.add(error.getDefaultMessage());
            }
            return buildResponseEntity(400, StringUtils.join(validMsgs, ","),throwable);
        }

        // 参数格式转换异常
        if (throwable instanceof JSONException
                || throwable instanceof BindException
                || throwable instanceof IllegalArgumentException
                || throwable instanceof HttpMessageConversionException
                || throwable instanceof MethodArgumentTypeMismatchException) {
            return buildResponseEntity(400, "参数错误，请检查你的入参 : " + throwable.getMessage(),throwable);
        }

        if ((throwable instanceof IOException
                && throwable.getMessage() != null
                && throwable.getMessage().toLowerCase().contains("broken pipe"))
                || (throwable instanceof RuntimeException
                && throwable.getCause() != null
                && throwable.getCause().getMessage() != null
                && throwable.getCause().getMessage().toLowerCase().contains("broken pipe"))) {
            return buildResponseEntity(999999, "您终止了该操作，如需要请重试",throwable);
        }

        //字段超长捕获
        if (throwable instanceof DataIntegrityViolationException) {

            DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException) throwable;
            if (dataIntegrityViolationException.getCause() instanceof MysqlDataTruncation) {
                MysqlDataTruncation cause = (MysqlDataTruncation) dataIntegrityViolationException.getCause();
                int errorCode = cause.getErrorCode();
                if (errorCode == 1406) {
                    String message = cause.getMessage();
                    String fieldName = StringUtils.EMPTY;
                    if (message.matches("(.*)'(.*).*(.*)'(.*)")) {
                        fieldName = message.substring(message.indexOf("'") + 1, message.lastIndexOf("'"));
                    }
                    return buildResponseEntity(999999, StringUtils.join("[", fieldName, "]字段", "内容有点长哦"),throwable);
                }
            }
        }

        // 业务异常捕获
        if (throwable instanceof JiMuBizException) {
            JiMuBizException e = (JiMuBizException) throwable;
            return buildResponseEntity(0, Integer.parseInt(e.getCode()), e.getMessage(), "");
        }

        /***********Log分割线***********/
        log.error(req.getRequestURL().toString(), throwable);

        // 流程异常
        if (throwable instanceof ActivitiObjectNotFoundException) {
            return buildResponseEntity(404, "流程数据不在了，请重试",throwable);
        }

        // 其他异常
        return buildResponseEntity(999999, "服务在开小差，请稍等",throwable);
    }

    private static ResponseEntity<Result> buildResponseEntity(int code, String msg,Throwable throwable) {

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(Result.newFailureResult(String.valueOf(code),msg,false,throwable));    }

    private static ResponseEntity<Result> buildResponseEntity(int type, int code, String msg, String subMsg) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(Result.newFailureResult(String.valueOf(code),msg));
    }


}
