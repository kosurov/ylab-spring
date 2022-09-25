package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.web.request.UserRequest;
import com.edu.ulab.app.web.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userRequestToUserDto(UserRequest userRequest);

    UserDto userToUserDto(User user);

    UserDto userResponseToUserDto(UserResponse userResponse);

    UserRequest userDtoToUserRequest(UserDto userDto);

    User userDtoToUser(UserDto userDto);

    UserResponse userDtoToUserResponse(UserDto userDto);
}
