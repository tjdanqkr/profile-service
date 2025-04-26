package com.plus.profile.user.domain;

import com.plus.profile.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="USERS",
        indexes={
                @Index(name="IDX_USER_USERNAME", columnList="username"),
                @Index(name="IDX_USER_CREATED_AT", columnList="createdAt")
        })
@Getter
@Builder
@ToString
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="USER_ID")
    private UUID id;

    @Column(name="USERNAME", unique=true, nullable=false)
    private String username;

    @Column(name="PASSWORD", nullable=false)
    private String encodedPassword;

    @Enumerated(EnumType.STRING)
    @Column(name="ROLE", nullable=false)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @OneToMany
    @JoinColumn(name = "USER_ID")
    @ToString.Exclude
    @Builder.Default
    private Set<UserCoupon> userCoupons = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    UserPoint userPoint;
}
