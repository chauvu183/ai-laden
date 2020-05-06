package com.haw.lebensmittelladen.article.services;

import com.haw.lebensmittelladen.article.domain.datatypes.Credentials;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.entities.ShoppingCart;
import com.haw.lebensmittelladen.article.gateways.PaymentGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ShoppingCartService {

    @Autowired
    PaymentGateway payment;

    public double getCartPrice(ShoppingCart cart){
        return Math.round(cart.getContent().entrySet().stream().mapToDouble(kv -> kv.getKey().getPrice()*kv.getValue()).sum()*100)/100.0;
    }

    public double payCart(ShoppingCart cart, Credentials cred){
        double cost = getCartPrice(cart);
        payment.pay(cost,cred);
        cart.resetCart();
        return cost;
    }

    public ShoppingCart createCart(){
        return new ShoppingCart();
    }

    public ShoppingCart addItem(ShoppingCart cart, Article art, int amt){
        cart.addToCart(art, amt);
        return cart;
    }

    public ShoppingCart addItems(Map<Article,Integer> contents){
        return addItems(createCart(),contents);
    }

    public ShoppingCart addItems(ShoppingCart cart, Map<Article,Integer> contents){
        contents.entrySet().forEach(kv -> cart.addToCart(kv.getKey(),kv.getValue()));
        return cart;
    }

}
