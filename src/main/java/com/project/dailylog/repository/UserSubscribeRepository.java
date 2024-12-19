package com.project.dailylog.repository;

import com.project.dailylog.model.entity.UserSubscribe;
import com.project.dailylog.model.entity.UserSubscribeId;
import com.project.dailylog.repository.custom.UserSubscribeRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscribeRepository extends JpaRepository<UserSubscribe, UserSubscribeId>, UserSubscribeRepositoryCustom {
    boolean existsById(UserSubscribeId id);
    void deleteById(UserSubscribeId id);
}
