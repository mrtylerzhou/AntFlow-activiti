package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
public enum OperationResp implements Serializable {
    SUCCESS("00000", "成功"),
    FAILURE("20000", "失败"),
    RETURN_SUCCESS("00000", "success"),
    NO_SERVICE("20003", "服务在开小差，请稍等"), // 未找到java服务
    SYSTEM_ERROR("20004", "服务在开小差，请稍等"), // 系统异常
    SERVICE_ADAPTOR_ERROR("20005", "服务出现异常，请稍等"), // 服务适配异常
    PARAMETER_LOGIN_FAIL("20006", "数据处理出现异常，请稍等"), // 数据登录状态校验失败
    UPDATE_FAIL("20007", "更新数据失败"),
    INSERT_FAIL("20008", "插入数据失败"),
    SELECT_FAIL("20009", "查询失败，未找到记录"),
    SELECT_ACTIVITY_FAIL("200013", "查询基础活动失败，未找到该记录"),
    INSERT_SUCCESS("20010", "插入成功"),
    UPDATE_SUCCESS("20011", "编辑成功"),
    MOBILE_100000("MOBILE_100000", "服务有点忙，亲爱的客官请稍等"),
    SQL_ERROR("20012", "数据库异常"),
    ASK_SUCCESS("000000", "查询成功"),
    UPDATE_FAIL_EXISTS("20013", "更新失败，活动状态已经被更改"),
    PARAM_ERROR("20014", "参数错误"),
    INFORMATION_PERMISSION_DENY("20015", "资讯权限不足"),
    NO_LOGIN_USER("20016", "未找到登录用户"),
    PARAMETER_ERROR("20017", "入参错误"),

    EMPLOYEE_EXIST("60001", "请先移出部门下的员工"),
    NOT_EXIST("60002", "请先添加部门领导"),
    LEADER_EXIST("60003", "该部门已有领导"),
    USERNAME_EXIST("60005", "用户名不能重复"),
    EMAIL_EXIST("60004", "邮箱不能重复"),
    AMOUNT_TOO_SMALL("60007", "担保额度不能少于已使用额度"),
    IS_SUB("60008", "无法添加下属或者子下属为直属上级"),
    PERM_FAIL("408", "权限报错代码"),
    PROC_FAIL("409", "工作流报错代码"),
    SOCKET_FAIL_CODE("30002", "Socket系统服务失败代码"),
    SOCKET_LINK_IP("30003", "Socket握手成功代码"),
    SOCKET_LINK_SUCCESS("30000", "Socket连接成功代码"),
    SOCKET_OFFLINE("40000", "Socket强制设备下线代码"),
    LOGIN_CHANCE_CODE("50000", "登录超限错误代码"),
    APP_AUTHORIZED_LOGIN_CODE("60000", "APP授权登录成功代码"),
    TOO_MANY_PURCHASE_TYPE("61000", "采购类型超过8个"),
    PURCHASE_SOURCE_ERROR("61001", "采购单中采购方信息错误"),
    FORBIDDEN_ERROR("403", "资源没有访问权限"),

    PROUREMENT_AUDIT_PUT_STORAGE("70000", "没有配置仓库");;


    @Getter
    private String code;
    @Getter
    private String desc;

    public static OperationResp get(String code) {
        for (OperationResp e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
