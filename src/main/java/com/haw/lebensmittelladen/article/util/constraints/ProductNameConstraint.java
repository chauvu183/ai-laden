package com.haw.lebensmittelladen.article.util.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProductValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductNameConstraint {
    String message() default "product not found in database";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
