package com.haw.lebensmittelladen.article.util.constraints;

import com.haw.lebensmittelladen.article.domain.datatypes.Email;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {

    @Override
    public void initialize(EmailConstraint emailConstraint) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null)
            return true;
        else
            return Email.isValid(email);
    }
}
