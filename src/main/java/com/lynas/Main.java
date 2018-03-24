package com.lynas;

import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Crawler crawler = new Crawler();
        String[] urlArray = crawler.getUrlFromHtml("https://en.wikipedia.org/wiki/Europe");
        Map<String, Integer> urlFrequencyCount = crawler.countUrlFrequency(urlArray);
        crawler.writeToFile(urlFrequencyCount);
    }
}
