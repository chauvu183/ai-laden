package com.haw.lebensmittelladen.article.api;

import com.google.gson.Gson;
import com.haw.lebensmittelladen.Application;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleBuyBankDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesBuyDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import com.haw.lebensmittelladen.article.gateways.BankPaymentGateway;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
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

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureJsonTesters
@ActiveProfiles(profiles = "testing")
class ArticleRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ArticleRepository articleRepository;

    @MockBean
    private BankPaymentGateway bankPaymentGateway;

    private Article article;
    private Article articleCherry;

    private final String testIban = "DE11111111111111111111";

    @BeforeEach
    void setUp() {
        this.articleRepository.deleteAll();

        article = articleRepository.save(new Article("Banana", "Edeka Bio Banana", "pc", 1, "Edeka", 1.55, 1));
        articleCherry = articleRepository.save(new Article("Cherry", "Edeka Bio Cherry", "pc", 1, "Edeka", 2.44, 2));
        RestAssured.port = port;
        RestAssured.basePath = "";
    }

    @Test
    void getAllArticlesSuccess() {
        given().
                when().
                get("/articles").
                then().
                statusCode(HttpStatus.OK.value()).
                body("productName", hasItems("Banana"));
    }

    @Test
    void getAllArticleCategories() {
        articleRepository.save(new Article(article.getProductName(), article.getProductFullName() + 2,
                article.getProductSizeUnit(), article.getProductSize(), article.getCompany(),
                article.getPrice(), article.getQuantity()));
        given().
                when().
                get("/articles/categories").
                then().log().all().
                statusCode(HttpStatus.OK.value()).
                body("size()", is(2));
    }

    @Test
    void getArticleSuccess() {
        given().
                when().
                get("/articles/{name}", article.getProductFullName()).
                then().
                statusCode(HttpStatus.OK.value()).
                body("productName", equalTo(article.getProductName()));
    }

    @Test
    void getArticleFailBecauseOfNotFound() {
        given().
                when().
                get("/articles/{name}", "nothingToSeeHere").
                then().
                statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getArticlesByProductNameSuccess() {
        given().
                when().
                get("/articles?productName=Banana").
                then().
                statusCode(HttpStatus.OK.value()).
                body("productName", hasItems("Banana"));
    }

    @Test
    void getArticlesByProductNameEmpty() {
        given().
                when().
                get("/articles?productName=nothingToSeeHere").
                then().
                body(hasItems());
    }

    @Test
    void addArticleSuccess() {
        ArticleCreateDTO articleCreateDTO = new ArticleCreateDTO("Cherry", "G&G Cherry", "pc", 1, "Edeka", 2.44, 2);
        given().
                contentType(ContentType.JSON).
                body(articleCreateDTO).
                when().
                post("/articles").
                then().log().all().
                statusCode(HttpStatus.CREATED.value());
        Optional<Article> article = articleRepository.findByProductFullNameIgnoreCase(articleCreateDTO.getProductFullName());
        assertThat(article.isPresent()).isTrue();
    }

    @Test
    void addArticleFailProductFullNameDuplicate() {
        ArticleCreateDTO articleCreateDTO = new ArticleCreateDTO("Cherry", article.getProductFullName(), "pc", 1, "Edeka", 2.44, 2);
        given().
                contentType(ContentType.JSON).
                body(articleCreateDTO).
                when().
                post("/articles").
                then().log().all().
                statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void addArticleFailNegativeQuantity() {
        ArticleCreateDTO articleCreateDTO = new ArticleCreateDTO("Cherry", "G&G Cherry", "pc", 1, "Edeka", 2.44, -2);
        given().
                contentType(ContentType.JSON).
                body(articleCreateDTO).
                when().
                post("/articles").
                then().
                statusCode(HttpStatus.BAD_REQUEST.value());
        Optional<Article> articleOptional = articleRepository.findByProductFullNameIgnoreCase(articleCreateDTO.getProductFullName());
        assertThat(articleOptional.isEmpty()).isTrue();
    }

    @Test
    void buySingleArticleSuccess() throws PaymentProviderException {
        Mockito.doNothing().when(bankPaymentGateway).pay(Mockito.anyDouble(), Mockito.anyString());

        ArticleBuyDTO articleBuy = new ArticleBuyDTO(article.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = Collections.singletonList(articleBuy);
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        String request = new Gson().toJson(buyWrapper);
        given().body(request).contentType(ContentType.JSON).
                when().
                post("/articles/buy").
                then().log().all().
                statusCode(HttpStatus.OK.value()).
                body("totalPrice", Matchers.equalTo(1.55f));

        Optional<Article> boughtItemOptional = articleRepository.findByProductFullNameIgnoreCase(articleBuy.getProductFullName());
        Assert.isTrue(boughtItemOptional.isPresent(), "Product not found");
        Assert.isTrue(boughtItemOptional.get().getQuantity() == article.getQuantity() - 1, "items did not get deleted from database");
    }

    @Test
    void buyMultipleArticlesSuccess() throws PaymentProviderException {
        Mockito.doNothing().when(bankPaymentGateway).pay(Mockito.anyDouble(), Mockito.anyString());

        ArticleBuyDTO articleBuy1 = new ArticleBuyDTO(article.getProductFullName(), 1);
        ArticleBuyDTO articleBuy2 = new ArticleBuyDTO(articleCherry.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = new ArrayList<>(Arrays.asList(articleBuy1, articleBuy2));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        String request = new Gson().toJson(buyWrapper);
        given().body(request).contentType(ContentType.JSON).
                when().
                post("/articles/buy").
                then().
                statusCode(HttpStatus.OK.value()).
                body("totalPrice", Matchers.equalTo(1.55f+2.44f));

        Optional<Article> boughtItemOptional1 = articleRepository.findByProductFullNameIgnoreCase(articleBuy1.getProductFullName());
        Assert.isTrue(boughtItemOptional1.isPresent(), "Product not found");
        Assert.isTrue(boughtItemOptional1.get().getQuantity() == article.getQuantity() - 1, "items did not get deleted from database");

        Optional<Article> boughtItemOptional2 = articleRepository.findByProductFullNameIgnoreCase(articleBuy2.getProductFullName());
        Assert.isTrue(boughtItemOptional2.isPresent(), "Product not found");
        Assert.isTrue(boughtItemOptional2.get().getQuantity() == articleCherry.getQuantity() - 1, "items did not get deleted from database");
    }

}
