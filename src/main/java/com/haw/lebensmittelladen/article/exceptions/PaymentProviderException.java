package com.haw.lebensmittelladen.article.exceptions;

import com.haw.lebensmittelladen.article.domain.dtos.ArticlesBuyDTO;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Value
@ResponseStatus(HttpStatus.BAD_REQUEST)
@EqualsAndHashCode(callSuper = false)
public class PaymentProviderException extends Exception {

    private final ArticlesBuyDTO articlesBuyDTO;

    public PaymentProviderException(ArticlesBuyDTO articlesBuyDTO) {
        super(String.format("Payment could not be executed."));
        this.articlesBuyDTO = articlesBuyDTO;
    }
}
