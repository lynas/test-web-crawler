package com.lynas;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    public String[] getUrlFromHtml(String url) {
        return htmlToUrlList(urlToHtmlString(url))
                .parallelStream()
                .filter(item -> !item.isEmpty())
                .map(this::urlToHtmlString)
                .filter(item -> !item.isEmpty())
                .map(this::htmlToUrlList)
                .parallel()
                .flatMap(Collection::stream)
                .toArray(String[]::new);
    }

    public Map<String, Integer> countUrlFrequency(String[] urlArray) {
        Map<String, Integer> urlFrequencyCount = new ConcurrentHashMap<>();
        Arrays.stream(urlArray).forEach(item -> {
            Integer i = urlFrequencyCount.get(item);
            if (i == null) {
                i = 0;
            }
            urlFrequencyCount.put(item, i + 1);
        });
        System.out.println("Total url : " + urlArray.length);
        System.out.println("Unique url : " + urlFrequencyCount.size());
        return urlFrequencyCount;
    }

    public void writeToFile(Map<String, Integer> urlFrequencyCount) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(new File("file.txt")), StandardCharsets.UTF_8));
        urlFrequencyCount.forEach((key, value) -> {
            try {
                writer.write(value + " URL " + key + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.flush();
        writer.close();
    }

    private String urlToHtmlString(String url) {
        try {
            return new Scanner(new URL(url.trim())
                    .openStream(), "UTF-8")
                    .useDelimiter("\\A")
                    .next();
        } catch (Exception e) {
            return "";
        }
    }


    private List<String> htmlToUrlList(String text) {
        List<String> containedUrls = new ArrayList<>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find()) {
            String substring = text.substring(urlMatcher.start(0), urlMatcher.end(0));
            System.out.println(substring);
            containedUrls.add(substring);
        }
        return containedUrls;
    }
}
