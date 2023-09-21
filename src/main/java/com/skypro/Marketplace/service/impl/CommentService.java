package com.skypro.Marketplace.service.impl;


import com.skypro.Marketplace.dto.comment.CommentDTO;
import com.skypro.Marketplace.dto.comment.Comments;
import com.skypro.Marketplace.dto.comment.CreateOrUpdateComment;
import com.skypro.Marketplace.entity.Comment;
import com.skypro.Marketplace.entity.User;
import com.skypro.Marketplace.mapper.CommentMapper;
import com.skypro.Marketplace.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public Comments getCommentsByAdId(Integer adId) {
        try {
            List<Comment> comments = commentRepository.findByAdId(adId);
            List<CommentDTO> commentDTOs = comments.stream()
                    .map(commentMapper::commentToCommentDTO)
                    .collect(Collectors.toList());

            // Создайте объект Comments и установите в него количество и список комментариев
            return new Comments(commentDTOs.size(), commentDTOs);
        } catch (Exception e) {
            logger.error("An error occurred while getting comments by adId {}: {}", adId, e.getMessage());
            throw new RuntimeException("Failed to retrieve comments.", e);
        }
    }

    public CommentDTO addComment(Integer adId, CreateOrUpdateComment CreateOrUpdateComment) {
        try {
            Comment comment = new Comment();
            comment.setId(adId);
            comment.setText(CreateOrUpdateComment.getText());

            comment = commentRepository.save(comment);

            return commentMapper.commentToCommentDTO(comment);
        } catch (Exception e) {
            logger.error("An error occurred while adding a comment: {}", e.getMessage());
            throw new RuntimeException("Failed to add a comment.", e);
        }
    }

    public void deleteComment(Integer commentId) {
        try {
            commentRepository.deleteById(commentId);
        } catch (Exception e) {
            logger.error("An error occurred while deleting comment with id {}: {}", commentId, e.getMessage());
            throw new RuntimeException("Failed to delete comment.", e);
        }
    }

    public CommentDTO updateComment(Integer commentId, CreateOrUpdateComment CreateOrUpdateComment) {
        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

            comment.setText(CreateOrUpdateComment.getText());

            comment = commentRepository.save(comment);

            return commentMapper.commentToCommentDTO(comment);
        } catch (IllegalArgumentException e) {
            logger.error("Comment not found with id: {}", commentId);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while updating comment with id {}: {}", commentId, e.getMessage());
            throw new RuntimeException("Failed to update comment.", e);
        }
    }

    public boolean isCommentOwner(Authentication authentication, Integer commentId) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

            return comment.getAuthor().getId().equals(currentUser.getId());
        } catch (IllegalArgumentException e) {
            logger.error("Comment not found with id: {}", commentId);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while checking comment ownership for id {}: {}", commentId, e.getMessage());
            throw new RuntimeException("Failed to check comment ownership.", e);
        }
    }

}
