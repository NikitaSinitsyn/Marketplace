package com.skypro.Marketplace.service;


import com.skypro.Marketplace.dto.Register;

public interface AuthService {
    boolean login(String userName, String password);

    boolean register(Register register);
}
