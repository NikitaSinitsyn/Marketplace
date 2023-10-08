package com.skypro.Marketplace.repository;

import com.skypro.Marketplace.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Ad entities.
 */
@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {

    /**
     * Retrieves a list of ads associated with a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of Ad entities associated with the user.
     */
    @Query("SELECT a FROM Ad a WHERE a.user.id = :userId")
    List<Ad> findByUserId(Integer userId);

    /**
     * Checks if an ad with a specific ID is owned by a user with a specific ID.
     *
     * @param adId   The ID of the ad.
     * @param userId The ID of the user.
     * @return true if the ad is owned by the user, false otherwise.
     */
    boolean existsByIdAndUser_Id(Integer adId, Integer userId);
}
