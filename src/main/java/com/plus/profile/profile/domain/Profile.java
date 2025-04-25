package com.plus.profile.profile.domain;

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
@ToString
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of="id", callSuper=false)
public class Profile {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="PROFILE_ID")
    private UUID id;

    @Column(name="TITLE")
    private String title;

    @Column(name="DETAIL")
    private String detail;

    @Column(name="IS_DELETED")
    private boolean deleted;

    @Column(name="VIEW_COUNT")
    private long viewCount;

    @Column(name = "USER_ID")
    private UUID userId;

    @Column(name = "USERNAME")
    private String username;

    @OneToMany(mappedBy="profile", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private Set<ProfileView> profileViews = new HashSet<>();

}
