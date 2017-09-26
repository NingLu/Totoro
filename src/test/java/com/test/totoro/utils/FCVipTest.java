package com.test.totoro.utils;

import java.lang.annotation.*;

/**
 * Created by zxy on 06/04/2017.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FCVipTest {
    public String availableSince() default "1.0.0";
}
