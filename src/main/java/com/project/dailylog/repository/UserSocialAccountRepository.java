package com.project.dailylog.repository;

import com.project.dailylog.model.entity.User;
import com.project.dailylog.model.entity.UserSocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, String> {
    Optional<UserSocialAccount> findByIdAndUser(String id, User user);
    List<UserSocialAccount> findByUser(User user);
    void deleteByIdAndAndUser(String id, User user);
}