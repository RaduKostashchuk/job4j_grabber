package ru.job4j.grabber;

import java.util.List;

public interface Store {
    int save(Post post);

    List<Post> getAll();

    Post findById(int id);
}
