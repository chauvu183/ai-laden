package com.haw.lebensmittelladen.article.util.constraints;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProvideAtLeastOneFieldValidator implements ConstraintValidator<ProvideAtLeastOneFieldConstraint, Object> {

    private String[] fields;

    public void initialize(ProvideAtLeastOneFieldConstraint constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    public boolean isValid(Object value, ConstraintValidatorContext context) {
        for (String field : fields) {
            Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
            if (fieldValue != null) {
                return true;
            }
        }
        return false;
    }
}
