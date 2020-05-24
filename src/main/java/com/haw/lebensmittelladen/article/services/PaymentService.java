package com.haw.lebensmittelladen.article.services;

import com.haw.lebensmittelladen.article.domain.dtos.ArticleBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesBuyDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.ArticleNotFoundException;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import com.haw.lebensmittelladen.article.gateways.PaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    PaymentGateway payment;
    @Autowired
    ArticleRepository articleRepository;

    public double payForProducts(ArticlesBuyDTO articlesBuyDTO) throws ArticleNotFoundException, PaymentProviderException {
        //todo: "reserve" articles and undo action when payment failed

        Map<Article, Integer> articlesAmountToBuyMap = new HashMap<>();
        double sumToPay = 0.0;

        for (ArticleBuyDTO a : articlesBuyDTO.getArticles()) {
            Article articleEntity = articleRepository
                    .findByProductNameIgnoreCase(a.getName())
                    .orElseThrow(() -> new ArticleNotFoundException(a.getName()));

            int amount = a.getAmount();

            articlesAmountToBuyMap
                    .put(articleEntity, amount);

            sumToPay += articleEntity.getPrice() * amount;

            //articleEntity.takeOutOfStock(a.getAmount());
        }

        payment.pay(sumToPay, articlesBuyDTO.getPaymentDetails().getIBan());

        for (Map.Entry<Article, Integer> articleAmount : articlesAmountToBuyMap.entrySet()) {
            articleAmount.getKey().takeOutOfStock(articleAmount.getValue());
        }
        return sumToPay;

    }
}
