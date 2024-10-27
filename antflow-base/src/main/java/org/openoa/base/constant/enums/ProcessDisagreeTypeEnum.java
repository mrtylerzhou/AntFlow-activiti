package org.openoa.base.constant.enums;

import lombok.Getter;

/**
 * process not agree type
 */
public enum ProcessDisagreeTypeEnum {

    //node submit type (1:Return to the previous node to submit the next node
    // 2:Return to the initiator to submit the next node
    // 3:Return to the initiator to submit the retreat node
    // 4:Return to the historical node to submit the next node
    // 5:Return to the historical node to submit the retreat node)
    ONE_DISAGREE(1, "退回上一个节点提交下一个节点"),
    TWO_DISAGREE(2, "退回发起人提交下一个节点"),
    THREE_DISAGREE(3, "退回发起人提交回退节点"),//default behavior
    FOUR_DISAGREE(4, "退回历史节点提交下一个节点"),
    FIVE_DISAGREE(5, "退回历史节点提交回退节点"),
    ;
    @Getter
    private Integer code;

    @Getter
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    ProcessDisagreeTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (ProcessDisagreeTypeEnum statusType : ProcessDisagreeTypeEnum.values()) {
            if (statusType.code.equals(code)) {
                return statusType.desc;
            }
        }
        return null;
    }

   public static ProcessDisagreeTypeEnum getByCode(Integer code){
        if(code==null){
            return null;
        }
       for (ProcessDisagreeTypeEnum value : ProcessDisagreeTypeEnum.values()) {
           if(code.equals(value.getCode())){
               return value;
           }
       }
       return null;
   }
}
