package com.haw.lebensmittelladen.article.domain.repositories;

import com.haw.lebensmittelladen.article.domain.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByProductNameIgnoreCase(String productName);
    @Query("select a from Article a where a.productFullName in ?1")
    List<Article> findByProductFullNames(List<String> productFullName);
    Optional<Article> findByProductFullNameIgnoreCase(String productFullName);
}
