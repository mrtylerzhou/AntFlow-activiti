package org.openoa.base.util;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadLocalContainer {

    private static ThreadLocal<Map<String, Object>> CACHE = TransmittableThreadLocal.withInitial(ConcurrentHashMap::new);

    public static Object get(String key) {
        return CACHE.get().get(key);
    }

    public static void set(String key, Object val) {
        CACHE.get().put(key, val);
    }

    public static Object remove(String key) {
        return CACHE.get().remove(key);
    }

    /**
     * 此方法应该在线程销毁前调用
     * this method should be called before thread destroying
     *
     * @return
     */
    public static Map<String, Object> clean() {
        Map<String, Object> map = CACHE.get();
        CACHE.remove();
        return map;
    }


}
