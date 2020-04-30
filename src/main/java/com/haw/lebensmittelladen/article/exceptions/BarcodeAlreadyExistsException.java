package com.haw.lebensmittelladen.article.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Value
@ResponseStatus(HttpStatus.BAD_REQUEST)
@EqualsAndHashCode(callSuper = false)
public class BarcodeAlreadyExistsException extends Exception{
    String barcode;

    public BarcodeAlreadyExistsException(String barcode) {
        super(String.format( "Barcode Already Exists %s", barcode));

        this.barcode = barcode;
    }
}
