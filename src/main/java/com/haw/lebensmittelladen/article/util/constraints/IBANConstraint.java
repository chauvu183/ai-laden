package com.haw.lebensmittelladen.article.util.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IBANValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IBANConstraint {
    String message() default "Invalid IBAN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
