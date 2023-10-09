package com.skypro.Marketplace.service.impl;


import com.skypro.Marketplace.dto.comment.CommentDTO;
import com.skypro.Marketplace.dto.comment.Comments;
import com.skypro.Marketplace.dto.comment.CreateOrUpdateComment;
import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.entity.Ad;
import com.skypro.Marketplace.entity.Comment;
import com.skypro.Marketplace.entity.User;
import com.skypro.Marketplace.exception.AdNotFoundException;
import com.skypro.Marketplace.exception.CommentNotFoundException;
import com.skypro.Marketplace.exception.ForbiddenException;
import com.skypro.Marketplace.mapper.CommentMapper;
import com.skypro.Marketplace.repository.AdRepository;
import com.skypro.Marketplace.repository.CommentRepository;
import com.skypro.Marketplace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing comments on advertisements.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper, AdRepository adRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.adRepository = adRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get comments for a specific advertisement by its ID.
     *
     * @param adId Advertisement ID.
     * @return Comments for the specified advertisement.
     */
    public Comments getCommentsByAdId(Integer adId) {

        List<Comment> comments = commentRepository.findByAdId(adId);
        if (comments.isEmpty()) {
            throw new AdNotFoundException("No comments found for Ad with id: " + adId);
        }
        List<CommentDTO> commentDTOs = comments.stream()
                .map(commentMapper::commentToCommentDTO)
                .collect(Collectors.toList());
        return new Comments(commentDTOs.size(), commentDTOs);
    }

    /**
     * Add a new comment to an advertisement.
     *
     * @param adId                  Advertisement ID.
     * @param CreateOrUpdateComment Comment data to be added.
     * @return Created comment data.
     */
    @Transactional
    public CommentDTO addComment(Integer adId, CreateOrUpdateComment CreateOrUpdateComment) {

        Optional<Ad> optionalAd = adRepository.findById(adId);
        Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));
        Comment comment = new Comment();
        comment.setAd(ad);
        comment.setText(CreateOrUpdateComment.getText());
        comment.setCreatedAt(System.currentTimeMillis());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            User author = userRepository.findById(securityUser.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + securityUser.getId()));
            comment.setAuthor(author);
        }

        comment = commentRepository.save(comment);

        return commentMapper.commentToCommentDTO(comment);
    }

    /**
     * Delete a comment by its ID.
     *
     * @param commentId Comment ID.
     * @return HTTP response indicating success.
     */
    @Transactional
    public ResponseEntity<?> deleteComment(Integer commentId, Authentication authentication) {

        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        Comment comment = optionalComment.orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        if (!isCommentOwner(authentication, commentId) && !hasAdminRole(authentication)) {
            throw new ForbiddenException("Access forbidden to update this ad.");
        }

        commentRepository.deleteById(commentId);
        return ResponseEntity.ok().build();
    }

    /**
     * Update a comment by its ID.
     *
     * @param commentId             Comment ID.
     * @param CreateOrUpdateComment Updated comment data.
     * @return Updated comment data.
     */
    @Transactional
    public CommentDTO updateComment(Integer commentId, CreateOrUpdateComment CreateOrUpdateComment, Authentication authentication) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));
        if (!isCommentOwner(authentication, commentId) && !hasAdminRole(authentication)) {
            throw new ForbiddenException("Access forbidden to update this ad.");
        }

        comment.setText(CreateOrUpdateComment.getText());

        comment = commentRepository.save(comment);

        return commentMapper.commentToCommentDTO(comment);
    }

    /**
     * Check if the authenticated user is the owner of a comment.
     *
     * @param authentication Information about the current user's authentication.
     * @param commentId      Comment ID.
     * @return True if the user is the owner, false otherwise.
     */
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
