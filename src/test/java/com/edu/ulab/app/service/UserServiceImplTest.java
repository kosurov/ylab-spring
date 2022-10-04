package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void createUserTest() {
        //given
        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson  = new Person();
        savedPerson.setId(1);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when
        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //then
        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1, userDtoResult.getId());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals(11, userDtoResult.getAge());
        assertEquals("test title", userDtoResult.getTitle());
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updateUserTest() {
        //given
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setAge(12);
        userDto.setFullName("test name updated");
        userDto.setTitle("test title updated");

        Person existingUser  = new Person();
        existingUser.setFullName("test name");
        existingUser.setAge(11);
        existingUser.setTitle("test title");

        Person userToUpdate  = new Person();
        userToUpdate.setId(1);
        userToUpdate.setFullName("test name updated");
        userToUpdate.setAge(12);
        userToUpdate.setTitle("test title updated");

        Person updatedUser  = new Person();
        updatedUser.setId(1);
        updatedUser.setFullName("test name updated");
        updatedUser.setAge(12);
        updatedUser.setTitle("test title updated");

        UserDto result = new UserDto();
        result.setId(1);
        result.setAge(12);
        result.setFullName("test name updated");
        result.setTitle("test title updated");

        //when
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userMapper.userDtoToPerson(userDto)).thenReturn(userToUpdate);
        when(userRepository.save(userToUpdate)).thenReturn(updatedUser);
        when(userMapper.personToUserDto(updatedUser)).thenReturn(result);

        //then
        UserDto userDtoResult = userService.updateUser(userDto);
        assertEquals(1, userDtoResult.getId());
        assertEquals("test name updated", userDtoResult.getFullName());
        assertEquals(12, userDtoResult.getAge());
        assertEquals("test title updated", userDtoResult.getTitle());
    }

    @Test
    @DisplayName("Получение пользователя. Должно пройти успешно.")
    void getUserByIdTest() {
        //given
        Person user  = new Person();
        user.setId(1);
        user.setFullName("test name");
        user.setAge(11);
        user.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.personToUserDto(user)).thenReturn(result);

        //then
        UserDto userDtoResult = userService.getUserById(1);
        assertEquals(1, userDtoResult.getId());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals(11, userDtoResult.getAge());
        assertEquals("test title", userDtoResult.getTitle());
    }

    @Test
    @DisplayName("Удаление пользователя. Должно пройти успешно.")
    void deleteUserByIdTest() {
        //given
        Person user  = new Person();
        user.setId(1);
        user.setFullName("test name");
        user.setAge(11);
        user.setTitle("test title");

        //when
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        //then
        userService.deleteUserById(1);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Неудачное получение пользователя")
    void getUserByIdFailedTest() {
        //given
        NotFoundException exception = new NotFoundException("User not found");

        //when
        doThrow(exception).when(userRepository).findById(1);

        //then
        assertThatThrownBy(() -> userService.getUserById(1))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
    }
}
