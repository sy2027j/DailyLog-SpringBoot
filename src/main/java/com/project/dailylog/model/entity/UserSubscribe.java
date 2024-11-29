package com.project.dailylog.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@Table(name = "user_subscribe")
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscribe {

    @EmbeddedId
    private UserSubscribeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subUserId")
    @JoinColumn(name = "sub_user_id")
    private User subscribedUser;

    @Builder.Default
    @Column(name = "subscribed_at", nullable = false)
    private LocalDateTime subscribedAt = LocalDateTime.now();
}
