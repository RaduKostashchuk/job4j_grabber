package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("grabber.driver"));
            cnn = DriverManager.getConnection(
                cfg.getProperty("grabber.url"),
                cfg.getProperty("grabber.username"),
                cfg.getProperty("grabber.password")
        );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int save(Post post) {
        int result = -1;
        try (PreparedStatement statement = cnn.prepareStatement(
            "insert into posts(name, description, link, created, updated)"
                    + "values (?, ?, ?, ?, ?) on conflict do nothing",
            Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.setTimestamp(5, Timestamp.valueOf(post.getUpdated()));
            statement.execute();
            try (ResultSet genKeys = statement.getGeneratedKeys()) {
                if (genKeys.next()) {
                    result = genKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
        e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Post> getAll() {
        var result = new ArrayList<Post>();
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from posts"
        )) {
            try (ResultSet rSet = statement.executeQuery()) {
                while (rSet.next()) {
                    LocalDateTime created = rSet.getTimestamp("created").toLocalDateTime();
                    LocalDateTime updated = rSet.getTimestamp("updated").toLocalDateTime();
                    result.add(new Post(
                            rSet.getInt("id"),
                            rSet.getString("name"),
                            rSet.getString("description"),
                            rSet.getString("link"),
                            created,
                            updated));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Post findById(int id) {
        Post result = null;
        try (PreparedStatement statement = cnn.prepareStatement(
                "select id, name, description, link, created, updated from posts where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet rSet = statement.executeQuery()) {
                if (rSet.next()) {
                    LocalDateTime created = rSet.getTimestamp("created").toLocalDateTime();
                    LocalDateTime updated = rSet.getTimestamp("updated").toLocalDateTime();
                    result = new Post(
                            rSet.getInt("id"),
                            rSet.getString("name"),
                            rSet.getString("description"),
                            rSet.getString("link"),
                            created,
                            updated);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    private static Properties load() {
        Properties properties = null;
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("grabber.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) {
        PsqlStore store = new PsqlStore(load());
        var parser = new SqlRuDateTimeParser();
        Post post1 = new Post(
                "Web-разработчик fullstack middle",
                "https://www.sql.ru/forum/1338477/web-razrabotchik-fullstack-middle",
                "На полный день требуется web-разработчик fullstack",
                parser.parse("1 сен 21, 17:17"),
                parser.parse("8 окт 21, 23:16"));
        Post post2 = new Post(
                "Microsoft Russia - Power BI Technical Specialist",
                "https://www.sql.ru/forum/1339344/microsoft-russia-power-bi-technical-specialist",
                "Microsoft Russia - Power BI Technical Specialist",
                parser.parse("10 окт 21, 10:40 "),
                parser.parse("10 окт 21, 11:40")
                );
        int post1Id = store.save(post1);
        int post2Id = store.save(post2);
        for (Post post : store.getAll()) {
            System.out.println(post);
        }
        System.out.println(store.findById(post1Id));
        System.out.println(store.findById(post2Id));
    }

}
