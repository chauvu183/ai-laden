package com.haw.lebensmittelladen.article.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Value
@ResponseStatus(HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = false)
public class ArticleNotFoundException  extends Exception {

    String articleNumber;

    public ArticleNotFoundException(String barcode) {
        super(String.format("Could not find article with barcode %s.", barcode));

        this.articleNumber = barcode;
    }

    public ArticleNotFoundException(Long articleId) {
        super(String.format("Could not find article with number %d.", articleId));

        this.articleNumber = articleId.toString();
    }

}
