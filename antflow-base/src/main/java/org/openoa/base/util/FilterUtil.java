package org.openoa.base.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *@Author JimuOffice
 * @Description //TODO $
 * @Date 2022-05-04 10:17
 * @Param
 * @return
 * @Version 1.0
 */
public class FilterUtil {
    public static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();
        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());
            boolean b = seen.putIfAbsent(keys, Boolean.TRUE) == null;
            return b;
        };
    }
    public static <T> Predicate<T> sameByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());

            boolean b = seen.putIfAbsent(keys, Boolean.TRUE) != null;
            return b;
        };
    }
}
