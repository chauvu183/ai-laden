package com.haw.lebensmittelladen.article.services;

import com.haw.lebensmittelladen.article.domain.dtos.ArticleBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticleSoldDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesBuyDTO;
import com.haw.lebensmittelladen.article.domain.dtos.ArticlesSoldDTO;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import com.haw.lebensmittelladen.article.exceptions.ArticleNotFoundException;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import com.haw.lebensmittelladen.article.gateways.PaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    PaymentGateway payment;
    @Autowired
    ArticleRepository articleRepository;

    public ArticlesSoldDTO payForProducts(ArticlesBuyDTO articlesBuyDTO) throws ArticleNotFoundException, PaymentProviderException {
        //todo: "reserve" articles and undo action when payment failed

        Map<Article, Integer> articlesAmountToBuyMap = new HashMap<>();
        List<ArticleSoldDTO> soldList = new ArrayList<>();
        double sumToPay = 0.0;

        for (ArticleBuyDTO a : articlesBuyDTO.getArticles()) {
            Article articleEntity = articleRepository
                    .findByProductFullNameIgnoreCase(a.getProductFullName())
                    .orElseThrow(() -> new ArticleNotFoundException(a.getProductFullName()));

            int amount = a.getAmount();

            if(!articlesAmountToBuyMap.containsKey(articleEntity)){
                articlesAmountToBuyMap
                        .put(articleEntity, amount);
            }
            else{
                articlesAmountToBuyMap
                        .put(articleEntity, articlesAmountToBuyMap
                                .get(articleEntity));
            }

            sumToPay += articleEntity.getPrice() * amount;
            soldList.add(new ArticleSoldDTO(articleEntity,amount));

            //articleEntity.takeOutOfStock(a.getAmount());
        }
        ArticlesSoldDTO checkoutReference = new ArticlesSoldDTO(soldList,sumToPay);

        payment.pay(sumToPay, articlesBuyDTO.getPaymentDetails().getIBan());

        for (Map.Entry<Article, Integer> articleAmount : articlesAmountToBuyMap.entrySet()) {
            articleAmount.getKey().takeOutOfStock(articleAmount.getValue());
            articleRepository.save(articleAmount.getKey());
        }
        return checkoutReference;

    }
}
