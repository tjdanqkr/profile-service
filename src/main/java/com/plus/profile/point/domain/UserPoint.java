package com.plus.profile.point.domain;

import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.global.jpa.BaseTimeEntity;
import com.plus.profile.point.exception.PointExceptionCode;
import com.plus.profile.user.domain.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="USER_POINTS")
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserPoint extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="POINT_ID")
    private Long id;

    @Column(name="POINT", nullable = false)
    private long point;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="USER_ID", referencedColumnName = "USER_ID")
    private User user;

    public void charge(long amount) {
        this.point += amount;
    }
    public void payOff(long amount) {
        this.point -= amount;
    }
}
