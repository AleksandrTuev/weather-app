package com.dev.mapper;

import com.dev.dto.UserSignInDto;
import com.dev.dto.UserSignUpDto;
import com.dev.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (componentModel = "spring")
public interface UserMapper{

    @Mapping(target = "id", ignore = true)
    User toUser(UserSignUpDto user);

    @Mapping(target = "repeatPassword", ignore = true)
    UserSignUpDto toUserSignUpDto(User user);
    UserSignInDto toUserSignInDto(User user);

}
