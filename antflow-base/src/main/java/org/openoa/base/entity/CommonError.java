package org.openoa.base.entity;

public enum CommonError {
    PARAM_BLANK("参数[%s]不能为空"),
    PARAMS_BLANK_SAME_TIME("参数[%s],[%s]不能同时为空"),
    PARAM_INVALID("参数[%s]的值非法"),
    UN_KNOWN_ERROR("未知错误"),
    SERVER_EXCEPTION("服务端发生异常"),
    CONCURRENT_FAILURE("并发操作失败"),
    JSON_EXCEPTION("JSON解析异常"),
    ENCODE_EXCEPTION("Encode编码异常"),
    ACTION_NOT_SUPPORT("该操作不支持，原因：[%s]"),
    WITH_OUT_AUTHORIZATION("API调用无授权"),
    TIME_OUT("超时[%s]");

    private String msg;

    private CommonError(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
