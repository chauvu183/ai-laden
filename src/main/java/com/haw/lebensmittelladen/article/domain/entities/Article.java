package com.haw.lebensmittelladen.article.domain.entities;

import com.haw.lebensmittelladen.article.domain.datatypes.Barcode;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(required = true)
    private Barcode barcode;

    @ApiModelProperty(required = true)
    private String productName;

    @ApiModelProperty(required = true)
    private String productFullName;

    @ApiModelProperty(required = true)
    private String company;

    //TODO: currency? format?
    @ApiModelProperty(required = true)
    private double price;

    //TODO: quantity > 0
    @ApiModelProperty(required = true)
    private Integer quantity;

    public Article(Barcode barcode, String name, double price){
        this.barcode = barcode;
        this.productName = productName;
        this.price = price;
    }

    public static Article of(ArticleCreateDTO articleCreateDTO){
        return new Article(articleCreateDTO.getBarcode(), articleCreateDTO.getName(), articleCreateDTO.getPrice());
    }

    public boolean isAvailable(){
        return quantity > 0;
    }

    public boolean enoughInStock(int needed){
        return quantity >= needed;
    }
}
