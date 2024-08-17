
package org.openoa.base.interf;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivitiServiceAnno {
    String svcName() default "";
    String desc();
}
