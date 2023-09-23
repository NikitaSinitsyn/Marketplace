package com.skypro.Marketplace.repository;

import com.skypro.Marketplace.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByAdId(Integer adId);
    boolean existsByIdAndAd_User_Id(Integer commentId, Integer userId);

}

