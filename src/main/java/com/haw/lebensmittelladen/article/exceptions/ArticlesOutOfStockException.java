package com.haw.lebensmittelladen.article.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Value
@ResponseStatus(HttpStatus.BAD_REQUEST)
@EqualsAndHashCode(callSuper = false)
public class ArticlesOutOfStockException extends Exception {

    private final String articleNames;

    public ArticlesOutOfStockException(String articleNames) {
        super(String.format("Articles do not meet requested stock amount: "+articleNames));

        this.articleNames = articleNames;
    }

    public static String formatArticlesNameList(List<String> names){
        StringBuilder sb = new StringBuilder();
        names.forEach(a->sb.append(" "+a));
        return sb.toString();
    }

}
