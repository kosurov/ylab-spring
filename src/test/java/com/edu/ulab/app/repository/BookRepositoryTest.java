package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу. Select = 2, Insert = 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertBookThenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader1");
        person.setFullName("Test Test");
        person.setCount(1);

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);
        userRepository.flush();
        bookRepository.flush();

        //Then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertThat(result.getAuthor()).isEqualTo("Test Author");
        assertSelectCount(2);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить книгу. Select = 2, Insert = 2, Update = 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBookThenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader2");
        person.setFullName("Test Test");
        person.setCount(1);

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        Book savedBook = bookRepository.save(book);

        Book updatedBook = new Book();
        updatedBook.setId(savedBook.getId());
        updatedBook.setAuthor("Test Author updated");
        updatedBook.setTitle("test updated");
        updatedBook.setPageCount(100);
        updatedBook.setPerson(savedBook.getPerson());

        //When
        Book result = bookRepository.save(updatedBook);
        userRepository.flush();
        bookRepository.flush();

        //Then
        assertThat(result.getPageCount()).isEqualTo(100);
        assertThat(result.getTitle()).isEqualTo("test updated");
        assertThat(result.getAuthor()).isEqualTo("Test Author updated");
        assertSelectCount(2);
        assertInsertCount(2);
        assertUpdateCount(1);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книгу. Select = 2, Insert = 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBookThenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader3");
        person.setFullName("Test Test");
        person.setCount(1);

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        Book savedBook = bookRepository.save(book);
        Integer bookId = savedBook.getId();

        //When
        Book result = bookRepository.findById(bookId).get();
        userRepository.flush();
        bookRepository.flush();

        //Then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertThat(result.getAuthor()).isEqualTo("Test Author");
        assertSelectCount(2);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книгу. Select = 2, Insert = 2, Delete = 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBookThenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader4");
        person.setFullName("Test Test");
        person.setCount(1);

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        Book savedBook = bookRepository.save(book);

        //When
        bookRepository.delete(savedBook);
        userRepository.flush();
        bookRepository.flush();

        //Then
        assertSelectCount(2);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(1);
    }

    // * failed
    // get all


    // example failed test
}
