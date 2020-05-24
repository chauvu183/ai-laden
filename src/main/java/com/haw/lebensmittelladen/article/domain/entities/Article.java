package com.haw.lebensmittelladen.article.domain.entities;

import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.gateways.PaymentGateway;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //todo: constraint lowercase
    @Column(unique = true, nullable = false, length = 100)
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

    public Article(String productName, String productFullName, String company, double price, int quantity) {
        this.productName = productName;
        this.productFullName = productFullName;
        this.company = company;
        this.price = price;
        this.quantity = quantity;
    }

    public static Article of(ArticleCreateDTO articleCreateDTO) {
        return new Article(
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
