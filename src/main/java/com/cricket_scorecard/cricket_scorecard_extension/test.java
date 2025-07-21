package com.cricket_scorecard.cricket_scorecard_extension;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://en.wikipedia.org/").get();
        System.out.println(doc.title());
        Elements newsHeadlines = doc.select("#mp-itn b a");
        for(Element headline: newsHeadlines) {
            System.out.printf("%s\n\t%s\n",
                    headline.attr("title"), headline.absUrl("href"));
        }
    }
}

