package pers.acp.admin.common.annotation;

import java.lang.annotation.*;

/**
 * @author zhang by 24/05/2019
 * @since JDK 11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DuplicateSubmission {

    String KEY = "[key]";

    /**
     * key的格式，默认[key]
     *
     * @return key
     */
    String keyExpress() default KEY;

    /**
     * 过期时间，单位毫秒，默认30秒
     *
     * @return 过期时间
     */
    long expire() default 30000;

}
