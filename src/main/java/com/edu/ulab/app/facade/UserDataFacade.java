package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.InvalidInputException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;

import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImplTemplate userService;
    private final BookServiceImplTemplate bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImplTemplate userService,
                          BookServiceImplTemplate bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<BookDto> createdBooks = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(mappedBookDto -> mappedBookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("Mapped book request: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .toList();

        return UserBookResponse.builder()
                .userResponse(userMapper.userDtoToUserResponse(createdUser))
                .bookResponses(createdBooks
                        .stream()
                        .map(bookMapper::bookDtoToBookResponse)
                        .toList())
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest, Long userId) {
        log.info("Got user book update request: {}", userBookRequest);
        log.info("Checking if ready to update");
        UserDto user = userService.getUserById(userId);
        log.info("Found user: {}", user);
        List<BookRequest> bookRequests = userBookRequest.getBookRequests();
        List<Long> booksToBeUpdatedIdList = bookService.getBooksByUserId(userId)
                .stream()
                .map(BookDto::getId)
                .toList();
        if (bookRequests.size() != booksToBeUpdatedIdList.size()) {
            throw new InvalidInputException("Unable to update: number of books doesn't match");
        }
        log.info("Ready to update");

        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        userDto.setId(userId);
        log.info("Mapped user update request: {}", userDto);

        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Updated user: {}", userDto);

        List<BookDto> updatedBooks = IntStream.range(0, booksToBeUpdatedIdList.size()).boxed()
                .map(i -> {
                    BookDto bookDto = bookMapper.bookRequestToBookDto(bookRequests.get(i));
                    bookDto.setId(booksToBeUpdatedIdList.get(i));
                    return bookDto;
                })
                .peek(mappedBookDto -> log.info("Mapped book request: {}", mappedBookDto))
                .map(bookService::updateBook)
                .peek(updatedBookDto -> log.info("Updated book: {}", updatedBookDto))
                .toList();

        return UserBookResponse.builder()
                .userResponse(userMapper.userDtoToUserResponse(updatedUser))
                .bookResponses(updatedBooks
                        .stream()
                        .map(bookMapper::bookDtoToBookResponse)
                        .toList())
                .build();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Got user book get request: userId {}", userId);
        UserDto user = userService.getUserById(userId);
        log.info("Got user from database: {}", user);
        List<BookDto> books = bookService.getBooksByUserId(userId)
                .stream()
                .filter(Objects::nonNull)
                .peek(bookDto -> log.info("Got book from database: {}", bookDto))
                .toList();

        return UserBookResponse.builder()
                .userResponse(userMapper.userDtoToUserResponse(user))
                .bookResponses(books
                        .stream()
                        .map(bookMapper::bookDtoToBookResponse)
                        .toList())
                .build();
    }

    public void deleteUserWithBooks(Long userId) {
        log.info("Got user book delete request: userId {}", userId);
        bookService.getBooksByUserId(userId)
                .stream()
                .filter(Objects::nonNull)
                .map(BookDto::getId)
                .forEach(bookService::deleteBookById);
        userService.deleteUserById(userId);
    }
}
