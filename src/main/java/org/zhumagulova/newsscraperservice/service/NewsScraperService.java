package org.zhumagulova.newsscraperservice.service;

import org.zhumagulova.newsscraperservice.exception.ScraperException;

public interface NewsScraperService {
    void scrape () throws ScraperException;
}
