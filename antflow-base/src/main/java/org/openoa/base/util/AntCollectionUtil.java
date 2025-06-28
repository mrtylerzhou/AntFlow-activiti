package org.openoa.base.util;

import org.openoa.base.vo.BaseNumIdStruVo;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * a convenient collection util mainly used to conver id types
 * @Author TylerZhou
 * @Date 2024/6/30 19:44
 * @Version 1.0
 */
public class AntCollectionUtil {
    public static <T extends Number> Collection<String> numberToStringList (Collection<T> numbers){

        return numbers.stream().map(Object::toString).collect(Collectors.toList());
    }

    public static Collection<String> serializeToStringCollection (Collection<?> collection){
        return collection.stream().map(Object::toString).collect(Collectors.toList());
    }
    public static Collection<Integer> StringToIntList (Collection<String> numbers){
        return numbers.stream().map(Integer::parseInt).collect(Collectors.toList());
    }
    public static Collection<Long> StringToLongList (Collection<String> numbers){

        return numbers.stream().map(Long::parseLong).collect(Collectors.toList());
    }
    public static Collection<Long> IntToLongList (Collection<Integer> numbers){

        return numbers.stream().map(Integer::longValue).collect(Collectors.toList());
    }
    public static <T> Collection<Integer> LongToIntList (Collection<Long> numbers){

        return numbers.stream().map(Long::intValue).collect(Collectors.toList());
    }
    public static String joinBaseNumIdTransVoToString (Collection<BaseNumIdStruVo> list){
        if(CollectionUtils.isEmpty(list)){
            return "";
        }
        return list.stream().map(a->a.getId().toString()).collect(Collectors.joining(","));
    }
}
