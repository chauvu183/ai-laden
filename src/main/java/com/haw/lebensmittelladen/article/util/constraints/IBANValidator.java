package com.haw.lebensmittelladen.article.util.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IBANValidator implements ConstraintValidator<IBANConstraint, String> {

    private static final String IBAN_PATTERN = "^DE\\d{2}\\s?([0-9a-zA-Z]{4}\\s?){4}[0-9a-zA-Z]{2}$";

    @Override
    public void initialize(IBANConstraint constraint) {
    }

    @Override
    public boolean isValid(String iban, ConstraintValidatorContext context) {
        return iban.matches(IBAN_PATTERN);
    }
}
