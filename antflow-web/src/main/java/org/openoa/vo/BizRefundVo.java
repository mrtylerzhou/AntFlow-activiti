package org.openoa.vo;

import lombok.Data;
import org.openoa.base.vo.BusinessDataVo;

import java.util.Date;

@Data
public class BizRefundVo extends BusinessDataVo {
    private Integer RefundUserId;
    private String RefundUserName;
    private Integer RefundType;
    private Date RefundDate;
    private Double RefundMoney;
    private Date createTime;
    private String createName;
    private Date updateTime;
    private String updateName;
    private String remark;
}
