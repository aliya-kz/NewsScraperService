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
import org.zhumagulova.newsscraperservice.entity.News;
import org.zhumagulova.newsscraperservice.exception.ScraperException;
import org.zhumagulova.newsscraperservice.kafka.KafkaSender;
import org.zhumagulova.newsscraperservice.repository.NewsRepository;
import org.zhumagulova.newsscraperservice.service.mapper.NewsMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TengrinewsScraperService implements NewsScraperService {

    private final KafkaSender kafkaSender;
    private static final String NEWS_PAGE_URL = "https://tengrinews.kz";
    private static final String KAZAKHSTAN_NEWS_PAGE_URL = NEWS_PAGE_URL + "/kazakhstan_news";
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @Override
    @Transactional
    @Scheduled(cron = "0 * * ? 4-6 * ")
    public void scrape() throws ScraperException {
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
                        News news = newsMapper.map(title, postDate, content, articleLink);
                        newsList.add(news);
                        kafkaSender.sendMessage (news, "tengri-news");
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
}
