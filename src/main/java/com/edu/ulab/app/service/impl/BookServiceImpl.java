package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           UserRepository userRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        book.setPerson(userRepository.findById(bookDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found")));
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book existingBook = bookRepository.findById(bookDto.getId())
                .orElseThrow(() -> new NotFoundException("Book not found"));
        log.info("Existing book: {}", existingBook);
        Book bookToUpdate = bookMapper.bookDtoToBook(bookDto);
        bookToUpdate.setPerson(existingBook.getPerson());
        log.info("Mapped book to update: {}", bookToUpdate);
        Book updatedBook = bookRepository.save(bookToUpdate);
        log.info("Saved book: {}", updatedBook);
        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        log.info("Found book to delete: {}", book);
        bookRepository.delete(book);
        log.info("Book deleted: bookId {}", id);
    }

    @Override
    public List<BookDto> getBooksByUserId(Integer id) {
        List<Book> books = bookRepository.findAllByPersonId(id);
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
