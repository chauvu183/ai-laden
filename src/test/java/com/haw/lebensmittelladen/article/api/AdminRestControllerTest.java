package com.haw.lebensmittelladen.article.api;

import com.haw.lebensmittelladen.Application;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @BeforeEach
    void setUp() throws PaymentProviderException {
        this.articleRepository.deleteAll();

        articleBanana = articleRepository.save(new Article("Banana", "Edeka Bio Banana", "pc", 1, "Edeka", 1.55, 1));
        articleCherry = articleRepository.save(new Article("Cherry", "Edeka Bio Cherry", "pc", 1, "Edeka", 2.44, 2));
        RestAssured.port = port;
        RestAssured.basePath = "";
    }



}
