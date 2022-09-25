package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dao.BookDao;
import com.edu.ulab.app.dao.PersonDao;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final PersonDao personDao;
    private final BookDao bookDao;
    private final UserMapper userMapper;

    public UserServiceImplTemplate(PersonDao personDao, BookDao bookDao, UserMapper userMapper) {
        this.personDao = personDao;
        this.bookDao = bookDao;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = personDao.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person existingUser = personDao.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("Existing user: {}", existingUser);
        Person userToUpdate = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user to update: {}", userToUpdate);
        Person updatedUser = personDao.update(userToUpdate);
        log.info("Saved user: {}", updatedUser);
        return userMapper.personToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        Person user = personDao.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.personToUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        Person user = personDao.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("Found user to delete: {}", user);
        List<Book> books = bookDao.findAllByUserId(id);
        if (books != null && !books.isEmpty()) {
            log.info("Found books from user: {}", books);
            books.stream()
                    .filter(Objects::nonNull)
                    .map(Book::getId)
                    .peek(bookDao::releaseBook)
                    .forEach(bookId -> log.info("Connection with user removed: bookId {}", bookId));
        }
        personDao.delete(user);
        log.info("User deleted: userId {}", id);
    }
}
