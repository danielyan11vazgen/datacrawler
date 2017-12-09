package com.datacrawler;

import com.datacrawler.crawler.Crawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataCrawlerApplication implements CommandLineRunner {

    @Autowired
    private Crawler crawler;

    public static void main(String[] args) {
        SpringApplication.run(DataCrawlerApplication.class, args);
    }

    @Override
    public void run(final String... strings) throws Exception {

        final String outputFilePath;
        if (strings.length > 0) {
            outputFilePath = strings[0];
        } else {
            outputFilePath = null;
        }

        crawler.crawl(outputFilePath);
    }
}
