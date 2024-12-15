package org.openoa.base.constant.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.exception.JiMuBizException;


/**
 * @Author TylerZhou
 * @Date 2024/6/22 20:55
 * @Version 1.0
 */
@Getter
public enum JudgeOperatorEnum {
    GTE(1,">="),
    GT(2,">"),
    LTE(3,"<="),
    LT(4,"<"),
    EQ(5,"=")
    ;
    private final Integer code;
    private final String symbol;

    JudgeOperatorEnum(Integer code, String symbol){

        this.code = code;
        this.symbol = symbol;
    }
    public static JudgeOperatorEnum getBySymbol(String symbol){
        if(StringUtils.isBlank(symbol)){
            return null;
        }
        for (JudgeOperatorEnum value : JudgeOperatorEnum.values()) {
            if(value.getSymbol().equals(symbol.trim())){
                return value;
            }
        }
        return null;
    }
    public static JudgeOperatorEnum getByOpType(Integer opType){
        for (JudgeOperatorEnum value : JudgeOperatorEnum.values()) {
            if(value.code.equals(opType)){
                return value;
            }
        }
        throw new JiMuBizException("操作符类型未定义");
    }
}
