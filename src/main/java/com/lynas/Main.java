package com.lynas;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] urlArray = htmlToUrlList(urlToHtmlString("https://en.wikipedia.org/wiki/Europe"))
                .parallelStream()
                .filter(item -> !item.isEmpty())
                .map(Main::urlToHtmlString)
                .filter(item -> !item.isEmpty())
                .map(Main::htmlToUrlList)
                .parallel()
                .flatMap(Collection::stream)
                .toArray(String[]::new);
        countUrlFrequency(urlArray);

    }

    private static void countUrlFrequency(String[] urlArray) throws Exception {
        Map<String, Integer> urlFrequencyCount = new ConcurrentHashMap<>();
        Arrays.stream(urlArray).forEach(item -> {
            Integer i = urlFrequencyCount.get(item);
            if (i == null) {
                i = 0;
            }
            urlFrequencyCount.put(item, i + 1);
        });
        System.out.println("final : " + urlArray.length);
        System.out.println("final : " + urlFrequencyCount.size());
        writeToFile(urlFrequencyCount);
    }

    private static void writeToFile(Map<String, Integer> urlFrequencyCount) throws IOException {
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

    private static String urlToHtmlString(String url) {
        try {
            return new Scanner(new URL(url.trim())
                    .openStream(), "UTF-8")
                    .useDelimiter("\\A")
                    .next();
        } catch (Exception e) {
            return "";
        }
    }


    private static List<String> htmlToUrlList(String text) {
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
