package org.openoa.base.interf.anno;

import org.openoa.base.constant.enums.FieldValueTypeEnum;

import java.lang.annotation.*;

/**
 * @Author TylerZhou
 * @Date 2024/7/27 19:00
 * @Version 1.0
 */
@Documented
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeField {
    String name();
    FieldValueTypeEnum type();
    //if multiple values,split with ","
    String value() default "";
    boolean multipleChoice() default false;
}
