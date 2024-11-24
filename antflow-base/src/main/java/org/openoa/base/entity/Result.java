package org.openoa.base.entity;

import org.openoa.base.util.JiMuCommonUtils;
import org.openoa.base.util.MDCLogUtil;

public class Result<T> {
    private String requestId;
    private Integer code;
    private T data;
    private boolean success;
    private boolean needRetry;
    private String expInfo;
    private String errCode;
    private String errMsg;

    public Result() {
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getErrMsg() {
        return this.errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public boolean isNeedRetry() {
        return this.needRetry;
    }

    public void setNeedRetry(boolean needRetry) {
        this.needRetry = needRetry;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getExpInfo() {
        return this.expInfo;
    }

    public void setExpInfo(String expInfo) {
        this.expInfo = expInfo;
    }

    public static Result newFailureResult(CommonError error, boolean needRetry, Throwable exp) {
        Result result = new Result();
        result.setErrCode(error.name());
        result.setErrMsg(error.getMsg());
        result.setSuccess(false);
        result.setExpInfo(JiMuCommonUtils.exceptionToString(exp));
        result.setNeedRetry(needRetry);
        result.setRequestId(MDCLogUtil.getLogId());
        return result;
    }

    public static Result newFailureResult(String errCode, String errMsg) {
        Result result = new Result();
        result.setErrCode(errCode);
        result.setErrMsg(errMsg);
        result.setSuccess(false);
        result.setExpInfo(JiMuCommonUtils.exceptionToString(new Exception(errMsg)));
        result.setNeedRetry(false);
        result.setRequestId(MDCLogUtil.getLogId());
        return result;
    }

    public static Result newFailureResult(String errCode, String errMsg, boolean needRetry, Throwable exp) {
        Result result = new Result();
        result.setErrCode(errCode);
        result.setErrMsg(errMsg);
        result.setSuccess(false);
        result.setExpInfo(JiMuCommonUtils.exceptionToString(exp));
        result.setNeedRetry(needRetry);
        result.setRequestId(MDCLogUtil.getLogId());
        return result;
    }
    public static <T> Result<T> newSuccessResult(T data) {
        Result<T> result = new Result<T>();
        result.setSuccess(true);
        result.setData(data);
        result.setCode(200);
        result.setRequestId(MDCLogUtil.getLogId());
        return result;
    }
    public static <T> Result success(T data){
        return Result.newSuccessResult(data);
    }
    public static Result success(){
        return Result.newSuccessResult(null);
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}