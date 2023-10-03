package com.skypro.Marketplace.repository;

import com.skypro.Marketplace.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Comment entities.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /**
     * Retrieves a list of comments associated with a specific ad.
     *
     * @param adId The ID of the ad.
     * @return A list of Comment entities associated with the ad.
     */
    List<Comment> findByAdId(Integer adId);

    /**
     * Checks if a comment with a specific ID is owned by a user with a specific ID.
     *
     * @param commentId The ID of the comment.
     * @param userId    The ID of the user.
     * @return true if the comment is owned by the user, false otherwise.
     */
    boolean existsByIdAndAd_User_Id(Integer commentId, Integer userId);

}

