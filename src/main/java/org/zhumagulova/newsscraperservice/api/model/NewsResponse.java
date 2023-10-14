package org.zhumagulova.newsscraperservice.api.model;

import lombok.Data;

@Data
public class NewsResponse {
    private String title;
    private String brief;
    private String content;
    private String date;
}
