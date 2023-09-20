package com.skypro.Marketplace.repository;

import com.skypro.Marketplace.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {

    @Query("SELECT a FROM Ad a WHERE a.user.id = :userId")
    List<Ad> findByUserId(Integer userId);
}
