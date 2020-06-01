package com.haw.lebensmittelladen.article.domain.entities;

import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
    @Column(nullable = false)
    @Positive
    private int productSize;

    @ApiModelProperty()
    private String company;

    @ApiModelProperty(required = true)
    @Column(nullable = false)
    @PositiveOrZero
    private double price;

    @ApiModelProperty(required = true)
    @PositiveOrZero
    private int quantity;

    public Article(String productName, String productFullName, String productSizeUnit, int productSize, String company, double price, int quantity) {
        this.productName = productName;
        this.productFullName = productFullName;
        this.productSizeUnit = productSizeUnit;
        this.productSize = productSize;
        this.company = company;
        this.price = price;
        this.quantity = quantity;
    }

    public static Article of(ArticleCreateDTO articleCreateDTO) {
        return new Article(
                articleCreateDTO.getProductName(),
                articleCreateDTO.getProductFullName(),
                articleCreateDTO.getProductSizeUnit(),
                articleCreateDTO.getProductSize(),
                articleCreateDTO.getCompany(),
                articleCreateDTO.getPrice(),
                null == articleCreateDTO.getQuantity() ? 0 : articleCreateDTO.getQuantity());
    }

    public boolean isAvailable() {
        return quantity > 0;
    }

    public boolean enoughInStock(int needed) {
        return quantity >= needed;
    }

    public boolean takeOutOfStock(int amount) {
        if (!enoughInStock(amount)) {
            return false;
        }
        this.quantity -= amount;
        return true;
    }
}
