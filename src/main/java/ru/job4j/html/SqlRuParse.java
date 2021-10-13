package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.job4j.utils.DateTimeParser;
import ru.job4j.utils.Post;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlRuParse {
    public static void main(String[] args) throws IOException {
        for (int index = 1; index < 2; index++) {
            int skipTopics = 4;
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + index).get();
            Element forumTable = doc.select(".forumTable").first();
            Element tableBody = forumTable.child(0);
            for (Element tableRow : tableBody.children()) {
                if (skipTopics != 0) {
                    skipTopics--;
                    continue;
                }
                Element link = tableRow.child(1).child(0);
                detail(link.attr("href"));
                //Element dateCell = tableRow.child(5);
                //System.out.println(parser.parse(dateCell.text()));
            }
        }
    }

    private static void detail(String link) throws IOException {
        DateTimeParser parser = new SqlRuDateTimeParser();
        Document doc = Jsoup.connect(link).get();
        Element firstPost = doc.select(".msgTable").first();
        Element titleTd = firstPost.child(0).child(0).child(0);
        Element descTd = firstPost.child(0).child(1).child(1);
        Element dateTd = firstPost.child(0).child(2).child(0);
        String title = titleTd.text();
        title = title.substring(0, title.length() - 6);
        String description = descTd.text();
        LocalDateTime created = null;
        Pattern pattern = Pattern.compile("^([^\\[]+) \\[.+");
        Matcher matcher = pattern.matcher(dateTd.text());
        if (matcher.matches()) {
            created = parser.parse(matcher.group(1));
        }
        System.out.println(title);
        System.out.println(description);
        System.out.println(created);
    }
}
