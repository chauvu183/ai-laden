package com.haw.lebensmittelladen.article.util.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProductUnitTypeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductUnitTypeConstraint {
    String message() default "Invalid unittype";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
