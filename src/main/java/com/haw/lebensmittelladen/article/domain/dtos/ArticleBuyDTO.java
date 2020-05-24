package com.haw.lebensmittelladen.article.domain.dtos;

import com.haw.lebensmittelladen.article.util.constraints.ProductNameConstraint;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArticleBuyDTO {

    @ApiModelProperty(required = true)
    @Size(min = 1, max = 20)
    @ProductNameConstraint
    private String name;

    @ApiModelProperty(required = true)
    @Positive
    private int amount;
}
