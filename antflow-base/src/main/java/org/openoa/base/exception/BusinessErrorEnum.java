package org.openoa.base.exception;

/**
 * @Classname AFBizException
 * @Description TODO
 * @Date 2021-10-31 11:06
 * @Created by AntOffice
 */

public enum BusinessErrorEnum {




    /**
     * 参数错误
     */
    PARAMS_IS_NULL(1418300271, "参数为空错误"),
    PARAMS_NOT_COMPLETE(1418300272, "参数不全"),
    PARAMS_TYPE_ERROR(1418300273, "参数类型匹配错误"),
    PARAMS_IS_INVALID(1418300274, "参数无效"),
    STATUS_ERROR(1418300275, "状态异常"),
    DATA_IS_OVER_MAXIMUM_LENGTH(1418300376, "数据长度超过最大长度:{}"),
    FILE_FORMAT_ERROR(999008070, "文件格式不正确"),

    /**
     * 数据处理错误
     */
    DATA_PROCESS_ERRROR(1418300371,"数据处理错误"),
    UPDATE_FAIL(1418300371, "更新数据失败"),
    INSERT_FAIL(1418300372, "插入数据失败"),
    SELECT_FAIL(1418300373, "查询失败，未找到记录"),
    DATA_NOT_FOUND(1418300374, "数据未找到"),
    DATA_IS_WRONG(1418300375, "数据有误"),
    DATA_ALREADY_EXISTED(1418300376, "数据已存在"),
    UPDATE_FAIL_EXISTS(1418300377, "更新失败，活动状态已经被更改"),
    DATA_TOO_LONG(1418300378, "数据导出超出上限{}，请缩小查询时间范围！"),
    UNFINISHED_TASK(1418300379, "已有未完成任务,请在{}分钟之后再导出!"),
    DATA_EXCEED_EXCEL_LIMIT(1418300380, "数据超过100万,不能导出!"),
    IMPORT_TASK_ERROR(1418300381, "导入任务保存失败，请重新导入!"),
    FILE_UPLOAD_ERROR(1418300382, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(1418300384, "文件下载失败"),
    FILE_DELETE_ERROR(1418300385, "文件删除失败"),
    FILE_GET_ERROR(1418300386, "获取文件失败"),
    FILE_TYPE_ERROR(1418300387, "文件类型错误"),
    MODULE_TYPE_ERROR(1418300388, "模块类型错误"),


    /**
     * 用户/组织关系错误
     */
    USER_NOT_EXIST(1418300471, "用户不存在"),
    USER_NOT_LOGGED_IN(1418300472, "用户未登陆"),
    USER_ACCOUNT_ERROR(1418300473, "用户名或密码错误"),
    USER_ACCOUNT_FORBIDDEN(1418300474, "用户账户已被禁用"),
    USER_HAS_EXIST(1418300475, "该用户已存在"),
    USER_CODE_ERROR(1418300476, "验证码错误"),
    USER_NOT_BELONG_CURRENT_ORG(1418300476,"用户不属于当前组织"),


    /**
     * 数据交互错误，系统错误
     */
    ACT_INNER_ERROER(1418300500,"activiti引擎内部错误"),
    ANTFLOW_INNER_ERROER(1418300501,"antflow引擎内部错误"),
    API_INNER_INVOKE_ERROR(1418300571, "系统内部接口调用异常"),
    API_OUTER_INVOKE_ERROR(1418300572, "系统外部接口调用异常"),
    API_ACCESS_DENIED(1418300573, "接口禁止访问"),
    API_ENDPOINT_INVALID(1418300574, "接口地址无效"),
    API_REQUEST_TIMEOUT(1418300575, "接口请求超时"),
    INTERFACE_EXCEED_LOAD(999005050, "接口负载过高"),
    SYSTEM_INNER_ERROR(999005060, " 服务在开小差，请稍等"),
    SERVICE_CALL_ERROR(999005070, "跨服务调用错误"),
    SERVICE_NOT_AVAILABLE(999005080, "服务不可用"),
    TOO_MANY_REQUEST(999005081,"请求过于频繁"),
    DB_ACCESS_ERROR(999005082,"数据库请求错误"),

    /**
     * 权限错误
     */
    PERMISSION_NO_ACCESS(1418300671, "当前按钮没有访问权限"),
    CONNECTION_RELATION_ERROR(1418300672, "关联关系不匹配"),
    RIGHT_VIOLATE(1418300673, "超出访问权限"),
    RIGHT_INVALID(1418300674, "当前状态不允许操作"),
    /**
     * 配置错误
     */
    CONFIG_FORMAT_ERROR(1418300771,"配置格式错误"),
    CONFIG_ITEM_NOT_EXIST(1418300771,"配置条目不存在"),
    CONFIG_ITEM_NOT_INVALID(1418300771,"配置内容非法")

    /**
     * 业务类错误
     */

    ;
    private int code;
    private String msg;

    BusinessErrorEnum() {
    }

    BusinessErrorEnum(int code, String desc) {
        this.code = code;
        this.msg = desc;
    }

    public int getCode() {
        return code;
    }
    public String getCodeStr(){
        return  String.valueOf(code);
    }

    public String getMsg() {
        return msg;
    }


    @Override
    public String toString() {
        return "ResultCodeEnum [code=" + code + ", msg=" + msg + "]";
    }
}
