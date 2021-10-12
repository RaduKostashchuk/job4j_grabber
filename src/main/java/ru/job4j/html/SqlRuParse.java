package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;

public class SqlRuParse {
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Element forumTable = doc.select(".forumTable").first();
        Element tableBody = forumTable.child(0);
        for (Element tableRow : tableBody.children()) {
            Element dateCell = tableRow.child(5);
            System.out.println(dateCell.text());
        }
    }
}
