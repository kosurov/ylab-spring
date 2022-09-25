package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    BookDto updateBook(BookDto bookDto);

    BookDto getBookById(Long id);

    void deleteBookById(Long id);

    BookDto setBookOwner(Long bookId, Long userId);

    void releaseBook(Long id);
}
