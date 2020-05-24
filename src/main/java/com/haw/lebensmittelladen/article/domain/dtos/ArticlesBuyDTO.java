package com.haw.lebensmittelladen.article.domain.dtos;


import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArticlesBuyDTO {

    @ApiModelProperty(required = true)
    @NotNull
    @NotEmpty
    private List<ArticleBuyDTO> articles;

    @ApiModelProperty(required = true)
    @NotNull
    private ArticleBuyBankDTO paymentDetails;
}


