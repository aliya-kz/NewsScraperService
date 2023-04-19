package org.zhumagulova.newsscraperservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zhumagulova.newsscraperservice.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByArticleLink(String articleLink);

}
