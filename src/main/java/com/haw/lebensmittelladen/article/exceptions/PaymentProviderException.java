package com.haw.lebensmittelladen.article.exceptions;

import com.haw.lebensmittelladen.article.domain.dtos.ArticlesBuyDTO;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Value
@ResponseStatus(HttpStatus.BAD_GATEWAY)
@EqualsAndHashCode(callSuper = false)
public class PaymentProviderException extends Exception {
    static final String DEFAULT = "Payment could not be executed. ";

    private final String message;

    public PaymentProviderException(String message) {
        super(DEFAULT+message);
        this.message = DEFAULT+message;
    }
}
