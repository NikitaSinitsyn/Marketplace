package com.skypro.Marketplace.mapper;

import com.skypro.Marketplace.dto.comment.CommentDTO;
import com.skypro.Marketplace.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link Comment} entities and {@link CommentDTO} data transfer objects.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Converts a {@link Comment} entity to a {@link CommentDTO} data transfer object.
     *
     * @param comment The {@link Comment} entity to be converted.
     * @return The corresponding {@link CommentDTO}.
     */
    @Mapping(source = "id", target = "pk")
    @Mapping(target = "authorImage", source = "author.image")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(source = "author.id", target = "author")
    CommentDTO commentToCommentDTO(Comment comment);

    /**
     * Converts a {@link CommentDTO} data transfer object to a {@link Comment} entity.
     *
     * @param commentDTO The {@link CommentDTO} to be converted.
     * @return The corresponding {@link Comment} entity.
     */
    @Mapping(source = "pk", target = "id")
    @Mapping(source = "author", target = "author.id")
    Comment commentDTOToComment(CommentDTO commentDTO);


}