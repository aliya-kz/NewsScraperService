create sequence news_sequence start with 1 increment by 1;

create database tengri_news;



create table news
(
    id           bigint not null,
    article_link varchar(255),
    content      varchar(8000),
    date         date,
    title        varchar(255),
    primary key (id)
);