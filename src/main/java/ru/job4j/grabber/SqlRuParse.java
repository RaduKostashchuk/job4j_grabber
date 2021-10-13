package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlRuParse implements Parse {
    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) {
        SqlRuParse sqlParse = new SqlRuParse(new SqlRuDateTimeParser());
        for (int index = 1; index < 2; index++) {
            List<Post> list = sqlParse.list("https://www.sql.ru/forum/job-offers/" + index);
            for (Post el : list) {
                System.out.println(el);
            }
        }
    }

    @Override
    public List<Post> list(String link) {
        List<Post> result = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element forumTable = doc.select(".forumTable").first();
        int skipTopics = 1;
        Element tableBody = forumTable.child(0);
        for (Element tableRow : tableBody.children()) {
            if (skipTopics != 0) {
                skipTopics--;
                continue;
            }
            Element href = tableRow.child(1).child(0);
            Post post = detail(href.attr("href"));
            String updated = tableRow.child(5).text();
            post.setUpdated(dateTimeParser.parse(updated));
            result.add(post);
        }
        return result;
    }

    @Override
    public Post detail(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            created = dateTimeParser.parse(matcher.group(1));
        }
        return new Post(title, link, description, created);
    }
}
