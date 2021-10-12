package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;

public class SqlRuParse {
    public static void main(String[] args) throws IOException {
        DateTimeParser parser = new SqlRuDateTimeParser();
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Element forumTable = doc.select(".forumTable").first();
        Element tableBody = forumTable.child(0);
        for (Element tableRow : tableBody.children()) {
            Element dateCell = tableRow.child(5);
            System.out.println(parser.parse(dateCell.text()));
        }
    }
}
