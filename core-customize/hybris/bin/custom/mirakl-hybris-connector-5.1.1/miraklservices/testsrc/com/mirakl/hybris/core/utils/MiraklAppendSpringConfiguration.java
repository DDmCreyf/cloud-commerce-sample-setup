package com.mirakl.hybris.core.utils;

import java.lang.annotation.*;

/**
 * Backport of Hybris' @AppendSpringConfiguration annotation for oldest Hybris versions
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MiraklAppendSpringConfiguration {
    String[] value();
}
