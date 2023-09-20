package com.skypro.Marketplace.controller;

import com.skypro.Marketplace.dto.comment.CommentDTO;
import com.skypro.Marketplace.dto.comment.Comments;
import com.skypro.Marketplace.dto.comment.CreateOrUpdateComment;
import com.skypro.Marketplace.exception.ForbiddenException;
import com.skypro.Marketplace.exception.UnauthorizedException;
import com.skypro.Marketplace.service.impl.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ads/{adId}/comments")
public class CommentController {

    private final CommentService commentService;
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping("/")
    public ResponseEntity<Comments> getComments(@PathVariable Integer adId) {
        try {
            Comments comments = commentService.getCommentsByAdId(adId);

            if (comments.getResults().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("An error occurred while fetching comments for ad with ID: " + adId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Integer adId, @RequestBody CreateOrUpdateComment createOrUpdateComment) {
        try {
            CommentDTO comment = commentService.addComment(adId, createOrUpdateComment);
            if (comment != null) {
                return new ResponseEntity<>(comment, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("An error occurred while adding a comment to ad with ID: " + adId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') and (@commentService.isCommentOwner(authentication, #commentId) or hasRole('ADMIN'))")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        try {
            commentService.deleteComment(commentId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("An error occurred while deleting comment with ID: " + commentId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') and (@commentService.isCommentOwner(authentication, #commentId) or hasRole('ADMIN'))")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Integer commentId, @RequestBody CreateOrUpdateComment CreateOrUpdateComment) {
        try {
            CommentDTO updatedComment = commentService.updateComment(commentId, CreateOrUpdateComment);
            if (updatedComment != null) {
                return new ResponseEntity<>(updatedComment, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("An error occurred while updating comment with ID: " + commentId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
