package com.haw.lebensmittelladen.article.api;

import com.haw.lebensmittelladen.article.domain.dtos.ArticleCreateDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.ArticleNotFoundException;
import com.haw.lebensmittelladen.article.exceptions.ArticlesOutOfStockException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin")
@Api(value = "/admin", tags = "admin")
public class AdminRestController {

    private final ArticleRepository articleRepository;

    @Autowired
    public AdminRestController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @ApiOperation(value = "reset database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully removed all articles"),
    })
    @PostMapping(value = "/reset")
    public void resetDataBases() {
        articleRepository.deleteAll();
    }

    @ApiOperation(value = "delete one product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully removed article"),
            @ApiResponse(code = 404, message = "Article not found"),
    })
    @DeleteMapping(value = "/{id:[\\d]+}")
    public void deleteProduct(@PathVariable("id") Long productID) throws ArticleNotFoundException {
        articleRepository.delete(articleRepository.findById(productID).orElseThrow(() -> new ArticleNotFoundException(productID)));
    }

    @ApiOperation(value = "add to amount of one article")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added to article"),
            @ApiResponse(code = 404, message = "Article not found"),
            @ApiResponse(code = 400, message = "Insufficient article stock"),
    })
    @PutMapping(value = "/{id:[\\d]+}")
    public void updateProductAmount(@PathVariable("id") Long productID, @RequestBody int amount) throws ArticleNotFoundException, ArticlesOutOfStockException {
        Article article = articleRepository.findById(productID).orElseThrow(() -> new ArticleNotFoundException(productID));
        article.setQuantity(article.getQuantity() + amount);
        if (article.getQuantity() < 0) {
            throw new ArticlesOutOfStockException(article.getProductFullName());
        }
        articleRepository.save(article);
    }

    @ApiOperation(value = "add to amount of one article")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated article"),
            @ApiResponse(code = 404, message = "Article not found")
    })
    @PutMapping()
    public Article updateArticleDataByProductFullName(@Valid @RequestBody ArticleCreateDTO articleUpdate) throws ArticleNotFoundException {
        Article article = articleRepository.findByProductFullNameIgnoreCase(articleUpdate.getProductFullName()).orElseThrow(() -> new ArticleNotFoundException(articleUpdate.getProductFullName()));

        if (null == articleUpdate.getQuantity()) {
            articleUpdate.setQuantity(article.getQuantity());
        }

        Article newArticle = Article.of(articleUpdate);
        articleRepository.save(newArticle);

        return newArticle;
    }


}
