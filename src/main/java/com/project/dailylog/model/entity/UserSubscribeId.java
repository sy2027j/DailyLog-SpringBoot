package com.project.dailylog.model.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSubscribeId implements Serializable {

    private Long userId;
    private Long subUserId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSubscribeId that = (UserSubscribeId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(subUserId, that.subUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, subUserId);
    }

}
