package com.haw.lebensmittelladen.article.util.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductUnitTypeValidator implements ConstraintValidator<ProductUnitTypeConstraint, String> {

    @Override
    public void initialize(ProductUnitTypeConstraint constraint) {
    }

    @Override
    public boolean isValid(String testee, ConstraintValidatorContext constraintValidatorContext) {
        List<String> validUnittypes = new ArrayList<>(Arrays.asList("gr","ml","pc"));
        if (testee == null)
            return true;
        else
            return validUnittypes.contains(testee);
    }
}
