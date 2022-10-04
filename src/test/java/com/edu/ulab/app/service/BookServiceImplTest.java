package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно")
    void saveBookTest() {
        //given
        Person person  = new Person();
        person.setId(1);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1);
        result.setUserId(1);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setAuthor("test author");
        book.setTitle("test title");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1);
        savedBook.setPageCount(1000);
        savedBook.setAuthor("test author");
        savedBook.setTitle("test title");
        savedBook.setPerson(person);

        //when
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(userRepository.findById(1)).thenReturn(Optional.of(person));
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1, bookDtoResult.getId());
        assertEquals(1, bookDtoResult.getUserId());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals(1000, bookDtoResult.getPageCount());
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBookTest() {
        //given
        Person person  = new Person();
        person.setId(1);

        BookDto bookDto = new BookDto();
        bookDto.setId(1);
        bookDto.setUserId(1);
        bookDto.setAuthor("test author updated");
        bookDto.setTitle("test title updated");
        bookDto.setPageCount(100);

        BookDto result = new BookDto();
        result.setId(1);
        result.setUserId(1);
        result.setAuthor("test author updated");
        result.setTitle("test title updated");
        result.setPageCount(100);

        Book existingBook = new Book();
        existingBook.setId(1);
        existingBook.setPageCount(1000);
        existingBook.setAuthor("test author");
        existingBook.setTitle("test title");
        existingBook.setPerson(person);

        Book bookToUpdate = new Book();
        bookToUpdate.setId(1);
        bookToUpdate.setPageCount(100);
        bookToUpdate.setAuthor("test author updated");
        bookToUpdate.setTitle("test author updated");

        Book updatedBook = new Book();
        updatedBook.setId(1);
        updatedBook.setPageCount(1000);
        updatedBook.setAuthor("test author updated");
        updatedBook.setTitle("test author updated");
        updatedBook.setPerson(person);

        //when
        when(bookRepository.findById(1)).thenReturn(Optional.of(existingBook));
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(bookToUpdate);
        when(bookRepository.save(bookToUpdate)).thenReturn(updatedBook);
        when(bookMapper.bookToBookDto(updatedBook)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.updateBook(bookDto);
        assertEquals(1, bookDtoResult.getId());
        assertEquals(1, bookDtoResult.getUserId());
        assertEquals("test author updated", bookDtoResult.getAuthor());
        assertEquals("test title updated", bookDtoResult.getTitle());
        assertEquals(100, bookDtoResult.getPageCount());
    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно.")
    void getBookByIdTest() {
        //given
        Person person  = new Person();
        person.setId(1);

        BookDto result = new BookDto();
        result.setId(1);
        result.setUserId(1);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setId(1);
        book.setPageCount(1000);
        book.setAuthor("test author");
        book.setTitle("test title");
        book.setPerson(person);

        //when
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.getBookById(1);
        assertEquals(1, bookDtoResult.getId());
        assertEquals(1, bookDtoResult.getUserId());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals(1000, bookDtoResult.getPageCount());
    }

    @Test
    @DisplayName("Удаление книги. Должно пройти успешно.")
    void deleteBookByIdTest() {
        //given
        Person person  = new Person();
        person.setId(1);

        Book book = new Book();
        book.setId(1);
        book.setPageCount(1000);
        book.setAuthor("test author");
        book.setTitle("test title");
        book.setPerson(person);

        //when
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        //then
        bookService.deleteBookById(1);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Получить книги по Id пользователя. Должно пройти успешно.")
    void getBooksByUserIdTest() {
        //given
        Person person  = new Person();
        person.setId(1);

        BookDto result1 = new BookDto();
        result1.setId(1);
        result1.setUserId(1);
        result1.setAuthor("test author1");
        result1.setTitle("test title1");
        result1.setPageCount(100);

        BookDto result2 = new BookDto();
        result2.setId(2);
        result2.setUserId(1);
        result2.setAuthor("test author2");
        result2.setTitle("test title2");
        result2.setPageCount(1000);

        List<BookDto> bookDtoExpectedList = new ArrayList<>();
        bookDtoExpectedList.add(result1);
        bookDtoExpectedList.add(result2);

        Book book1 = new Book();
        book1.setId(1);
        book1.setPageCount(100);
        book1.setAuthor("test author1");
        book1.setTitle("test title1");
        book1.setPerson(person);

        Book book2 = new Book();
        book2.setId(2);
        book2.setPageCount(1000);
        book2.setAuthor("test author2");
        book2.setTitle("test title2");
        book2.setPerson(person);

        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);

        //when
        when(bookRepository.findAllByPersonId(1)).thenReturn(books);
        when(bookMapper.bookToBookDto(book1)).thenReturn(result1);
        when(bookMapper.bookToBookDto(book2)).thenReturn(result2);

        //then
        List<BookDto> bookDtoResultList = bookService.getBooksByUserId(1);
        assertEquals(bookDtoExpectedList, bookDtoResultList);
    }

    // * failed
}
