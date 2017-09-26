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
public @interface NativeAds {
    public String src() default Const.DEFAULT_SRC;

    public String mt() default Const.DEFAULT_MT;

    public String desc() default "0";
}
