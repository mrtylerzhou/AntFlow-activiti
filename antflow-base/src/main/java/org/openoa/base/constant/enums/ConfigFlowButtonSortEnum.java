package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * 可配置化流程按钮排序参数
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-04 10:00
 * @Param
 * @return
 * @Version 1.0
 */
public enum ConfigFlowButtonSortEnum {
    SUBMIT(1, "提交", 1), // 1提交
    AGAIN_SUBMIT(2, "重新提交", 2), // 2重新提交
    AGREED(3, "同意", 10), // 3同意
    NO_AGREED(4, "不同意", 9), // 4不同意
    BACK_NODE_EDIT(6, "打回上节点修改", 7), // 6打回上节点修改
    ABANDONED(7, "作废", 11), // 7作废
    PRINT(8, "打印", 13), // 8打印
    UNDERTAKE(10, "承办", 3), // 10 承办
    CHANGE_TYPE(11, "变更处理人", 4), // 11 变更处理人
    END_TYPE(12, "终止", 5), // 12 终止
    ADD_APPROVAL_PEOPLE(13, "添加审批人", 6),// 13 添加审批人
    FORWARDING(15, "转发", 12), // 9转发
    BACK_EDIT(18, "打回修改", 8), // 5打回修改
    SCAN_HELP(20, "扫码帮助", 20),

            ;
    @Getter
    private final Integer code;

    @Getter
    private final String desc;

    @Getter
    private final Integer sort;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getSort() {
        return sort;
    }

    ConfigFlowButtonSortEnum(Integer code, String desc, Integer sort) {
        this.code = code;
        this.desc = desc;
        this.sort = sort;
    }

    /**根据code获取枚举数据
     * @param code
     * @return
     * @date 2019年6月30日
     */
    public static ConfigFlowButtonSortEnum getEnumByCode(Integer code) {
        for (ConfigFlowButtonSortEnum statusType : ConfigFlowButtonSortEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType;
            }
        }
        return null;
    }

    public static String getDescByCode(Integer code) {
        for (ConfigFlowButtonSortEnum statusType : ConfigFlowButtonSortEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }

    public static Integer getCodeByDesc(String desc) {
        for (ConfigFlowButtonSortEnum statusType : ConfigFlowButtonSortEnum.values()) {
            if (statusType.desc.equals(desc)) {
                return statusType.code;
            }
        }
        return null;
    }
}
