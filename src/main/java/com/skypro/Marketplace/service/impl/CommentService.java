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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CommentDTO addComment(Integer adId, CreateOrUpdateComment CreateOrUpdateComment) {

            Optional<Ad> optionalAd = adRepository.findById(adId);
            Ad ad = optionalAd.orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));
            Comment comment = new Comment();
            comment.setAd(ad);
            comment.setText(CreateOrUpdateComment.getText());

            comment = commentRepository.save(comment);

            return commentMapper.commentToCommentDTO(comment);

    }

    @Transactional
    public ResponseEntity<?> deleteComment(Integer commentId) {


            Optional<Comment> optionalComment = commentRepository.findById(commentId);
            Comment comment = optionalComment.orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

            commentRepository.deleteById(commentId);
            return ResponseEntity.ok().build();

    }

    @Transactional
    public CommentDTO updateComment(Integer commentId, CreateOrUpdateComment CreateOrUpdateComment) {


            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

            comment.setText(CreateOrUpdateComment.getText());

            comment = commentRepository.save(comment);

            return commentMapper.commentToCommentDTO(comment);


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
