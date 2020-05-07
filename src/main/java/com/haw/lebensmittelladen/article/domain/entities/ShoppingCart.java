package com.haw.lebensmittelladen.article.domain.entities;

import lombok.Data;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

//@Entity
//@Data
public class ShoppingCart {
    private Map<Article, Integer> content = new HashMap<>();

    public void addToCart(Article art) {
        addToCart(art, 1);
    }

    public void addToCart(Article art, int amt) {
        if (content.containsKey(art)) {
            content.replace(art, content.get(art) + amt);
        } else {
            content.put(art, amt);
        }
    }

    public void removeFromCart(Article art) {
        removeFromCart(art, 1);
    }

    public void removeFromCart(Article art, int amt) {
        if (content.containsKey(art)) {
            int cartAmt = content.get(art);
            if (cartAmt > amt) {
                content.replace(art, cartAmt - amt);
            } else {
                content.remove(art);
            }
        }
    }

    public void resetCart() {
        content = new HashMap<>();
    }

    public Map<Article, Integer> getContent() {
        return new HashMap<>(content);
    }

}
