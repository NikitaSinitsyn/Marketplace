package com.skypro.Marketplace.mapper;

import com.skypro.Marketplace.dto.user.UserDTO;
import com.skypro.Marketplace.entity.User;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between {@link User} entities and {@link UserDTO} data transfer objects.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a {@link User} entity to a {@link UserDTO} data transfer object.
     *
     * @param user The {@link User} entity to be converted.
     * @return The corresponding {@link UserDTO}.
     */
    UserDTO userToUserDTO(User user);

    /**
     * Converts a {@link UserDTO} data transfer object to a {@link User} entity.
     *
     * @param userDTO The {@link UserDTO} to be converted.
     * @return The corresponding {@link User} entity.
     */
    User userDTOToUser(UserDTO userDTO);


}

