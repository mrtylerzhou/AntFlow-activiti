package org.openoa.engine.utils;


import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author lidonghui
 */
@Slf4j
public class ReflectionUtils {

    /**
     * 通过反射获取对象指定属性的值
     * @param obj 对象实例
     * @param fieldName 属性名
     * @return 属性值，获取失败返回null
     */
    public static Object getProperty(Object obj, String fieldName) {
        if (obj == null || fieldName == null) return null;
        try {
            Field field = getField(obj.getClass(), fieldName);
            if (field == null) return null;
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            log.warn("无法访问属性: {}", fieldName, e);
            return null;
        }
    }

    /**
     * 判断对象是否有指定属性
     * @param obj 对象实例
     * @param fieldName 属性名
     * @return 存在返回true，否则false
     */
    public static boolean hasProperty(Object obj, String fieldName) {
        if (obj == null || fieldName == null) return false;
        return getField(obj.getClass(), fieldName) != null;
    }

    /**
     * 通过反射设置对象指定属性的值
     * @param obj 对象实例
     * @param fieldName 属性名
     * @param value 属性值
     * @return 设置成功返回true，否则false
     */
    public static boolean setProperty(Object obj, String fieldName, Object value) {
        if (obj == null || fieldName == null) return false;
        try {
            Field field = getField(obj.getClass(), fieldName);
            if (field == null) return false;
            field.setAccessible(true);
            field.set(obj, value);
            return true;
        } catch (IllegalAccessException e) {
            log.warn("无法设置属性: {}", fieldName, e);
            return false;
        }
    }

    /**
     * 获取对象指定属性的类型
     * @param obj 对象实例
     * @param fieldName 属性名
     * @return 属性类型，获取失败返回null
     */
    public static Class<?> getPropertyType(Object obj, String fieldName) {
        if (obj == null || fieldName == null) return null;
        Field field = getField(obj.getClass(), fieldName);
        return field != null ? field.getType() : null;
    }

    /**
     * 获取类及其父类的所有字段
     * @param clazz 类对象
     * @return 字段数组
     */
    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * 获取类及其父类的指定字段
     * @param clazz 类对象
     * @param fieldName 字段名
     * @return 字段对象，未找到返回null
     */
    private static Field getField(Class<?> clazz, String fieldName) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
    /**
     * 复制对象属性值到另一个对象
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyProperties(Object source, Object target) {
        try {
            java.beans.BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(source.getClass());
            for (java.beans.PropertyDescriptor propertyDesc : beanInfo.getPropertyDescriptors()) {
                String propName = propertyDesc.getName();
                java.lang.reflect.Method getter = propertyDesc.getReadMethod();
                java.lang.reflect.Method setter = null;
                try {
                    setter = target.getClass().getMethod("set" + propName.substring(0, 1).toUpperCase() + propName.substring(1), propertyDesc.getPropertyType());
                } catch (NoSuchMethodException ignored) {}
                if (getter != null && setter != null) {
                    Object value = getter.invoke(source);
                    setter.invoke(target, value);
                }
            }
        } catch (Exception e) {
            log.warn("复制对象属性值到另一个对象失败: {0}", e);
        }
    }
}
