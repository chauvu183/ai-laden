package com.haw.lebensmittelladen.article.domain.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArticlesSoldDTO {

    @ApiModelProperty(required = true)
    private List<ArticleSoldDTO> articles;

    @ApiModelProperty(required = true)
    private double totalPrice;

    public boolean sanityCheck() {
        double accPrices = articles.stream().map(a -> a.getCumulatedPrice()).reduce((i, acc) -> i + acc).get();
        return accPrices == totalPrice;
    }
}
