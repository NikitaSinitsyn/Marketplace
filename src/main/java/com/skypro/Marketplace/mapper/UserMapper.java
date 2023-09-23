package com.skypro.Marketplace.mapper;

import com.skypro.Marketplace.dto.ad.AdDTO;
import com.skypro.Marketplace.dto.comment.CommentDTO;
import com.skypro.Marketplace.dto.user.UserDTO;
import com.skypro.Marketplace.entity.Ad;
import com.skypro.Marketplace.entity.Comment;
import com.skypro.Marketplace.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);


    default Integer mapUserToInteger(User user) {
        return user != null ? user.getId() : null;
    }

    default Integer mapAdToInteger(Ad ad) {
        return ad != null ? ad.getId() : null;
    }


    default byte[] mapStringToByteArray(String value) {
        return value != null ? value.getBytes() : null;
    }

    default User mapIntegerToUser(Integer value) {
        if (value != null) {
            User user = new User();
            user.setId(value);
            return user;
        }
        return null;
    }

    default Ad mapIntegerToAd(Integer value) {
        if (value != null) {
            Ad ad = new Ad();
            ad.setId(value);
            return ad;
        }
        return null;
    }

    default String mapByteArrayToString(byte[] value) {
        return value != null ? new String(value) : null;
    }


    @AfterMapping
    default void mapUserAndCreatedAt(Comment comment, @MappingTarget CommentDTO commentDTO, @Context UserMapper userMapper) {
        if (comment != null) {
            commentDTO.setAuthor(userMapper.mapUserToInteger(comment.getAuthor()));
            commentDTO.setAuthorImage(comment.getAuthorImage());
            commentDTO.setAuthorFirstName(comment.getAuthorFirstName());
            commentDTO.setCreatedAt(comment.getCreatedAt());
            commentDTO.setText(comment.getText());

        }
    }



}

