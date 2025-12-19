package org.openoa.base.constant.enums;

import lombok.Getter;

public enum DuplicationProcessStrategyEnum implements AfEnumBase {
    REMOVE(1, "去除"),//去除即不生成审批人的审批任务
    SKIP(2, "跳过")//会生成审批人的审批任务,但是多个节点出现相同审批人时,前置(前去重)、后续(后去重)、相邻(后续)节点会自动同意
    ;
    @Getter
    private final Integer code;
    @Getter
    private final String desc;

    DuplicationProcessStrategyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
