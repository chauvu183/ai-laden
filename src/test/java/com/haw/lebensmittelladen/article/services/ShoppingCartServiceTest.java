package com.haw.lebensmittelladen.article.services;

import com.haw.lebensmittelladen.Application;
import com.haw.lebensmittelladen.article.domain.entities.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureJsonTesters
@ActiveProfiles(profiles = "testing")
class ShoppingCartServiceTest {

    @Autowired
    private ShoppingCartService shoppingCartService;

    private ShoppingCart shoppingCart;

    @BeforeEach
    void setUp(){
        this.shoppingCart = shoppingCartService.createCart();
        assertThat(shoppingCart != null);
    }

}
