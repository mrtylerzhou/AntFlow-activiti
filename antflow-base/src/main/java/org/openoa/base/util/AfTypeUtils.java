package org.openoa.base.util;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AfTypeUtils {

    public static Set<ResolvableType> getAllTypes(ResolvableType root) {
        Set<ResolvableType> result = new LinkedHashSet<>();
        collect(root, result);
        return result;
    }

    private static void collect(ResolvableType type, Set<ResolvableType> result) {
        if (type == null || type == ResolvableType.NONE) {
            return;
        }

        if (!result.add(type)) {
            return;
        }

        // 接口（含接口的父接口）
        for (ResolvableType itf : type.getInterfaces()) {
            collect(itf, result);
        }

        // 父类
        collect(type.getSuperType(), result);
    }
}
