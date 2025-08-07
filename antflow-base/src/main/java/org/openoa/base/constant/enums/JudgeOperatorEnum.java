package org.openoa.base.constant.enums;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.exception.AFBizException;

import java.util.List;


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
    EQ(5,"="),
    GT1LT2(6,"first<a<second"),
    GTE1LT2(7,"first<=a<second"),
    GET1LE2(8,"first<a<=second"),
    GTE1LTE2(9,"first<=a<=second"),

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
        throw new AFBizException("操作符类型未定义");
    }
    public static List<Integer> binaryOperator(){
        return Lists.newArrayList(6,7,8,9);
    }
}
