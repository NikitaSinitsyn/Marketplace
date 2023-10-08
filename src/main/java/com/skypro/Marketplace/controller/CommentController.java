package com.skypro.Marketplace.controller;

import com.skypro.Marketplace.dto.comment.CommentDTO;
import com.skypro.Marketplace.dto.comment.Comments;
import com.skypro.Marketplace.dto.comment.CreateOrUpdateComment;
import com.skypro.Marketplace.service.impl.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing comments on advertisements.
 */
@RestController
@RequestMapping("/ads/{adId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Get comments for a specific advertisement.
     *
     * @param adId Advertisement ID.
     * @return Comments for the specified advertisement.
     */
    @GetMapping({"", "/"})
    public ResponseEntity<Comments> getComments(@PathVariable Integer adId) {

        Comments comments = commentService.getCommentsByAdId(adId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    /**
     * Add a new comment to an advertisement.
     *
     * @param adId               Advertisement ID.
     * @param createOrUpdateComment Comment data to be added.
     * @return Created comment data.
     */
    @PostMapping({"", "/"})
    public ResponseEntity<CommentDTO> addComment(@PathVariable Integer adId, @RequestBody CreateOrUpdateComment createOrUpdateComment) {

        CommentDTO comment = commentService.addComment(adId, createOrUpdateComment);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    /**
     * Delete a comment by its ID.
     *
     * @param commentId Comment ID.
     * @return HTTP response indicating success.
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("@commentService.isCommentOwner(authentication, #commentId) or hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId) {
        return commentService.deleteComment(commentId);
    }

    /**
     * Update a comment by its ID.
     *
     * @param commentId           Comment ID.
     * @param CreateOrUpdateComment Updated comment data.
     * @return Updated comment data.
     */
    @PatchMapping("/{commentId}")
    @PreAuthorize("@commentService.isCommentOwner(authentication, #commentId) or hasRole('ADMIN')")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Integer commentId, @RequestBody CreateOrUpdateComment CreateOrUpdateComment) {

        CommentDTO updatedComment = commentService.updateComment(commentId, CreateOrUpdateComment);
        return ResponseEntity.status(HttpStatus.OK).body(updatedComment);
    }
}
