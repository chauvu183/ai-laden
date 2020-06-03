package com.haw.lebensmittelladen.article.api;

import com.haw.lebensmittelladen.Application;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureJsonTesters
@ActiveProfiles(profiles = "testing")
public class AdminRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ArticleRepository articleRepository;

    private Article articleBanana;
    private Article articleCherry;
    private ArticleCreateDTO original;
    private ArticleCreateDTO newArticle;

    private final static String TEST_NAME = "Edeka Bio Banana";

    @BeforeEach
    void setUp() throws PaymentProviderException {
        this.articleRepository.deleteAll(); //this made the id's increment

        articleBanana = new Article("Banana", TEST_NAME, "pc", 1, "Edeka", 1.55, 2);
        articleCherry = new Article("Cherry", "Edeka Bio Cherry", "pc", 1, "Edeka", 2.44, 2);

        articleRepository.save(articleBanana);
        articleRepository.save(articleCherry);

        RestAssured.port = port;
        RestAssured.basePath = "";

        original = new ArticleCreateDTO(articleBanana.getProductName()
                , articleBanana.getProductFullName()
                , articleBanana.getProductSizeUnit()
                , articleBanana.getProductSize()
                , articleBanana.getCompany()
                , articleBanana.getPrice()
                , articleBanana.getQuantity());

        newArticle = new ArticleCreateDTO(articleBanana.getProductName()
                , articleBanana.getProductFullName()
                , articleBanana.getProductSizeUnit()
                , articleBanana.getProductSize()
                , articleBanana.getCompany()
                , articleBanana.getPrice()
                , articleBanana.getQuantity());

        String newCategory = "bla";
        String newUnit = "ml";
        int newSize = 500;
        String newComp = "mock";
        int newQuant = 500;

        newArticle.setProductName(newCategory);
        newArticle.setProductSizeUnit(newUnit);
        newArticle.setProductSize(newSize);
        newArticle.setCompany(newComp);
        newArticle.setQuantity(newQuant);
    }

    @Test
    void resetDatabaseSuccess() {
        Assertions.assertTrue(articleRepository.findAll().size() == 2, "Database empty at start");
        given().contentType(ContentType.JSON).
                when().
                post("/admin/reset").
                then().
                statusCode(HttpStatus.OK.value());

        Assertions.assertTrue(articleRepository.findAll().size() == 0, "Database not empty");
    }

    @Test
    void deleteOneArticleSuccess() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");
        given().contentType(ContentType.JSON).
                when().
                delete("/admin/"+getBananaID()).
                then().
                statusCode(HttpStatus.OK.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).orElse(null) == null, "article stinn in dB");
    }

    @Test
    void deleteOneArticleFail() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");
        given().contentType(ContentType.JSON).
                when().
                delete("/admin/800").
                then().
                statusCode(HttpStatus.NOT_FOUND.value());

        Assertions.assertTrue(articleRepository.findAll().size() == 2, "Database was altered");
    }

    @Test
    void updateArticleAmountSuccess() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");

        given().contentType(ContentType.JSON).
                body(1).
                when().
                put("/admin/"+getBananaID()).
                then().
                statusCode(HttpStatus.OK.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 3, "item was not added");
    }

    @Test
    void updateArticleMultipleAmountSuccess() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");

        given().contentType(ContentType.JSON).
                body(2).
                when().
                put("/admin/"+getBananaID()).
                then().
                statusCode(HttpStatus.OK.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 4, "item was not added");
    }

    @Test
    void updateArticleNegativeAmountSuccess() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");

        given().contentType(ContentType.JSON).
                body(-1).
                when().
                put("/admin/"+getBananaID()).
                then().
                statusCode(HttpStatus.OK.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 1, "item was not reduced");
    }

    @Test
    void updateArticleMultipleNegativeAmountSuccess() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");

        given().contentType(ContentType.JSON).
                body(-2).
                when().
                put("/admin/"+getBananaID()).
                then().
                statusCode(HttpStatus.OK.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 0, "item was not reduced");
    }

    @Test
    void updateArticleNegativeAmountFail() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");

        given().contentType(ContentType.JSON).
                body(-3).
                when().
                put("/admin/"+getBananaID()).
                then().
                statusCode(HttpStatus.BAD_REQUEST.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database was altered");
    }

    @Test
    void updateArticleNegativeAmountNotFound() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");

        given().contentType(ContentType.JSON).
                body(-3).
                when().
                put("/admin/800").
                then().
                statusCode(HttpStatus.NOT_FOUND.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database was altered");
    }

    @Test
    void updateArticleAmountNotFound() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");
        Assertions.assertTrue(articleRepository.findAll().size() == 2, "Database wrong size at start");
        given().contentType(ContentType.JSON).
                body(3).
                when().
                put("/admin/800").
                then().
                statusCode(HttpStatus.NOT_FOUND.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database was altered");
    }

    @Test
    void updateArticleFullSuccess() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");

        given().contentType(ContentType.JSON).
                body(newArticle).
                when().
                put("/admin").
                then().
                statusCode(HttpStatus.OK.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == newArticle.getQuantity(), "Database quantity was not altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductName().equals(newArticle.getProductName()), "Database category was not altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductSizeUnit().equals(newArticle.getProductSizeUnit()), "Database unit was not altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductSize() == newArticle.getProductSize(), "Database size was not altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getCompany().equals(newArticle.getCompany()), "Database company was not altered");
    }

    @Test
    void updateArticleFullNotFound() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");

        String notFoundName = "notfound";
        newArticle.setProductFullName(notFoundName);

        given().contentType(ContentType.JSON).
                body(newArticle).
                when().
                put("/admin").
                then().
                statusCode(HttpStatus.NOT_FOUND.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == original.getQuantity(), "Database quantity was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductName().equals(original.getProductName()), "Database category was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductSizeUnit().equals(original.getProductSizeUnit()), "Database unit was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductSize() == original.getProductSize(), "Database size was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getCompany().equals(original.getCompany()), "Database company was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(notFoundName).orElse(null)==null, "Database entry was added");
    }

    @Test
    void updateArticleFullNullQuantityFail() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");

        newArticle.setQuantity(null);

        given().contentType(ContentType.JSON).
                body(newArticle).
                when().
                put("/admin").
                then().
                statusCode(HttpStatus.BAD_REQUEST.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == original.getQuantity(), "Database quantity was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductName().equals(original.getProductName()), "Database category was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductSizeUnit().equals(original.getProductSizeUnit()), "Database unit was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductSize() == original.getProductSize(), "Database size was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getCompany().equals(original.getCompany()), "Database company was altered");
    }

    @Test
    void updateArticleFullInvalidQuantity() {
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == 2, "Database empty at start");

        newArticle.setQuantity(-1);

        given().contentType(ContentType.JSON).
                body(newArticle).
                when().
                put("/admin").
                then().
                statusCode(HttpStatus.BAD_REQUEST.value());

        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getQuantity() == original.getQuantity(), "Database quantity was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductName().equals(original.getProductName()), "Database category was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductSizeUnit().equals(original.getProductSizeUnit()), "Database unit was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getProductSize() == original.getProductSize(), "Database size was altered");
        Assertions.assertTrue(articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getCompany().equals(original.getCompany()), "Database company was altered");
    }

    private Long getBananaID(){
        return articleRepository.findByProductFullNameIgnoreCase(TEST_NAME).get().getId();
    }

}
