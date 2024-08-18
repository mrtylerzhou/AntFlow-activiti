package org.openoa.engine.factory;


import java.lang.annotation.*;

/**
 * this is used by antflow framework for build up adaptor factory
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpfService {
    Class<? extends TagParser<?,?>> tagParser();
}
