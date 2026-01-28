package org.openoa.base.constant.enums;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

public interface BinaryAfEnumBase<T extends Enum<?> & AfEnumBase> extends AfEnumBase{
    /**
     * 从已有的标识中清除当前标识
     * @param alreadyFlags 已有的标识
     * @return 清除后的标识值
     */
     default Integer binaryAndNot(Integer alreadyFlags) {


        if (alreadyFlags == null) {
            alreadyFlags = 0;
        }
        return alreadyFlags & ~this.getCode();
    }

    /**
     * 现有的标识可以拆分出的枚举数
     * @param flags
     * @return
     */
    default  List<T> flagEnumsByCode(Integer flags){
        if(flags==null){
            flags=0;
        }
        Set<Integer> integers = splitBinary(flags);
        List<T> results=new ArrayList<>();
        Class<T> enumType =(Class<T>) this.getClass();

        for (T value :enumType.getEnumConstants()) {
            if(integers.contains(value.getCode())){
                results.add(value);
            }
        }
        return results;
    }

    /**
     * 已有的标识中是否包含当前的标识
     * @param alreadyFlags 已有的标识
     * @return
     */
    default  boolean flagsContainsCurrent(Integer alreadyFlags){
        if(alreadyFlags==null){
            alreadyFlags=0;
        }

        List<T> bpmnConfFlags = flagEnumsByCode(alreadyFlags);
        for (T confFlagsEnum : bpmnConfFlags) {
            if(confFlagsEnum ==this){
                return true;
            }
        }
        return false;
    }

    /**
     * flag叠加
     * @param alreadyFlags
     * @return
     */
    default Integer binaryOr(Integer alreadyFlags){
        if(alreadyFlags==null){
            alreadyFlags=0;
        }
        return alreadyFlags|this.getCode();
    }
    default Integer binaryOr(Integer ... alreadyFlags){
        Integer result=0;
        ArrayList<Integer> integers = Lists.newArrayList(alreadyFlags);
        return binaryOr(integers);
    }
    default Integer binaryOr(List<Integer> alreadyFlags){
        Integer result=0;
        for (Integer alreadyFlag : alreadyFlags) {
            result=result|alreadyFlag;
        }
        result=result|this.getCode();
        return result;
    }
    default Integer binaryOr(T ... alreadyFlags){
        List<Integer> flags = Arrays.stream(alreadyFlags).map(T::getCode).collect(Collectors.toList());
        return binaryOr(flags);
    }
    default Integer binaryOr(Collection<T> alreadyFlags){
        List<Integer> flags = alreadyFlags.stream().map(T::getCode).collect(Collectors.toList());
        return binaryOr(flags);
    }
    default Set<Integer> splitBinary(int number) {
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

}
