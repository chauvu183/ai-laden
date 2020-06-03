package com.haw.lebensmittelladen.article.api;

import com.google.gson.Gson;
import com.haw.lebensmittelladen.Application;
import com.haw.lebensmittelladen.article.domain.dtos.*;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.ArticlesOutOfStockException;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import com.haw.lebensmittelladen.article.gateways.BankPaymentGateway;
import com.haw.lebensmittelladen.article.services.PaymentService;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    private PaymentService paymentService;

    private Article article;
    private Article articleCherry;

    private final String testIban = "DE11111111111111111111";

    @BeforeEach
    void setUp() throws PaymentProviderException {
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
    void buySingleArticleSuccess() throws ArticlesOutOfStockException, PaymentProviderException {
        float totalPrice = (float) article.getPrice();
        Mockito.when(paymentService.payForProducts(Mockito.any(), Mockito.any())).thenReturn(new ArticlesSoldDTO(new ArrayList<>(), totalPrice));
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
                body("totalPrice", Matchers.equalTo(totalPrice));

        /*Optional<Article> boughtItemOptional = articleRepository.findByProductFullNameIgnoreCase(articleBuy.getProductFullName());
        Assert.isTrue(boughtItemOptional.isPresent(), "Product not found");
        assertEquals(article.getQuantity() - 1, boughtItemOptional.get().getQuantity(), "items did not get deleted from database");*/
    }

    @Test
    void buyMultipleArticlesSuccess() throws PaymentProviderException, ArticlesOutOfStockException {
        float totalPrice = (float) (article.getPrice() + articleCherry.getPrice());
        Mockito.when(paymentService.payForProducts(Mockito.any(), Mockito.any())).thenReturn(new ArticlesSoldDTO(new ArrayList<>(), totalPrice));
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
                body("totalPrice", Matchers.equalTo(totalPrice));


        /*Optional<Article> boughtItemOptional1 = articleRepository.findByProductFullNameIgnoreCase(articleBuy1.getProductFullName());
        Assert.isTrue(boughtItemOptional1.isPresent(), "Product not found");
        assertEquals(article.getQuantity() - 1, boughtItemOptional1.get().getQuantity(), "items did not get deleted from database");

        Optional<Article> boughtItemOptional2 = articleRepository.findByProductFullNameIgnoreCase(articleBuy2.getProductFullName());
        Assert.isTrue(boughtItemOptional2.isPresent(), "Product not found");
        assertEquals(articleCherry.getQuantity() - 1, boughtItemOptional2.get().getQuantity(), "items did not get deleted from database");*/
    }

    @Test
    void buySameArticleMultipleTimesSucess() throws PaymentProviderException, ArticlesOutOfStockException {
        float totalPrice = (float) (articleCherry.getPrice() + articleCherry.getPrice());
        Mockito.when(paymentService.payForProducts(Mockito.any(), Mockito.any())).thenReturn(new ArticlesSoldDTO(new ArrayList<>(), totalPrice));
        ArticleBuyDTO articleBuy1 = new ArticleBuyDTO(articleCherry.getProductFullName(), 1);
        ArticleBuyDTO articleBuy2 = new ArticleBuyDTO(articleCherry.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = new ArrayList<>(Arrays.asList(articleBuy1, articleBuy2));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        String request = new Gson().toJson(buyWrapper);
        given().body(request).contentType(ContentType.JSON).
                when().
                post("/articles/buy").
                then().log().all().
                statusCode(HttpStatus.OK.value()).
                body("totalPrice", Matchers.equalTo(totalPrice));

        /*
        Optional<Article> boughtItemOptional1 = articleRepository.findByProductFullNameIgnoreCase(articleBuy1.getProductFullName());
        Assert.isTrue(boughtItemOptional1.isPresent(), "Product not found");
        assertEquals(articleCherry.getQuantity() - articleBuy1.getAmount() - articleBuy2.getAmount(),
                boughtItemOptional1.get().getQuantity(), "items did not get deleted from database");
         */
    }

    @Test
    void buyMultipleArticlesNotFoundFail() throws PaymentProviderException, ArticlesOutOfStockException {
        Mockito.when(paymentService.payForProducts(Mockito.any(), Mockito.any())).thenThrow(ArticlesOutOfStockException.class);
        ArticleBuyDTO articleBuy1 = new ArticleBuyDTO("articleshouldnotbeFound!", 1);
        ArticleBuyDTO articleBuy2 = new ArticleBuyDTO(article.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = new ArrayList<>(Arrays.asList(articleBuy1, articleBuy2));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        String request = new Gson().toJson(buyWrapper);
        given().body(request).contentType(ContentType.JSON).
                when().
                post("/articles/buy").
                then().log().all().
                statusCode(HttpStatus.NOT_FOUND.value());

        /*
        Optional<Article> boughtItemOptional2 = articleRepository.findByProductFullNameIgnoreCase(articleBuy2.getProductFullName());
        Assert.isTrue(boughtItemOptional2.isPresent(), "Product not found");
        assertEquals(article.getQuantity(), boughtItemOptional2.get().getQuantity(), "items did not get deleted from database");
         */
    }

    /*@Test
    void buySameArticleMultipleTimesOutOfStockFail() throws PaymentProviderException {
        Mockito.when(paymentService.payForProducts(Mockito.any(), Mockito.any())).thenThrow(ArticlesOutOfStockException.class);
        ArticleBuyDTO articleBuy1 = new ArticleBuyDTO(article.getProductFullName(), 1);
        ArticleBuyDTO articleBuy2 = new ArticleBuyDTO(article.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = new ArrayList<>(Arrays.asList(articleBuy1, articleBuy2));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        String request = new Gson().toJson(buyWrapper);
        given().body(request).contentType(ContentType.JSON).
                when().
                post("/articles/buy").
                then().log().all().
                statusCode(HttpStatus.BAD_REQUEST.value());

        Optional<Article> boughtItemOptional1 = articleRepository.findByProductFullNameIgnoreCase(articleBuy1.getProductFullName());
        Assert.isTrue(boughtItemOptional1.isPresent(), "Product not found");
        //toDo why? assertEquals(article.getQuantity(), boughtItemOptional1.get().getQuantity(), "items did not get deleted from database");
    }*/

    @Test
    void buyArticlePayFailRollback() throws PaymentProviderException, ArticlesOutOfStockException {
        Mockito.when(paymentService.payForProducts(Mockito.any(), Mockito.any())).thenThrow(PaymentProviderException.class);
        ArticleBuyDTO articleBuy1 = new ArticleBuyDTO(article.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = new ArrayList<>(Collections.singletonList(articleBuy1));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        String request = new Gson().toJson(buyWrapper);
        given().body(request).contentType(ContentType.JSON).
                when().
                post("/articles/buy").
                then().log().all().
                statusCode(HttpStatus.BAD_GATEWAY.value());

        /*
        Optional<Article> boughtItemOptional1 = articleRepository.findByProductFullNameIgnoreCase(articleBuy1.getProductFullName());
        Assert.isTrue(boughtItemOptional1.isPresent(), "Product not found");
        assertEquals(article.getQuantity(), boughtItemOptional1.get().getQuantity(), "items did not get deleted from database");
         */
    }

}
