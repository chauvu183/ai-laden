package com.haw.lebensmittelladen.article.services;

import com.haw.lebensmittelladen.article.domain.dtos.ArticleBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleSoldDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesSoldDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.ArticlesOutOfStockException;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import com.haw.lebensmittelladen.article.gateways.PaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.haw.lebensmittelladen.article.exceptions.ArticlesOutOfStockException.formatArticlesNameList;

@Service
public class PaymentService {

    @Autowired
    PaymentGateway payment;
    @Autowired
    ArticleRepository articleRepository;

    @Transactional
    public ArticlesSoldDTO payForProducts(ArticlesBuyDTO articlesBuyDTO, Map<String, Article> articleMap) throws PaymentProviderException, ArticlesOutOfStockException {
        List<ArticleSoldDTO> soldList = new ArrayList<>();
        double sumToPay = 0.0;

        List<String> productsOutOfStockNames = new ArrayList<>();

        for (ArticleBuyDTO buyArticle : articlesBuyDTO.getArticles()) {
            Article article = articleMap.get(buyArticle.getProductFullName());
            soldList.add(new ArticleSoldDTO(article, buyArticle.getAmount()));
            sumToPay += article.getPrice() * buyArticle.getAmount();

            if (!article.takeOutOfStock(buyArticle.getAmount())) {
                productsOutOfStockNames.add(buyArticle.getProductFullName());
            }
        }

        if (!productsOutOfStockNames.isEmpty()) {
            throw new ArticlesOutOfStockException(formatArticlesNameList(productsOutOfStockNames));
        }

        ArticlesSoldDTO checkoutReference = new ArticlesSoldDTO(soldList, sumToPay);
        articleRepository.saveAll(articleMap.values());

        payment.pay(sumToPay, articlesBuyDTO.getPaymentDetails().getIBan());
        return checkoutReference;
    }
}
