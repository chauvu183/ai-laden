package com.haw.lebensmittelladen.article.domain.repositories;

import com.haw.lebensmittelladen.article.domain.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByProductNameIgnoreCase(String productName);
}
