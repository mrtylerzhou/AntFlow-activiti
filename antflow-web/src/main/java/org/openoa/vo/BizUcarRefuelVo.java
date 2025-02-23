package org.openoa.vo;

import lombok.Data;
import org.openoa.base.vo.BusinessDataVo;

import java.util.Date;

@Data
public class BizUcarRefuelVo extends BusinessDataVo {
    private String licensePlateNumber;
    private Date refuelTime;
    private Date createTime;
    private String createName;
    private Date updateTime;
    private String updateName;
    private String remark;
}
