package org.openoa.base.interf.anno;

import java.lang.annotation.*;

/**
 * used for auto parse the request params,main purpose it to reduce tedious repeated work
 * you can refer to the example for more details
 * @Author TylerZhou
 * @Date 2024/7/10 21:06
 * @Version 0.5
 */
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoParse {
}
