package com.haw.lebensmittelladen.article.services;

import com.haw.lebensmittelladen.Application;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleBuyBankDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesSoldDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.ArticlesOutOfStockException;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import com.haw.lebensmittelladen.article.gateways.PaymentGateway;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureJsonTesters
@ActiveProfiles(profiles = "testing")
public class PaymentServiceTest {

    private final String testIban = "DE11111111111111111111";
    @LocalServerPort
    private int port;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private PaymentService paymentService;
    @MockBean
    private PaymentGateway paymentGateway;
    private Article articleBanana;
    private Article articleCherry;
    private Map<String, Article> articleMap;

    @BeforeEach
    void setUp() {
        this.articleRepository.deleteAll();

        articleBanana = articleRepository.save(new Article("Banana", "Edeka Bio Banana", "pc", 1, "Edeka", 1.55, 1));
        articleCherry = articleRepository.save(new Article("Cherry", "Edeka Bio Cherry", "pc", 1, "Edeka", 2.44, 2));
        RestAssured.port = port;
        RestAssured.basePath = "";
        articleMap = Map.of(articleBanana.getProductFullName(), new Article(articleBanana), articleCherry.getProductFullName(), new Article(articleCherry));
    }

    @Test
    void buySingleArticleSuccess() throws PaymentProviderException, ArticlesOutOfStockException {
        double totalPrice = articleBanana.getPrice();
        ArticleBuyDTO articleBuy = new ArticleBuyDTO(articleBanana.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = Collections.singletonList(articleBuy);
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        ArticlesSoldDTO soldDTOs = paymentService.payForProducts(buyWrapper, this.articleMap);

        assertEquals(soldDTOs.getTotalPrice(), totalPrice, "unexpected Totalprice");

        Optional<Article> boughtItemOptional = articleRepository.findByProductFullNameIgnoreCase(articleBuy.getProductFullName());
        Assert.isTrue(boughtItemOptional.isPresent(), "Product not found");
        assertEquals(articleBanana.getQuantity() - 1, boughtItemOptional.get().getQuantity(), "items did not get deleted from database");
    }

    @Test
    void buyMultipleArticlesSuccess() throws PaymentProviderException, ArticlesOutOfStockException {
        double totalPrice = articleBanana.getPrice() + articleCherry.getPrice();
        ArticleBuyDTO articleBuy1 = new ArticleBuyDTO(articleBanana.getProductFullName(), 1);
        ArticleBuyDTO articleBuy2 = new ArticleBuyDTO(articleCherry.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = new ArrayList<>(Arrays.asList(articleBuy1, articleBuy2));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        ArticlesSoldDTO soldDTOs = paymentService.payForProducts(buyWrapper, this.articleMap);

        assertEquals(soldDTOs.getTotalPrice(), totalPrice, "unexpected Totalprice");


        Optional<Article> boughtItemOptional1 = articleRepository.findByProductFullNameIgnoreCase(articleBuy1.getProductFullName());
        Assert.isTrue(boughtItemOptional1.isPresent(), "Product not found");
        assertEquals(articleBanana.getQuantity() - 1, boughtItemOptional1.get().getQuantity(), "items did not get deleted from database");

        Optional<Article> boughtItemOptional2 = articleRepository.findByProductFullNameIgnoreCase(articleBuy2.getProductFullName());
        Assert.isTrue(boughtItemOptional2.isPresent(), "Product not found");
        assertEquals(articleCherry.getQuantity() - 1, boughtItemOptional2.get().getQuantity(), "items did not get deleted from database");
    }

    @Test
    void buySameArticleMultipleTimesSucess() throws PaymentProviderException, ArticlesOutOfStockException {
        double totalPrice = articleCherry.getPrice() + articleCherry.getPrice();
        ArticleBuyDTO articleBuy1 = new ArticleBuyDTO(articleCherry.getProductFullName(), 1);
        ArticleBuyDTO articleBuy2 = new ArticleBuyDTO(articleCherry.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = new ArrayList<>(Arrays.asList(articleBuy1, articleBuy2));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        ArticlesSoldDTO soldDTOs = paymentService.payForProducts(buyWrapper, this.articleMap);

        assertEquals(soldDTOs.getTotalPrice(), totalPrice, "unexpected Totalprice");

        Optional<Article> boughtItemOptional1 = articleRepository.findByProductFullNameIgnoreCase(articleBuy1.getProductFullName());
        Assert.isTrue(boughtItemOptional1.isPresent(), "Product not found");
        assertEquals(articleCherry.getQuantity() - articleBuy1.getAmount() - articleBuy2.getAmount(),
                boughtItemOptional1.get().getQuantity(), "items did not get deleted from database");
    }

    @Test
    void buyMultipleArticlesNotFoundFail() {
        ArticleBuyDTO articleBuy1 = new ArticleBuyDTO("articleshouldnotbeFound!", 1);
        ArticleBuyDTO articleBuy2 = new ArticleBuyDTO(articleBanana.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = new ArrayList<>(Arrays.asList(articleBuy1, articleBuy2));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        assertThrows(NullPointerException.class, () -> paymentService.payForProducts(buyWrapper, this.articleMap));

        Optional<Article> boughtItemOptional2 = articleRepository.findByProductFullNameIgnoreCase(articleBuy2.getProductFullName());
        Assert.isTrue(boughtItemOptional2.isPresent(), "Product not found");
        assertEquals(articleBanana.getQuantity(), boughtItemOptional2.get().getQuantity(), "Quantity should not have been changed.");
    }

    @Test
    void buySameArticleMultipleTimesOutOfStockFail() {
        ArticleBuyDTO articleBuy1 = new ArticleBuyDTO(articleBanana.getProductFullName(), 1);
        ArticleBuyDTO articleBuy2 = new ArticleBuyDTO(articleBanana.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = new ArrayList<>(Arrays.asList(articleBuy1, articleBuy2));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        assertThrows(ArticlesOutOfStockException.class, () -> paymentService.payForProducts(buyWrapper, this.articleMap));

        Optional<Article> boughtItemOptional1 = articleRepository.findByProductFullNameIgnoreCase(articleBuy1.getProductFullName());
        Assert.isTrue(boughtItemOptional1.isPresent(), "Product not found");
        assertEquals(articleBanana.getQuantity(), boughtItemOptional1.get().getQuantity(), "Quantity should not have been changed.");
    }

    @Test
    void buyArticlePayFailRollback() throws PaymentProviderException {
        Mockito.doThrow(new PaymentProviderException("error"))
                .when(paymentGateway)
                .pay(Mockito.anyDouble(), Mockito.anyString());
        ArticleBuyDTO articleBuy1 = new ArticleBuyDTO(articleBanana.getProductFullName(), 1);
        ArticleBuyBankDTO bank = new ArticleBuyBankDTO(testIban);
        List<ArticleBuyDTO> buyList = new ArrayList<>(Collections.singletonList(articleBuy1));
        ArticlesBuyDTO buyWrapper = new ArticlesBuyDTO(buyList, bank);

        assertThrows(PaymentProviderException.class, () -> paymentService.payForProducts(buyWrapper, this.articleMap));

        Optional<Article> boughtItemOptional1 = articleRepository.findByProductFullNameIgnoreCase(articleBuy1.getProductFullName());
        Assert.isTrue(boughtItemOptional1.isPresent(), "Product not found");
        assertEquals(articleBanana.getQuantity(), boughtItemOptional1.get().getQuantity(), "Quantity should have been rolledback thus not changed.");
    }

}
