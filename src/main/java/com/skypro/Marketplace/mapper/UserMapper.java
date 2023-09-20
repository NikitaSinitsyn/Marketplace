package com.skypro.Marketplace.mapper;

import com.skypro.Marketplace.dto.user.UserDTO;
import com.skypro.Marketplace.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "role", target = "role")
    UserDTO userToUserDTO(User user);

    @Mapping(source = "role", target = "role")
    User userDTOToUser(UserDTO userDTO);
}