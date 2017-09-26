package com.test.totoro.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lvning on 16/10/9.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Info {
    public String author() default "Anonymous";

    public String reviewer() default "Anonymous";

    public String rd() default "Anonymous";

    public String clientType() default Const.ANDROID;
}
