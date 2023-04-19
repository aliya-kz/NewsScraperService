package org.zhumagulova.newsscraperservice.exception;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ScraperException extends Exception {

    public ScraperException(String message, IOException e) {
        super(e);
    }
}
