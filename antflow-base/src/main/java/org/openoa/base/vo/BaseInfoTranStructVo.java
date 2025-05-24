package org.openoa.base.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseInfoTranStructVo extends BaseIdTranStruVo{
    private String varName;
}
