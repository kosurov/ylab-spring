package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.Book;

import java.util.Optional;

public interface BookRepository {
    void save(Book book);

    void update(Book book);

    Optional<Book> findBookById(Long id);

    void remove(Book book);
}
