package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto userDto);

    BookDto updateBook(Long id, BookDto userDto);

    BookDto getBookById(Long id);

    void deleteBookById(Long id);

    BookDto setBookOwner(Long bookId, Long userId);

    void releaseBook(Long id);
}
