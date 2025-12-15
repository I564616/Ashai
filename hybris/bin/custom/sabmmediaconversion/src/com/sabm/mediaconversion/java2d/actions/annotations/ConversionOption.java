package com.sabm.mediaconversion.java2d.actions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for all ConversionAction implementation. a required annotation for all ConversionAction
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME) public @interface ConversionOption {
    String value();

    String description() default "";

    boolean hasArgument() default true;
}
