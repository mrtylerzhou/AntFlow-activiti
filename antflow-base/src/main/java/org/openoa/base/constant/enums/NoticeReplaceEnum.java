package org.openoa.base.constant.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum NoticeReplaceEnum {

    PROCESS_TYPE(1, "流程类型", "processType", false),
    PROCESS_NAME(2, "流程名称", "processName", false),
    REJECT_NAME(3, "审批不同意者", StringUtils.EMPTY, true),
    OPERATOR(4, "操作者", StringUtils.EMPTY, true),
    AFTER_CHANGE_TREATER(5, "变更后处理人", StringUtils.EMPTY, true),
    ORIGINAL_NODE_TREATER(6, "原审批节点处理人", StringUtils.EMPTY, true),
    PROCESS_ID(7, "流程编号", "processId", false),
    ;

    @Getter
    private Integer code;

    @Getter
    private String desc;

    @Getter
    private String filName;

    @Getter
    private Boolean isSelectEmpl;

    NoticeReplaceEnum(Integer code, String desc, String filName, Boolean isSelectEmpl) {
        this.code = code;
        this.desc = desc;
        this.filName = filName;
        this.isSelectEmpl = isSelectEmpl;
    }

    public static String getDescByCode(Integer code) {
        for (NoticeReplaceEnum enums : NoticeReplaceEnum.values()) {
            if (enums.code.equals(code)) {
                return enums.desc;
            }
        }
        return null;
    }
}
