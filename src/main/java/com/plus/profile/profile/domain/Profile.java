package com.plus.profile.profile.domain;

import com.plus.profile.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="PROFILES",
        indexes={
                @Index(name="IDX_PROFILE_USERID", columnList="USER_ID"),
                @Index(name="IDX_PROFILE_USERNAME", columnList="USERNAME"),
                @Index(name="IDX_PROFILE_TITLE", columnList="TITLE"),
                @Index(name="IDX_PROFILE_CREATED_AT", columnList="CREATED_AT"),
        })
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of="id", callSuper=false)
public class Profile extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="PROFILE_ID")
    private UUID id;

    @Column(name="TITLE", nullable=false)
    private String title;

    @Column(name="CONTENT", nullable=false)
    private String content;

    @Column(name="IS_DELETED", nullable=false)
    @Builder.Default
    private boolean deleted = false;

    @Column(name="VIEW_COUNT", nullable=false)
    @Builder.Default
    private long viewCount = 0;

    @Column(name = "USER_ID", nullable=false, unique = true)
    private UUID userId;

    @Column(name = "USERNAME", nullable=false, unique = true)
    private String username;

    @OneToMany(mappedBy="profile", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private Set<ProfileView> profileViews = new HashSet<>();

}
