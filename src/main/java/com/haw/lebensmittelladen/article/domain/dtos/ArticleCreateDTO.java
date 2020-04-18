package com.haw.lebensmittelladen.article.domain.dtos;

import com.haw.lebensmittelladen.article.domain.datatypes.Barcode;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArticleCreateDTO {

    @ApiModelProperty(required = true)
    @Size(min = 1, max = 20)
    @NotNull
    private String name;


    @ApiModelProperty(required = true)
    @Size(min = 1, max = 20)
    @NotNull
    private Barcode barcode;

    @ApiModelProperty(required = true)
    @NotNull
    private double price;
}
