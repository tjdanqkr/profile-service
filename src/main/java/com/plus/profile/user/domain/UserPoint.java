package com.plus.profile.user.domain;

import com.plus.profile.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="USER_POINTS")
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserPoint extends BaseTimeEntity {
    @Id
    @Column(name="POINT_ID")
    private Long id;

    @Column(name="POINT", nullable = false)
    private Long point;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="USER_ID", referencedColumnName = "USER_ID")
    private User user;
}
