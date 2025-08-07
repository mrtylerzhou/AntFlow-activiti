package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.Strings;
import lombok.Data;
import org.openoa.base.exception.AFBizException;

import java.util.Date;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@TableName("t_bpmn_outside_conf")
public class BpmnOutsideConf {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * formCode
     */
    @TableField("form_code")
    private String formCode;
    /**
     * nodule code
     */
    private String moduleCode;
    /**
     * call back url
     */
    @TableField("call_back_url")
    private String callBackUrl;
    /**
     * detail url
     */
    @TableField("detail_url")
    private String detailUrl;
    /**
     * 0 for normal,1 for delete
     */
    @TableField("is_del")
    private Integer isDel;
    /**
     * business name
     */
    @TableField("business_name")
    private String businessName;
    @TableField("remark")
    private String remark;
    /**
     * create user id
     */
    @TableField("create_user_id")
    private Integer createUserId;
    /**
     * create time
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * modify user id
     */
    @TableField("modified_user_id")
    private Integer modifiedUserId;
    /**
     * modify time
     */
    @TableField("modified_time")
    private Date modifiedTime;
    public static void checkParams(BpmnOutsideConf conf){
        if(Strings.isNullOrEmpty(conf.getFormCode())){
            throw new AFBizException("formCode不能为空!");
        }
        if(Strings.isNullOrEmpty(conf.getCallBackUrl())){
            throw new AFBizException("callBackUrl不能为空!");
        }
        if(Strings.isNullOrEmpty(conf.getDetailUrl())){
            throw new AFBizException("detailUrl不能为空!");
        }
        if(Strings.isNullOrEmpty(conf.getBusinessName())){
            throw new AFBizException("businessName不能为空!");
        }
    }
}
