package com.checkmate.users.service;

import com.checkmate.users.model.entity.User;
import com.checkmate.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfilesService {

    @Autowired
    private UserRepository userRepository;

}
