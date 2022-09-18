package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.BookRepository;
import com.edu.ulab.app.storage.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, UserRepository userRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        bookRepository.save(book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public BookDto updateBook(Long id, BookDto updatedBookDto) {
        Book bookToBeUpdated = bookRepository.findBookById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        Book updatedBook = bookMapper.bookDtoToBook(updatedBookDto);
        updatedBook.setId(id);
        updatedBook.setOwner(bookToBeUpdated.getOwner());
        bookRepository.update(updatedBook);
        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findBookById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        Book book = bookRepository.findBookById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        releaseBook(id);
        bookRepository.remove(book);
    }

    @Override
    public BookDto setBookOwner(Long bookId, Long userId) {
        Book book = bookRepository.findBookById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        book.setOwner(user);
        if (user.getBooks() == null) {
            user.setBooks(new ArrayList<>());
        }
        user.getBooks().add(book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void releaseBook(Long id) {
        Book book = bookRepository.findBookById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        User user = book.getOwner();
        if (user != null && user.getBooks() != null) {
            user.getBooks().remove(book);
            book.setOwner(null);
        }
    }
}
