package com.datacrawler.crawler.impl;

import com.datacrawler.crawler.Crawler;
import com.datacrawler.crawler.model.Point;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * User: Vazgen Danielyan
 * Date: 11/29/17
 * Time: 10:41 AM
 */
@Service
public class CrawlerImpl implements Crawler {

    private static final String BASE_URL = "https://pkmngotrading.com/";

    private static final String POINTS_PAGE_URL = BASE_URL + "wiki/Nests";

    @Override
    public void crawl(final String outputFilePath) throws IOException {

        final CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        final ResponseEntity<String> response = new RestTemplate(requestFactory).exchange(POINTS_PAGE_URL, HttpMethod.GET, null, String.class);

        final List<?> list = Jsoup
                .parse(response.getBody())
                .select("tbody > tr[data-row-number]")
                .stream()
                .map(element ->
                        CompletableFuture.supplyAsync(() ->
                                getPoints(element.select("td.smwtype_num").first().text(), BASE_URL + element.select("td.Acquired-by.smwtype_wpg > a").first().attr("href"))
                        )
                )
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).flatMap(List::stream).collect(Collectors.toList());

        FileUtils.writeStringToFile(new File(StringUtils.isNotEmpty(outputFilePath) ? outputFilePath : "out.json"), new Gson().toJson(list), Charset.defaultCharset(), false);
    }

    private List<Point> getPoints(final String number, final String anchor) {
        try {
            final CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

            final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            final ResponseEntity<String> response2 = new RestTemplate(requestFactory).exchange(anchor, HttpMethod.GET, null, String.class);

            return Jsoup
                    .parse(response2.getBody())
                    .select("tbody > tr[data-row-number]")
                    .stream()
                    .map(i -> new Point(number, new BigDecimal(i.select("td.Latitude.smwtype_wpg").first().text()), new BigDecimal(i.select("td.Longitude.smwtype_wpg").first().text())))
                    .collect(Collectors.toList());

        } catch (final Exception e) {
            System.out.println("Unexpected exception occurred when trying to fetch data for " + anchor + " url");
            return new LinkedList<>();
        }
    }
}
