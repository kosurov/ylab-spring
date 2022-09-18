package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository //todo какая аннотация нужна?
public class Storage implements UserRepository, BookRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Book> books = new HashMap<>();
    private Long userId = 0L;
    private Long bookId = 0L;

    @Override
    public void save(Book book) {
        book.setId(++bookId);
        books.put(book.getId(), book);
    }

    @Override
    public void update(Book book) {
        books.put(book.getId(), book);
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    @Override
    public void remove(Book book) {
        books.remove(book.getId());
    }

    @Override
    public void save(User user) {
        user.setId(++userId);
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void remove(User user) {
        users.remove(user.getId());
    }
}
