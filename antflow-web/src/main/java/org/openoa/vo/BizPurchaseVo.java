package org.openoa.vo;

import lombok.Data;
import org.openoa.base.vo.BusinessDataVo;

import java.util.Date;

@Data
public class BizPurchaseVo extends BusinessDataVo {
    private Integer PurchaseUserId;
    private String PurchaseUserName;
    private Integer PurchaseType;
    private Date PurchaseDate;
    private Double PlanProcurementTotalMoney;
    private Date createTime;
    private String createName;
    private Date updateTime;
    private String updateName;
    private String remark;
}
