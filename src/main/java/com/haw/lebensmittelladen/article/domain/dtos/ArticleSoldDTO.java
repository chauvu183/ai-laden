package com.haw.lebensmittelladen.article.domain.dtos;

import com.haw.lebensmittelladen.article.domain.entities.Article;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArticleSoldDTO {

    //todo: constraint lowercase
    @Column(nullable = false, length = 100)
    @ApiModelProperty(required = true)
    private String productName;

    @Column(unique = true, nullable = false, length = 100)
    @ApiModelProperty(required = true)
    private String productFullName;

    //Gramm, Piece, Milliliter (gr,pc,ml)
    @Column(nullable = false, length = 2)
    @ApiModelProperty(required = true)
    private String productSizeUnit;

    @ApiModelProperty(required = true)
    @Positive
    private int productSize;

    @ApiModelProperty()
    private String company;

    @ApiModelProperty(required = true)
    private double pricePerUnit;

    @ApiModelProperty(required = true)
    private double cumulatedPrice;

    @ApiModelProperty(required = true)
    @Positive
    private int soldQuantity;

    public ArticleSoldDTO(Article art, int amountBought){
        this.productName = art.getProductName();
        this.productFullName = art.getProductFullName();
        this.productSizeUnit = art.getProductSizeUnit();
        this.productSize = art.getProductSize();
        this.company = art.getCompany();
        this.pricePerUnit = art.getPrice();
        this.soldQuantity = amountBought;
        this.cumulatedPrice = this.soldQuantity * this.pricePerUnit;
    }
}
