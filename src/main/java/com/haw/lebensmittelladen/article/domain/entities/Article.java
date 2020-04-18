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

    //@Column(unique=true) //barcodes are essentially links to some place thus no reason to make them unique
    @ApiModelProperty(required = true)
    @Embedded
    private Barcode barcode;

    @ApiModelProperty(required = true)
    private String productName;

    @ApiModelProperty(required = true)
    private String productFullName;

    @ApiModelProperty()
    private String company;

    //TODO: currency? format?
    @ApiModelProperty(required = true)
    private double price;

    //TODO: quantity > 0
    @ApiModelProperty(required = true)
    private int quantity;

    public Article(Barcode barcode, String productName, String productFullName, String company, double price, int quantity) {
        this.barcode = barcode;
        this.productName = productName;
        this.productFullName = productFullName;
        this.company = company;
        this.price = price;
        this.quantity = quantity;
    }

    public static Article of(ArticleCreateDTO articleCreateDTO) {
        Barcode bar = articleCreateDTO.getBarcode();
        return new Article(bar!=null&&Barcode.isValid(bar.getCode())?articleCreateDTO.getBarcode():new Barcode(),
                articleCreateDTO.getProductName(),
                articleCreateDTO.getProductFullName(),
                articleCreateDTO.getCompany(),
                articleCreateDTO.getPrice(),
                articleCreateDTO.getQuantity());
    }

    public boolean isAvailable() {
        return quantity > 0;
    }

    public boolean enoughInStock(int needed) {
        return quantity >= needed;
    }
}
