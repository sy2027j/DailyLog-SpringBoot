package com.project.dailylog.service;

import com.project.dailylog.exception.DuplicateEmailException;
import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.enums.Role;
import com.project.dailylog.model.request.SignupRequest;
import com.project.dailylog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void registerUser(SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new DuplicateEmailException("이미 회원가입된 이메일입니다.");
        }

        userRepository.save(User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(Role.USER)
                .build()
        );
    }

    @Transactional
    public LoginDTO getUserInfo(String userEmail) {
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        User user = optionalUser.orElseThrow(() -> new NoSuchElementException("회원 정보를 찾을 수 없습니다."));
        return user.toLoginDTO();
    }
}