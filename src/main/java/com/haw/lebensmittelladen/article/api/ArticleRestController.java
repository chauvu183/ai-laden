package com.haw.lebensmittelladen.article.api;

import com.haw.lebensmittelladen.article.domain.dtos.ArticleBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesSoldDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.ArticleNotFoundException;
import com.haw.lebensmittelladen.article.exceptions.ArticlesOutOfStockException;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import com.haw.lebensmittelladen.article.exceptions.ProductAlreadyExistsException;
import com.haw.lebensmittelladen.article.services.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.haw.lebensmittelladen.article.exceptions.ArticlesOutOfStockException.formatArticlesNameList;

@RestController
@RequestMapping(path = "/articles")
@Api(value = "/articles", tags = "Articles")
public class ArticleRestController {

    private final ArticleRepository articleRepository;
    private final PaymentService paymentService;

    @Autowired
    public ArticleRestController(ArticleRepository articleRepository, PaymentService paymentService) {
        this.articleRepository = articleRepository;
        this.paymentService = paymentService;
    }

    @ApiOperation(value = "Get a article by name", response = Article.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved article"),
            @ApiResponse(code = 404, message = "Article is not found")
    })
    @GetMapping(value = "/{name}")
    public Article getArticle(@PathVariable("name") String name) throws ArticleNotFoundException {
        return articleRepository
                .findByProductFullNameIgnoreCase(name)
                .orElseThrow(() -> ArticleNotFoundException.productName(name));
    }

    @ApiOperation(value = "Get articles", response = Article.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved articles"),
    })
    @GetMapping
    public List<Article> getArticles(@RequestParam(value = "productName", required = false, defaultValue = "") String productName) {
        if (productName.isBlank()) {
            return articleRepository.findAll();
        } else {
            return articleRepository.findAllByProductNameIgnoreCase(productName);
        }
    }

    @ApiOperation(value = "Get a article-categories", response = String.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved categories"),
    })
    @GetMapping(value = "/categories")
    public List<String> getProductNames() {
        return articleRepository.findAll().stream().map(Article::getProductName).distinct().collect(Collectors.toList());
    }

    @ApiOperation(value = "Create an article")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created article"),
            @ApiResponse(code = 400, message = "productFullName already exists"),
            @ApiResponse(code = 400, message = "invalid parameter")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addArticle(@Valid @RequestBody ArticleCreateDTO articleCreateDTO) throws ProductAlreadyExistsException {
        if (articleRepository.findByProductFullNameIgnoreCase(articleCreateDTO.getProductFullName()).isPresent()) {
            throw new ProductAlreadyExistsException(articleCreateDTO.getProductFullName());
        }
        return articleRepository.save(Article.of(articleCreateDTO)).getId().toString();
    }

    @ApiOperation(value = "Buy a set of articles", response = ArticlesSoldDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully bought all articles"),
            @ApiResponse(code = 404, message = "articles not found"),
            @ApiResponse(code = 400, message = "invalid parameter"),
    })
    @PostMapping(value = "/buy")
    @ResponseStatus(HttpStatus.OK)
    public ArticlesSoldDTO buyArticles(@Valid @RequestBody ArticlesBuyDTO articlesBuyDTO) throws ArticlesOutOfStockException, PaymentProviderException, ArticleNotFoundException {
        /*List<String> productsOutOfStockNames = articlesBuyDTO.getArticles().stream()
                .filter(a -> !articleRepository.findByProductFullNameIgnoreCase(a.getProductFullName())
                        .get()
                        .enoughInStock(a.getAmount()))
                .map(ArticleBuyDTO::getProductFullName).collect(Collectors.toList());*/
        List<Article> articles =
                articleRepository.findByProductFullNames(
                        articlesBuyDTO
                                .getArticles()
                                .stream()
                                .map(ArticleBuyDTO::getProductFullName)
                                .collect(Collectors.toList())
                );

        Map<String, Article> articleMap = articles.stream().collect(Collectors.toMap(Article::getProductFullName, a -> a));

        for (ArticleBuyDTO buyArticle : articlesBuyDTO.getArticles()) {
            if (!articleMap.containsKey(buyArticle.getProductFullName())) {
                ArticleNotFoundException.productName(buyArticle.getProductFullName());
            }
        }

        List<String> productsOutOfStockNames = articlesBuyDTO.getArticles().stream()
                .filter(buyArticle -> articleMap.get(buyArticle.getProductFullName()).enoughInStock(buyArticle.getAmount()))
                .map(ArticleBuyDTO::getProductFullName).collect(Collectors.toList());

        if (!productsOutOfStockNames.isEmpty()) {
            throw new ArticlesOutOfStockException(formatArticlesNameList(productsOutOfStockNames));
        }
        return paymentService.payForProducts(articlesBuyDTO);
    }

}
