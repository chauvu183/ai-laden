package com.haw.lebensmittelladen.article.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Value
@ResponseStatus(HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = false)
public class ArticleNotFoundException  extends Exception {

    private static String exceptionBasis = "Could not find an article with the";

    String article;

    public static ArticleNotFoundException barcode(String barcode){
        return new ArticleNotFoundException(String.format(exceptionBasis + " barcode: %s.", barcode), barcode);
    }

    public static ArticleNotFoundException productName(String barcode){
        return new ArticleNotFoundException(String.format(exceptionBasis + " Product Name: %s.", barcode), barcode);
    }

    private ArticleNotFoundException(String errorMessage, String article) {
        super(errorMessage);

        this.article = article;
    }

    public ArticleNotFoundException(Long articleId) {
        super(String.format(exceptionBasis + " number %d.", articleId));

        this.article = articleId.toString();
    }

}
