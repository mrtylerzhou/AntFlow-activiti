package org.openoa.base.interf.anno;

import java.lang.annotation.*;

/**
 * it is an empty interface used as a marker interface for biz interface,means that it is a not process logic,it is business logic
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BizInterface {

}
