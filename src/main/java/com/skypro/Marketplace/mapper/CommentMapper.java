package com.skypro.Marketplace.mapper;

import com.skypro.Marketplace.dto.comment.CommentDTO;
import com.skypro.Marketplace.entity.Ad;
import com.skypro.Marketplace.entity.Comment;
import com.skypro.Marketplace.entity.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "id", target = "pk")
    @Mapping(target = "authorImage", source = "author.image")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    CommentDTO commentToCommentDTO(Comment comment);


    @Mapping(source = "pk", target = "id")
    Comment commentDTOToComment(CommentDTO commentDTO);

    default User map(Integer userId) {
        if (userId != null) {
            User user = new User();
            user.setId(userId);
            return user;
        }
        return null;
    }

    default Integer map(User user) {
        return user != null ? user.getId() : null;
    }
}