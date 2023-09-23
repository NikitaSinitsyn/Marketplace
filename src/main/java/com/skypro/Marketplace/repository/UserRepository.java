package com.skypro.Marketplace.repository;

import com.skypro.Marketplace.dto.user.UserDTO;
import com.skypro.Marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    UserDTO findByEmail(String email);

}
