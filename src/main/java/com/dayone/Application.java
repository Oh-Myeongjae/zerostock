package com.dayone;

import com.dayone.scraper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Application {

    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
        SpringApplication.run(Application.class, args);

        try {
            Connection connection = Jsoup
                    .connect("https://finance.yahoo.com/quote/COKE/history/?frequency=1mo&period1=99153000&period2=1733243800")
                    .userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
                    .header("scheme", "https")
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6")
                    .header("cache-control", "no-cache")
                    .header("pragma", "no-cache")
                    .header("upgrade-insecure-requests", "1");
            Document document = connection.get();
            Elements eles = document.select("table.table.yf-j5d1ld.noDl");
            Element ele = eles.get(0);

            Element tbody = ele.children().get(1);

            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }
                String[] splits = txt.split(" ");
                String month = splits[0];
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];
                System.out.println(year + "/" + month + "/" + day + " -> " + dividend);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
