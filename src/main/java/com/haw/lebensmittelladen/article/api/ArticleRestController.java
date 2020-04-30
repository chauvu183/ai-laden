package com.haw.lebensmittelladen.article.api;

import com.haw.lebensmittelladen.article.domain.datatypes.Barcode;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.ArticleNotFoundException;
import com.haw.lebensmittelladen.article.exceptions.BarcodeAlreadyExistsException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
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

    @ApiOperation(value = "Get a article by Id", response = Article.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved article"),
            @ApiResponse(code = 404, message = "Article is not found")
    })
    @GetMapping(value = "/{barcode:[\\d]+}")
    public Article getArticle(@PathVariable("barcode") String barcode) throws ArticleNotFoundException {
        return articleRepository
                .findByBarcode(new Barcode(barcode))
                .orElseThrow(() -> ArticleNotFoundException.barcode(barcode));
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
    public String addArticle(@Valid @RequestBody ArticleCreateDTO articleCreateDTO) throws BarcodeAlreadyExistsException {
        if(articleRepository.findByBarcode(articleCreateDTO.getBarcode()).isPresent()){
            throw new BarcodeAlreadyExistsException(articleCreateDTO.getBarcode().getCode());
        }
        return articleRepository.save(Article.of(articleCreateDTO)).getBarcode().getCode();
    }

}
