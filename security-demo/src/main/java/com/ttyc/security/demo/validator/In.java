package com.ttyc.security.demo.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
// 指定校验类
@Constraint(validatedBy = InValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface In {
    String message() default "必须在允许的数值内";

    int[] values();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}