package org.zhumagulova.newsscraperservice.service.mapper;

import org.springframework.stereotype.Component;
import org.zhumagulova.newsscraperservice.entity.News;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class NewsMapper {
    final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")
            .withLocale(new Locale("ru"));

    public News map(String title, String postDate, String content, String articleLink) {
        if (postDate.contains("Вчера")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(new Locale("ru"));
            String yesterday = LocalDate.now().minus(Period.ofDays(1)).format(formatter);
            postDate = yesterday.concat("," + postDate.substring(6));
        }
        LocalDate localDate = LocalDate.parse(postDate, dtf);
        return new News(title, localDate, content, articleLink);
    }
}
