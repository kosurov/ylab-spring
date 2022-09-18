package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.InvalidInputException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
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
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserService userService,
                          BookService bookService,
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
                .peek(mappedBookDto -> log.info("Mapped book request: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(bookDto -> bookService.setBookOwner(bookDto.getId(), createdUser.getId()))
                .peek(bookDto -> log.info("Book set to user: userId {}", bookDto.getUserId()))
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

        List<BookRequest> bookRequests = userBookRequest.getBookRequests();
        List<Long> booksToBeUpdatedIdList = userService.getBooksByUserId(userId)
                .stream()
                .map(BookDto::getId)
                .toList();
        if (bookRequests.size() != booksToBeUpdatedIdList.size()) {
            throw new InvalidInputException("Unable to update: number of books doesn't match");
        }

        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user update request: {}", userDto);

        UserDto updatedUser = userService.updateUser(userId, userDto);
        log.info("Updated user: {}", userDto);

        List<BookDto> updatedBooks = IntStream.range(0, booksToBeUpdatedIdList.size()).boxed()
                .map(i -> {
                    BookDto bookDto = bookMapper.bookRequestToBookDto(bookRequests.get(i));
                    bookDto.setId(booksToBeUpdatedIdList.get(i));
                    return bookDto;
                })
                .peek(mappedBookDto -> log.info("Mapped book request: {}", mappedBookDto))
                .map(mappedBookDto -> bookService.updateBook(mappedBookDto.getId(), mappedBookDto))
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
        log.info("Got user from storage: {}", user);
        List<BookDto> books = userService.getBooksByUserId(userId)
                .stream()
                .filter(Objects::nonNull)
                .peek(bookDto -> log.info("Got book from storage: {}", bookDto))
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
        userService.getBooksByUserId(userId)
                .stream()
                .filter(Objects::nonNull)
                .map(BookDto::getId)
                .forEach(bookId -> {
                    bookService.deleteBookById(bookId);
                    log.info("Book deleted: bookId {}", bookId);
                });
        userService.deleteUserById(userId);
        log.info("User deleted: userId {}", userId);
    }
}
