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
    CommentDTO commentToCommentDTO(Comment comment);

    default Integer mapUserToInteger(User user) {
        return user != null ? user.getId() : null;
    }

    default User mapIntegerToUser(Integer userId) {
        if (userId != null) {
            User user = new User();
            user.setId(userId);
            return user;
        }
        return null;
    }

    default Integer mapAdToInteger(Ad ad) {
        return ad != null ? ad.getId() : null;
    }

    default Ad mapIntegerToAd(Integer adId) {
        if (adId != null) {
            Ad ad = new Ad();
            ad.setId(adId);
            return ad;
        }
        return null;
    }

    @AfterMapping
    default void mapUserAndCreatedAt(Comment comment, @MappingTarget CommentDTO commentDTO) {
        if (comment != null) {
            commentDTO.setAuthor(mapUserToInteger(comment.getAuthor()));
            commentDTO.setAuthorImage(comment.getAuthorImage());
            commentDTO.setAuthorFirstName(comment.getAuthorFirstName());
            commentDTO.setCreatedAt(comment.getCreatedAt());
            commentDTO.setText(comment.getText());
        }
    }

    @Mapping(source = "pk", target = "id")
    Comment commentDTOToComment(CommentDTO commentDTO);
}