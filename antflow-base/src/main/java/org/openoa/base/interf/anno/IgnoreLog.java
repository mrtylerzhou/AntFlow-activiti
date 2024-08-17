package org.openoa.base.interf.anno;

import java.lang.annotation.*;

/**
 * it is used to ignore request log,if you system has a lot of requests and you have logging system like elk,you can use this annotation to ignore request log.
 * @author AntFlow
 * @since 0.5
 */
@Documented
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreLog {

}
