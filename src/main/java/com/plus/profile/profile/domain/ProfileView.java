package com.plus.profile.profile.domain;


import com.plus.profile.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="PROFILES_VIEWS",
        indexes={
                @Index(name="IDX_PROFILE_VIEWED_PROFILE_ID", columnList="PROFILE_ID"),
        })
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of="id", callSuper=false)
public class ProfileView {
    @Id
    @Column(name="PROFILE_VIEW_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="PROFILE_ID")
    private Profile profile;

    @CreatedDate
    @Column(name="VIEWED_AT")
    private LocalDateTime viewedAt;
    public static ProfileView of(UUID profileId) {
        return ProfileView.builder()
                .profile(Profile.builder().id(profileId).build())
                .viewedAt(LocalDateTime.now())
                .build();
    }
}
