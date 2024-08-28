package org.openoa.vo;

import lombok.Data;
import org.openoa.base.vo.BusinessDataVo;

import java.util.Date;

@Data
public class BizLeaveTimeVo extends BusinessDataVo {
    private Integer leaveUserId;
    private String leaveUserName;
    private Integer leaveType;
    private Date beginDate;
    private Date endDate;
    private Double leaveHour;
    private Date createTime;
    private String createName;
    private Date updateTime;
    private String updateName;
    private String remark;
}
