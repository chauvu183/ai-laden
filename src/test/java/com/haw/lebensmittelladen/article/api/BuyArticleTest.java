package com.haw.lebensmittelladen.article.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.haw.lebensmittelladen.Application;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleBuyBankDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesBuyDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import com.haw.lebensmittelladen.article.gateways.BankPaymentGateway;
import com.haw.lebensmittelladen.article.gateways.PaymentGateway;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureJsonTesters
@ActiveProfiles(profiles = "testing")
public class BuyArticleTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ArticleRepository articleRepository;

    @MockBean
    private BankPaymentGateway bankPaymentGateway;

    private Article article;

    @BeforeEach
    void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        this.articleRepository.deleteAll();

        article = articleRepository.save(new Article("Banane", "Edeka Bio Banane","gr",500, "Edeka", 1.55, 1));
        articleRepository.save(new Article("Kirsche", "Edeka Bio Kirsche", "gr",500,"Edeka", 2.44, 2));
        RestAssured.port = port;
        RestAssured.basePath = "";
    }
//todo tests
    @Test
    void buySingleArticleSuccess() throws PaymentProviderException {
        //https://www.baeldung.com/mockito-void-methods
        Mockito.doNothing().when(bankPaymentGateway).pay(Mockito.anyDouble(),Mockito.anyString());

        ArticleBuyDTO articleBuy = new ArticleBuyDTO("Edeka Bio Banane",1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO("DE75512108001245126199");
        List<ArticleBuyDTO> buyList = new ArrayList<>(Arrays.asList(articleBuy));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList,bank);

        String request = new Gson().toJson(buyWrapper);
        given().body(request).contentType(ContentType.JSON).
                when().
                post("/articles/buy").
                then().
                statusCode(HttpStatus.OK.value()).
                body("totalPrice",Matchers.equalTo(1.55f));

        Article boughtItem = articleRepository.findByProductFullNameIgnoreCase("Edeka Bio Banane").get();
        Assert.isTrue(boughtItem.getQuantity()==0,"items did not get deleted from database");
    }


}
