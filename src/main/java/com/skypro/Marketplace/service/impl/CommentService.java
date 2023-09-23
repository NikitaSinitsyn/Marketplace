package com.skypro.Marketplace.service.impl;


import com.skypro.Marketplace.dto.comment.CommentDTO;
import com.skypro.Marketplace.dto.comment.Comments;
import com.skypro.Marketplace.dto.comment.CreateOrUpdateComment;
import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.entity.Ad;
import com.skypro.Marketplace.entity.Comment;
import com.skypro.Marketplace.exception.AdNotFoundException;
import com.skypro.Marketplace.exception.CommentNotFoundException;
import com.skypro.Marketplace.exception.ForbiddenException;
import com.skypro.Marketplace.exception.UnauthorizedException;
import com.skypro.Marketplace.mapper.CommentMapper;
import com.skypro.Marketplace.repository.AdRepository;
import com.skypro.Marketplace.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdRepository adRepository;
    private final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper, AdRepository adRepository) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.adRepository = adRepository;
    }

    public Comments getCommentsByAdId(Integer adId, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UnauthorizedException("Authentication required to get comments.");
            }
            List<Comment> comments = commentRepository.findByAdId(adId);
            if (comments.isEmpty()) {
                throw new AdNotFoundException("No comments found for Ad with id: " + adId);
            }
            List<CommentDTO> commentDTOs = comments.stream()
                    .map(commentMapper::commentToCommentDTO)
                    .collect(Collectors.toList());
            return new Comments(commentDTOs.size(), commentDTOs);
        } catch (Exception e) {
            logger.error("An error occurred while getting comments by adId {}: {}", adId, e.getMessage());
            throw new RuntimeException("Failed to retrieve comments.", e);
        }
    }

    public CommentDTO addComment(Integer adId, CreateOrUpdateComment CreateOrUpdateComment, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UnauthorizedException("Authentication required to add a comment.");
            }
            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));
            Comment comment = new Comment();
            comment.setAd(ad);
            comment.setText(CreateOrUpdateComment.getText());

            comment = commentRepository.save(comment);

            return commentMapper.commentToCommentDTO(comment);
        } catch (Exception e) {
            logger.error("An error occurred while adding a comment: {}", e.getMessage());
            throw new RuntimeException("Failed to add a comment.", e);
        }
    }

    public ResponseEntity<?> deleteComment(Integer commentId, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UnauthorizedException("Authentication required to delete a comment.");
            }
            Optional<Comment> optionalComment = commentRepository.findById(commentId);
            Comment comment = optionalComment.orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));
            if (!isCommentOwner(authentication, commentId) && !hasAdminRole(authentication)) {
                throw new ForbiddenException("Access forbidden to update this ad.");
            }
            commentRepository.deleteById(commentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("An error occurred while deleting comment with id {}: {}", commentId, e.getMessage());
            throw new RuntimeException("Failed to delete comment.", e);
        }
    }

    public CommentDTO updateComment(Integer commentId, CreateOrUpdateComment CreateOrUpdateComment, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UnauthorizedException("Authentication required to update a comment.");
            }
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));
            if (!isCommentOwner(authentication, commentId) && !hasAdminRole(authentication)) {
                throw new ForbiddenException("Access forbidden to update this ad.");
            }

            comment.setText(CreateOrUpdateComment.getText());

            comment = commentRepository.save(comment);

            return commentMapper.commentToCommentDTO(comment);

        } catch (Exception e) {
            logger.error("An error occurred while updating comment with id {}: {}", commentId, e.getMessage());
            throw new RuntimeException("Failed to update comment.", e);
        }
    }

    private boolean isCommentOwner(Authentication authentication, Integer commentId) {
        if (authentication != null && authentication.isAuthenticated()) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            return commentRepository.existsByIdAndAd_User_Id(commentId, securityUser.getId());
        }
        return false;
    }

    private boolean hasAdminRole(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

}
