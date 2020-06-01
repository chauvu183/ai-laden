package com.haw.lebensmittelladen.article.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Value
@ResponseStatus(HttpStatus.BAD_REQUEST)
@EqualsAndHashCode(callSuper = false)
public class ProductAlreadyExistsException extends Exception {
    String productName;

    public ProductAlreadyExistsException(String productName) {
        super(String.format("Productname Already Exists %s", productName));

        this.productName = productName;
    }
}
