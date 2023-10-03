package com.skypro.Marketplace.repository;

import com.skypro.Marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Retrieves a user by their email.
     *
     * @param email The email address of the user.
     * @return An Optional containing the User entity if found, or an empty Optional if not found.
     */
    Optional<User> findByEmail(String email);


}
