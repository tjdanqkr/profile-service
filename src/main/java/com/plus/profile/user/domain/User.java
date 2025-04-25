package com.plus.profile.user.domain;

import com.plus.profile.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name="USERNAME", unique=true)
    private String username;

    @Column(name="PASSWORD")
    private String encodedPassword;

    @Enumerated(EnumType.STRING)
    @Column(name="ROLE")
    @Builder.Default
    private UserRole role = UserRole.USER;

}
