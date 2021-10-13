package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.Post;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;

public class SqlRuParse {
    public static void main(String[] args) throws IOException {
        DateTimeParser parser = new SqlRuDateTimeParser();
        for (int index = 1; index < 2; index++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + index).get();
            Element forumTable = doc.select(".forumTable").first();
            Element tableBody = forumTable.child(0);
            for (Element tableRow : tableBody.children()) {
                if (tableRow.child(0).tagName().equals("td")) {
                    Element link = tableRow.child(1).child(0);
                    detail(link.attr("href"));
                    Element dateCell = tableRow.child(5);
                    System.out.println(parser.parse(dateCell.text()));
                }
            }
        }
    }

    private static void detail(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        Element firstPost = doc.select(".msgTable").first();
        Element titleTd = firstPost.child(0).child(0).child(0);
        Element descTd = firstPost.child(0).child(1).child(1);
        String title = titleTd.text();
        String description = descTd.text();
        System.out.println(title);
        System.out.println(description);
    }
}
