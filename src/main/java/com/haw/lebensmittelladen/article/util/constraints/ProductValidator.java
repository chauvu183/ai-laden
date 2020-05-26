package com.haw.lebensmittelladen.article.util.constraints;

import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ProductValidator implements ConstraintValidator<ProductNameConstraint, String> {
    @Autowired
    ArticleRepository articles;

    @Override
    public void initialize(ProductNameConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return articles.findByProductFullNameIgnoreCase(value).isPresent();
    }
}
