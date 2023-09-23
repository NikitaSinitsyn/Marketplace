package com.skypro.Marketplace.controller;

import com.skypro.Marketplace.dto.comment.CommentDTO;
import com.skypro.Marketplace.dto.comment.Comments;
import com.skypro.Marketplace.dto.comment.CreateOrUpdateComment;
import com.skypro.Marketplace.service.impl.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Comments> getComments(@PathVariable Integer adId, Authentication authentication) {

        Comments comments = commentService.getCommentsByAdId(adId, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(comments);


    }

    @PostMapping("/")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Integer adId, @RequestBody CreateOrUpdateComment createOrUpdateComment, Authentication authentication) {

        CommentDTO comment = commentService.addComment(adId, createOrUpdateComment, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);

    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId, Authentication authentication) {
        return commentService.deleteComment(commentId, authentication);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Integer commentId, @RequestBody CreateOrUpdateComment CreateOrUpdateComment, Authentication authentication) {

        CommentDTO updatedComment = commentService.updateComment(commentId, CreateOrUpdateComment, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(updatedComment);

    }
}
