package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           BookRepository bookRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("Existing user: {}", existingUser);
        Person userToUpdate = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user to update: {}", userToUpdate);
        Person updatedUser = userRepository.save(userToUpdate);
        log.info("Saved user: {}", updatedUser);
        return userMapper.personToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Integer id) {
        Person user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.personToUserDto(user);
    }

    @Override
    public void deleteUserById(Integer id) {
        Person user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("Found user to delete: {}", user);
        Set<Book> books = user.getBookSet();
        if (books != null && !books.isEmpty()) {
            log.info("Found books from user: {}", books);
            books.stream()
                    .filter(Objects::nonNull)
                    .peek(book -> book.setPerson(null))
                    .peek(bookRepository::save)
                    .forEach(book -> log.info("Connection with user removed: bookId {}", book.getId()));
        }
        userRepository.delete(user);
        log.info("User deleted: userId {}", id);
    }
}
