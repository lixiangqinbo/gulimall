package com.lxsx.gulimall.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {ListValidConstraintValidator.class}
)
public @interface ListValid {

    String message() default "{com.lxsx.gulimall.valid.ListValid.message}";

    Class<?>[] groups() default {};

    int[] value() default {};

    Class<? extends Payload>[] payload() default {};
}
