package org.openoa.base.util;

import org.springframework.core.env.Environment;

public class PropertyUtil {

    public static String getProperty(String key) {
        Environment env = SpringBeanUtils.getBean(Environment.class);
        return env.getProperty(key);
    }
}