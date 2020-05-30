package com.haw.lebensmittelladen.article.domain.dtos;

import com.haw.lebensmittelladen.article.util.constraints.ProductUnitTypeConstraint;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.*;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArticleCreateDTO {

    @ApiModelProperty(required = true)
    @NotNull
    @Size(min = 1, max = 20)
    private String productName;

    @ApiModelProperty(required = true)
    @NotNull
    private String productFullName;

    //Gramm, Piece, Milliliter (gr,pc,ml)
    @ApiModelProperty(required = true)
    @NotNull
    @ProductUnitTypeConstraint
    private String productSizeUnit;

    @ApiModelProperty(required = true)
    @NotNull
    @Positive
    private int productSize;

    @ApiModelProperty()
    @Size(min = 1, max = 50)
    private String company;

    @ApiModelProperty(required = true)
    @NotNull
    @PositiveOrZero
    private Double price;

    @ApiModelProperty(required = false)
    @NotNull
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private Integer quantity;

    @Override
    public String toString(){
        String seperator = " | ";
        return productName+seperator+productFullName+seperator+productSizeUnit+seperator+productSize+seperator+company+seperator+price+seperator+quantity;
    }
}
