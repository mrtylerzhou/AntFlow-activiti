package org.openoa.base.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum OpLogFlagEnum {

    SUCCESS(0, "成功"), FAILURE(1, "失败"), BusinessException(2, "业务异常");

    @Getter
    private Integer code;

    @Getter
    private String desc;

}
