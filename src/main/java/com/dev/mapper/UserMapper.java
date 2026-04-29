package com.dev.mapper;

import com.dev.dto.UserSignUpDto;
import com.dev.model.User;
import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface UserMapper{

    User toUser(UserSignUpDto user);
    UserSignUpDto toUserSignUpDto(User user);

}
