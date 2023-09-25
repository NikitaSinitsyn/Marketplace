package com.skypro.Marketplace.controller;

import com.skypro.Marketplace.dto.comment.CommentDTO;
import com.skypro.Marketplace.dto.comment.Comments;
import com.skypro.Marketplace.dto.comment.CreateOrUpdateComment;
import com.skypro.Marketplace.service.impl.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ads/{adId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @GetMapping("/")
    public ResponseEntity<Comments> getComments(@PathVariable Integer adId) {

        Comments comments = commentService.getCommentsByAdId(adId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);


    }

    @PostMapping("/")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Integer adId, @RequestBody CreateOrUpdateComment createOrUpdateComment) {

        CommentDTO comment = commentService.addComment(adId, createOrUpdateComment);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);

    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') and (@commentService.isCommentOwner(authentication, #commentId) or hasRole('ADMIN'))")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId) {
        return commentService.deleteComment(commentId);
    }

    @PatchMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') and (@commentService.isCommentOwner(authentication, #commentId) or hasRole('ADMIN'))")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Integer commentId, @RequestBody CreateOrUpdateComment CreateOrUpdateComment) {

        CommentDTO updatedComment = commentService.updateComment(commentId, CreateOrUpdateComment);
        return ResponseEntity.status(HttpStatus.OK).body(updatedComment);

    }
}
