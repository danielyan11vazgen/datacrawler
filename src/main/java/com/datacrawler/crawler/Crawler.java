package com.datacrawler.crawler;

import java.io.IOException;

/**
 * User: Vazgen Danielyan
 * Date: 11/29/17
 * Time: 10:41 AM
 */
public interface Crawler {

    void crawl(final String outputFilePath) throws IOException;

}
