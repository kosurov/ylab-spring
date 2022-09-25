package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dao.BookDao;
import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final BookDao bookDao;
    private final BookMapper bookMapper;

    public BookServiceImplTemplate(BookDao bookDao, BookMapper bookMapper) {
        this.bookDao = bookDao;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookDao.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book existingBook = bookDao.findById(bookDto.getId())
                .orElseThrow(() -> new NotFoundException("Book not found"));
        log.info("Existing book: {}", existingBook);
        Book bookToUpdate = bookMapper.bookDtoToBook(bookDto);
        bookToUpdate.setUserId(existingBook.getUserId());
        log.info("Mapped book to update: {}", bookToUpdate);
        Book updatedBook = bookDao.update(bookToUpdate);
        log.info("Saved book: {}", updatedBook);
        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        Book book = bookDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        log.info("Found book to delete: {}", book);
        bookDao.delete(book);
        log.info("Book deleted: bookId {}", id);
    }

    @Override
    public List<BookDto> getBooksByUserId(Long id) {
        List<Book> books = bookDao.findAllByUserId(id);
        if (books != null && !books.isEmpty()) {
            log.info("Found books: {}", books);
            return books
                    .stream()
                    .filter(Objects::nonNull)
                    .map(bookMapper::bookToBookDto)
                    .toList();
        }
        log.info("User doesn't have books");
        return Collections.emptyList();
    }
}
