package org.zhumagulova.newsscraperservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.zhumagulova.newsscraperservice.api.model.NewsResponse;
import org.zhumagulova.newsscraperservice.entity.News;
import org.zhumagulova.newsscraperservice.exception.ScraperException;
import org.zhumagulova.newsscraperservice.repository.NewsRepository;
import org.zhumagulova.newsscraperservice.service.mapper.NewsMapper;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TengrinewsScraperService implements NewsScraperService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String NEWS_PORTAL_URL = "http://news-server:8080/api/1";

    private static final String NEWS_PAGE_URL = "https://tengrinews.kz";
    private static final String KAZAKHSTAN_NEWS_PAGE_URL = NEWS_PAGE_URL + "/kazakhstan_news";

    private final NewsRepository newsRepository;

    private final NewsMapper newsMapper;

    @Override
    @Transactional
    @Scheduled(cron = "0 */2 * * * *")
    public void scrape() throws ScraperException {
        connectToNewsPortalUsingWebClient();
        try {
            Document document = Jsoup.connect(KAZAKHSTAN_NEWS_PAGE_URL).get();
            List<News> newsList = new ArrayList<>();
            Elements articleList = document.body().select(".tn-article-grid > .tn-article-item");
            for (Element articleListElement : articleList) {
                String articleLinkPart = articleListElement.select("a").attr("href");
                if (!articleLinkPart.isBlank()) { // check if element is not an ad
                    String articleLink = NEWS_PAGE_URL + articleLinkPart;
                    if (!newsRepository.existsByArticleLink(articleLink)) {
                        Document article = Jsoup.connect(articleLink).get();
                        Elements articleTitle = article.select(".tn-content-title");
                        String title = articleTitle.textNodes().get(0).getWholeText();
                        String postDate = articleTitle.select("span").text();
                        StringBuilder contentBuilder = new StringBuilder();
                        List<String> paragraphs = article.select(".tn-news-text > p").eachText();
                        for (int i = 0; i < paragraphs.size() - 2; i++) {
                            contentBuilder.append(paragraphs.get(i));
                        }
                        String content = contentBuilder.toString();
                        if (content.length() > 2048) {
                            content = content.substring(0, 2047);
                        }
                        News news = newsMapper.map(title, postDate, content, articleLink);
                        newsList.add(news);
                    }
                }
            }
            log.debug("[Tengrinews] {} news were written into the database", newsList.size());
            newsRepository.saveAll(newsList);
        } catch (IOException e) {
            log.error("[Tengrinews] Caught error during scraping", e);
            throw new ScraperException("Cannot get news from Tengrinews", e);
        }
    }

    public void connectToNewsPortalUsingRestTemplate() {
        try {
            NewsResponse response = restTemplate.getForObject(NEWS_PORTAL_URL, NewsResponse.class);
            log.debug("response title " + response.getTitle());
        } catch (Exception exception) {
            throw new ResourceAccessException("NewsPortal is not running");
        }

    }

    public void connectToNewsPortalUsingWebClient() {
        try {
            WebClient client = WebClient.builder().baseUrl(NEWS_PORTAL_URL).build();
            Mono<NewsResponse> response = client.get()
                    .retrieve()
                    .bodyToMono(NewsResponse.class);
            log.debug("response title " + response.block().getTitle());
        } catch (Exception exception) {
            throw new ResourceAccessException("NewsPortal is not running");
        }
    }
}

