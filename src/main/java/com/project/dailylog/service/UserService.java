package com.project.dailylog.service;

import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.request.SignupRequest;
import com.project.dailylog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void registerUser(SignupRequest signupRequest) {
        // 이메일 중복 체크
        userRepository.save(User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .build()
        );
    }
}