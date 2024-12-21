package com.project.dailylog.service;

import com.project.dailylog.exception.DuplicateEmailException;
import com.project.dailylog.model.dto.LoginDTO;
import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.enums.Role;
import com.project.dailylog.model.request.SignupRequest;
import com.project.dailylog.model.request.UserRequest;
import com.project.dailylog.model.response.UserDetailInfoResponse;
import com.project.dailylog.model.response.UserSocialAccountResponse;
import com.project.dailylog.repository.UserRepository;
import com.project.dailylog.repository.UserSocialAccountRepository;
import com.project.dailylog.security.user.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserSocialAccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserSocialAccountRepository userSocialAccountRepository;

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

    @Transactional
    public void updateUserProfile(UserRequest userRequest, String profileImageUrl, CustomUserDetails customUserDetails) {
        User user = userRepository.findByEmail(customUserDetails.getUser().getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        if (profileImageUrl == null) {
            profileImageUrl = user.getProfile();
        }
        user.updateProfile(userRequest.getNickname(), profileImageUrl);

        userRepository.save(user);
    }

    @Transactional
    public UserDetailInfoResponse getUserProfile(User user) {
        return UserDetailInfoResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profile(user.getProfile())
                .socialAccounts(accountRepository.findByUser(user).stream()
                        .map(UserSocialAccountResponse::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public List<UserSocialAccountResponse> getUserSocialAccount(User user) {
        return userSocialAccountRepository.findByUser(user).stream()
                .map(UserSocialAccountResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserSocialAccount(String socialId, User user) {
        userSocialAccountRepository.deleteByIdAndAndUser(socialId, user);
    }
}
