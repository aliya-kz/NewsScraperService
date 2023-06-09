package org.zhumagulova.newsscraperservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "news")
public class News {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "news_seq_gen")
    @SequenceGenerator(name = "news_seq_gen", sequenceName = "news_sequence", allocationSize = 1)
    private long id;
    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @Column(name = "content", length = 2048)
    private String content;

    @JsonIgnore
    private String articleLink;

    public News(String title, LocalDate date, String content, String articleLink) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.articleLink = articleLink;
    }
}
