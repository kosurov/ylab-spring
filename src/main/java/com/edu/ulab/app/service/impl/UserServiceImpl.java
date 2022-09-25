package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, BookMapper bookMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        userRepository.save(user);
        return userMapper.userToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto updatedUserDto) {
        User userToBeUpdated = userRepository.findUserById(updatedUserDto.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        User updatedUser = userMapper.userDtoToUser(updatedUserDto);
        updatedUser.setBooks(userToBeUpdated.getBooks());
        userRepository.update(updatedUser);
        return userMapper.userToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.userToUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<Book> books = user.getBooks();
        if (books != null) {
            books.stream()
                    .filter(Objects::nonNull)
                    .forEach(book -> book.setOwner(null));
        }
        userRepository.remove(user);
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<Book> books = user.getBooks();
        if (books != null) {
            return books
                    .stream()
                    .filter(Objects::nonNull)
                    .map(bookMapper::bookToBookDto)
                    .toList();
        }
        return Collections.emptyList();
    }
}
