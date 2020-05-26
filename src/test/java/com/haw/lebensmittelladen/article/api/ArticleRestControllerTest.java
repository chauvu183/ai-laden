//package com.haw.lebensmittelladen.article.api;
//
//import com.haw.lebensmittelladen.Application;
//import com.haw.lebensmittelladen.article.domain.datatypes.Barcode;
//import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
//import com.haw.lebensmittelladen.article.domain.entities.Article;
//import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.Optional;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.hasItems;
//import static org.assertj.core.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureJsonTesters
//@ActiveProfiles(profiles = "testing")
//class ArticleRestControllerTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private ArticleRepository articleRepository;
//
//    private Article article;
//
//    @BeforeEach
//    void setUp() {
//        this.articleRepository.deleteAll();
//
//        article = articleRepository.save(new Article("Banane", "Edeka Bio Banane", "Edeka", 1.55, 1));
//        articleRepository.save(new Article("Kirsche", "Edeka Bio Kirsche", "Edeka", 2.44, 2));
//        RestAssured.port = port;
//        RestAssured.basePath = "";
//    }
//
//    @Test
//    void getAllArticlesSuccess() {
//        given().
//                when().
//                get("/articles").
//                then().
//                statusCode(HttpStatus.OK.value()).
//                body("productName", hasItems("Banane"));
//    }
//
//    @Test
//    void getArticleSuccess() {
//        given().
//                when().
//                get("/articles/{barcode}", article.getBarcode().getCode()).
//                then().
//                statusCode(HttpStatus.OK.value()).
//                body("productName", equalTo(article.getProductName()));
//    }
//
//    @Test
//    void getArticleFailBecauseOfNotFound() {
//        given().
//                when().
//                get("/articles/{barcode}", new Barcode().getCode()).
//                then().
//                statusCode(HttpStatus.NOT_FOUND.value());
//    }
//
//    @Test
//    void getArticlesByProductNameSuccess(){
//        given().
//                when().
//                get("/articles?productName=Banane").
//                then().
//                statusCode(HttpStatus.OK.value()).
//                body("productName", hasItems("Banane"));
//    }
//
//    @Test
//    void getArticlesByProductNameFail(){
//        given().
//                when().
//                get("/articles?productName=gibtsNicht").
//                then().
//                statusCode(HttpStatus.NOT_FOUND.value());
//    }
//
//    @Test
//    void addArticleSuccess(){
//        ArticleCreateDTO articleCreateDTO = new ArticleCreateDTO(new Barcode(), "Kirsche", "Edeka Bio Kirsche", "Edeka", 2.44, 2);
//        given().
//                contentType(ContentType.JSON).
//                body(articleCreateDTO).
//                when().
//                post("/articles").
//                then().
//                statusCode(HttpStatus.CREATED.value());
//        Optional<Article> article = articleRepository.findByBarcode(articleCreateDTO.getBarcode());
//        assertThat(article.isPresent()).isTrue();
//    }
//
//    @Test
//    void addArticleFailBarcodeDublicate(){
//        ArticleCreateDTO articleCreateDTO = new ArticleCreateDTO(article.getBarcode(), "Kirsche", "Edeka Bio Kirsche", "Edeka", 2.44, 2);
//        given().
//                contentType(ContentType.JSON).
//                body(articleCreateDTO).
//                when().
//                post("/articles").
//                then().
//                statusCode(HttpStatus.BAD_REQUEST.value());
//    }
//
//    @Test
//    void addArticleFailNegativeQuantity(){
//        ArticleCreateDTO articleCreateDTO = new ArticleCreateDTO(new Barcode(), "Kirsche", "Edeka Bio Kirsche", "Edeka", 2.44, -2);
//        given().
//                contentType(ContentType.JSON).
//                body(articleCreateDTO).
//                when().
//                post("/articles").
//                then().
//                statusCode(HttpStatus.BAD_REQUEST.value());
//        Optional<Article> articleOptional = articleRepository.findByBarcode(articleCreateDTO.getBarcode());
//        assertThat(articleOptional.isEmpty()).isTrue();
//    }
//
//}
