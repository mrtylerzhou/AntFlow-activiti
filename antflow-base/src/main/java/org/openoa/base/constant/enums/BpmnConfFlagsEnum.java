package org.openoa.base.constant.enums;


import org.openoa.base.exception.JiMuBizException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum BpmnConfFlagsEnum {
    NOTHING(0,"不存在"),
    HAS_NODE_LABELS(0b1,"包含节点标签(任意节点包含标签)"),
    HAS_STARTUSER_CHOOSE_MODULES(0b10,"是否包含发起人自选模块"),
    HAS_DYNAMIC_CONDITIONS(0b100,"是否包含动态条件"),
    HAS_COPY(0b1000,"是否包含抄送"),
    HAS_LAST_NODE_COPY(0b10000,"最后一个节点是否包含抄送"),
    ;
    private final Integer code;
    private final String desc;

    BpmnConfFlagsEnum(Integer code, String desc){

        this.code = code;
        this.desc = desc;
    }

    /**
     * flag叠加
     * @param alreadyFlags
     * @param newFlag
     * @return
     */
    public static Integer binaryOr(Integer alreadyFlags,Integer newFlag){
        if(alreadyFlags==null){
            alreadyFlags=0;
        }
        return alreadyFlags|newFlag;
    }
    /**
     * 从已有的标识中清除某个标识
     * @param alreadyFlags 已有的标识
     * @param flagToRemove 要清除的标识
     * @return 清除后的标识值
     */
    public static Integer binaryAndNot(Integer alreadyFlags, BpmnConfFlagsEnum flagToRemove) {

        if (flagToRemove == null) {
            return alreadyFlags;
        }
        if (alreadyFlags == null) {
            alreadyFlags = 0;
        }
        return alreadyFlags & ~flagToRemove.code;
    }

    /**
     * 现有的标识可以拆分出的枚举数
     * @param flags
     * @return
     */
    public static List<BpmnConfFlagsEnum> flagEnumsByCode(Integer flags){
        if(flags==null){
            flags=0;
        }
        Set<Integer> integers = splitBinary(flags);
        List<BpmnConfFlagsEnum> results=new ArrayList<>();
        for (BpmnConfFlagsEnum value : BpmnConfFlagsEnum.values()) {
            if(integers.contains(value.code)){
                results.add(value);
            }
        }
        return results;
    }

    /**
     *
     * @param alreadyFlags 已有的标识
     * @param flag 要判断是否包含的标识
     * @return
     */
    public static boolean hasFlag(Integer alreadyFlags, BpmnConfFlagsEnum flag){
        if(alreadyFlags==null){
            alreadyFlags=0;
        }
        if(flag==null){
            throw new JiMuBizException("要判断的枚举不存在!");
        }
        List<BpmnConfFlagsEnum> bpmnConfFlags = BpmnConfFlagsEnum.flagEnumsByCode(alreadyFlags);
        for (BpmnConfFlagsEnum confFlagsEnum : bpmnConfFlags) {
            if(confFlagsEnum ==flag){
                return true;
            }
        }
        return false;
    }

    private static Set<Integer> splitBinary(int number) {
        Set<Integer> parts = new HashSet<>();
        int position = 1;  // 从最低位开始

        while (number > 0) {
            if ((number & 1) == 1) {
                parts.add(position);
            }
            number >>= 1;
            position <<= 1;
        }

        return parts;
    }

    public String getDesc() {
        return desc;
    }
    public Integer getCode(){
        return code;
    }
}
