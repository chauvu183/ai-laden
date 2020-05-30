package com.haw.lebensmittelladen.article.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Value
@ResponseStatus(HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = false)
public class ArticleNotFoundException extends Exception {

    private static String exceptionBasis = "Could not find an article with the";

    String article;

    public static ArticleNotFoundException productName(String name){
        return new ArticleNotFoundException(String.format(exceptionBasis + " Product Name: %s.", name), name);
    }

    private ArticleNotFoundException(String errorMessage, String article) {
        super(errorMessage);

        this.article = article;
    }

    public ArticleNotFoundException(Long articleId) {
        super(String.format(exceptionBasis + " number %d.", articleId));

        this.article = articleId.toString();
    }

    public ArticleNotFoundException(String name) {
        super("Article not found: "+name);
        this.article = name;
    }

}
