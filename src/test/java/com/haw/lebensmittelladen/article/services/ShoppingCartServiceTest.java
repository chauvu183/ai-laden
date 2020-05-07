package com.haw.lebensmittelladen.article.services;

import com.haw.lebensmittelladen.Application;
import com.haw.lebensmittelladen.article.domain.datatypes.Barcode;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.entities.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureJsonTesters
@ActiveProfiles(profiles = "testing")
class ShoppingCartServiceTest {

    @Autowired
    private ShoppingCartService shoppingCartService;

    Article article = new Article(new Barcode(), "Banane", "Edeka Bio Banane", "Edeka", 1.55, 1);

    @BeforeEach
    void before(){
        shoppingCartService.newShoppingCart();
    }

    @Test
    void addItemTest(){
        shoppingCartService.addItem(article, 2);
        assertThat(shoppingCartService.getShoppingCart().getContent().get(article)).as("check article: %s quantity", article.getProductFullName()).isEqualTo(2);
        shoppingCartService.addItem(article, 1);
        assertThat(shoppingCartService.getShoppingCart().getContent().get(article)).as("check article: %s quantity", article.getProductFullName()).isEqualTo(3);
    }

    @Test
    void addItemsTest(){
        Map<Article, Integer> articleIntegerMap = new HashMap<>();
        articleIntegerMap.put(article, 2);
        shoppingCartService.addItems(articleIntegerMap);
        assertThat(shoppingCartService.getShoppingCart().getContent().get(article)).as("check article: %s quantity", article.getProductFullName()).isEqualTo(2);
    }

    @Test
    void getCartPriceTest(){
        shoppingCartService.addItem(article, 2);
        assertThat(shoppingCartService.getCartPrice()).as("check carts Price").isEqualTo(article.getPrice() * 2);
    }

    @Test
    void getCartPriceTestEmpty(){
        assertThat(shoppingCartService.getCartPrice()).as("check carts Price").isEqualTo(0);
    }

}
