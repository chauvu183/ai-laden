package com.haw.lebensmittelladen.article.api;

import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.ArticleNotFoundException;
import com.haw.lebensmittelladen.article.exceptions.ProductAlreadyExistsException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/articles")
@Api(value = "/articles", tags = "Articles")
public class ArticleRestController {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleRestController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @ApiOperation(value = "Get a article by name", response = Article.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved article"),
            @ApiResponse(code = 404, message = "Article is not found")
    })
    @GetMapping(value = "/{name:[\\d]+}")
    public Article getArticle(@PathVariable("name") String name) throws ArticleNotFoundException {
        return articleRepository
                .findByProductNameIgnoreCase(name)
                .orElseThrow(() -> ArticleNotFoundException.productName(name));
    }

    @ApiOperation(value = "Get articles", response = Article.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved bookings"),
    })
    @GetMapping
    public List<Article> getArticles(@RequestParam(value = "productName", required = false, defaultValue = "") String productName) throws ArticleNotFoundException {
        if (productName.isBlank()) {
            return articleRepository.findAll();
        } else {
            return Collections.singletonList(articleRepository
                    .findByProductNameIgnoreCase(productName)
                    .orElseThrow(() -> ArticleNotFoundException.productName(productName)));
        }
    }

    @ApiOperation(value = "Create an article")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created article"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addArticle(@Valid @RequestBody ArticleCreateDTO articleCreateDTO) throws ProductAlreadyExistsException {
        if(articleRepository.findByProductNameIgnoreCase(articleCreateDTO.getProductName()).isPresent()){
            throw new ProductAlreadyExistsException(articleCreateDTO.getProductName());
        }
        return articleRepository.save(Article.of(articleCreateDTO)).getId().toString();
    }

}
