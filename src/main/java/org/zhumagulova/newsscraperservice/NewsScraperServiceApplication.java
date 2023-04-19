package org.zhumagulova.newsscraperservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.zhumagulova.newsscraperservice.exception.ScraperException;
import org.zhumagulova.newsscraperservice.service.NewsScraperService;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "org.zhumagulova.newsscraperservice.repository")
public class NewsScraperServiceApplication implements CommandLineRunner {

    private final NewsScraperService newsScraperService;

    public NewsScraperServiceApplication(NewsScraperService newsScraperService) {
        this.newsScraperService = newsScraperService;
    }

    public static void main(String[] args) {
        SpringApplication.run(NewsScraperServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws ScraperException {
        newsScraperService.scrape();
    }
}
