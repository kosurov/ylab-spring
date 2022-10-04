package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
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
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить юзера. Select = 1, Insert = 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPersonThenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(41);
        person.setTitle("reader1");
        person.setFullName("Test Test");
        person.setCount(1);

        //When
        Person result = userRepository.save(person);
        userRepository.flush();

        //Then
        assertThat(result.getAge()).isEqualTo(41);
        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить юзера. Select = 1, Insert = 1, Update = 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePersonThenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(41);
        person.setTitle("reader2");
        person.setFullName("Test Test");
        person.setCount(1);

        Person savedPerson = userRepository.save(person);

        Person updatedPerson = new Person();
        updatedPerson.setId(savedPerson.getId());
        updatedPerson.setAge(45);
        updatedPerson.setTitle("reader updated");
        updatedPerson.setFullName("Test Test updated");
        updatedPerson.setCount(1);

        //When
        Person result = userRepository.save(updatedPerson);
        userRepository.flush();

        //Then
        assertThat(result.getFullName()).isEqualTo("Test Test updated");
        assertThat(result.getTitle()).isEqualTo("reader updated");
        assertThat(result.getAge()).isEqualTo(45);
        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(1);
        assertDeleteCount(0);
    }

    @DisplayName("Получить юзера. Select = 1, Insert = 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPersonThenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(41);
        person.setTitle("reader3");
        person.setFullName("Test Test");
        person.setCount(1);

        Person savedPerson = userRepository.save(person);
        Integer personId = savedPerson.getId();

        //When
        Person result = userRepository.findById(personId).get();
        userRepository.flush();

        //Then
        assertThat(result.getFullName()).isEqualTo("Test Test");
        assertThat(result.getTitle()).isEqualTo("reader3");
        assertThat(result.getAge()).isEqualTo(41);
        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить юзера. Select = 1, Insert = 1, Delete = 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePersonThenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(41);
        person.setTitle("reader4");
        person.setFullName("Test Test");
        person.setCount(1);

        Person savedPerson = userRepository.save(person);

        //When
        userRepository.delete(savedPerson);
        userRepository.flush();

        //Then
        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(1);
    }
    // * failed
}
