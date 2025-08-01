package org.openoa.engine.bpmnconf.confentity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

@Data
@Builder
@TableName("t_op_log")
public class OpLog{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 消息编号
     */
    @TableField("msg_id")
    private String msgId;
    /**
     * 0=成功, 1=失败, 2=业务异常
     */
    @TableField("op_flag")
    private Integer opFlag;
    /**
     * 操作账号
     */
    @TableField("op_user_no")
    private String opUserNo;
    /**
     * 操作账号名称
     */
    @TableField("op_user_name")
    private String opUserName;
    /**
     * 操作方法
     */
    @TableField("op_method")
    private String opMethod;
    /**
     * 操作时间
     */
    @TableField("op_time")
    private Date opTime;
    /**
     * 操作用时
     */
    @TableField("op_use_time")
    private Long opUseTime;
    /**
     * 操作内容
     */
    @TableField("op_param")
    private String opParam;
    /**
     * 操作结果
     */
    @TableField("op_result")
    private String opResult;
    /**
     * 系统类型，iOS，Android，1=PC
     */
    @TableField("system_type")
    private String systemType;
    /**
     * app应用版本号
     */
    @TableField("app_version")
    private String appVersion;
    /**
     * 设备类型
     */
    @TableField("hardware")
    private String hardware;
    /**
     * app系统版本号
     */
    @TableField("system_version")
    private String systemVersion;
    /**
     * 操作备注
     */
    private String remark;
    @TableField("is_del")
    private Boolean del;
    @TableField("tenant_id")
    private String tenantId;
    @Tolerate
    public OpLog() {
    }

}
