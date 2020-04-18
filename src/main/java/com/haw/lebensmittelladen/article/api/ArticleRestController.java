package com.haw.lebensmittelladen.article.api;

import com.haw.lebensmittelladen.article.domain.datatypes.Barcode;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.ArticleNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping(value = "/{id:[\\d]+}")
    public Article getArticle(@PathVariable("id") Long bookingId) throws ArticleNotFoundException {
        return articleRepository
                .findById(bookingId)
                .orElseThrow(() -> new ArticleNotFoundException(bookingId));
    }

    @ApiOperation(value = "Get articles", response = Article.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved bookings"),
    })
    @GetMapping
    public List<Article> getArticles(@RequestParam(value = "barcode", required = false, defaultValue = "") String barcode, @RequestParam(value = "productName", required = false, defaultValue = "") String productName) throws ArticleNotFoundException {
        if (productName.isBlank() && barcode.isBlank()) {
            return articleRepository.findAll();
        } else if (!barcode.isEmpty()) {
            if (!Barcode.isValid(barcode)) {
                throw ArticleNotFoundException.barcode(barcode);
            } else {
                return Collections.singletonList(articleRepository
                        .findByBarcode(new Barcode(barcode))
                        .orElseThrow(() -> ArticleNotFoundException.barcode(barcode)));
            }
        } else {
            return Collections.singletonList(articleRepository
                    .findByProductNameIgnoreCase(productName)
                    .orElseThrow(() -> ArticleNotFoundException.productName(productName)));
        }
    }

}
