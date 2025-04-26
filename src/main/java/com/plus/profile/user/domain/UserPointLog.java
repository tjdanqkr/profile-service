package com.plus.profile.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="USER_POINTS_LOG",
        indexes={
                @Index(name = "IDX_PAYMENT_USER_COUPON_USERID", columnList = "USERID"),
                @Index(name = "IDX_PAYMENT_USER_COUPON_COUPON_CODE", columnList = "COUPON_CODE"),
                @Index(name = "IDX_PAYMENT_USER_COUPON_IS_USED", columnList = "IS_USED"),
                @Index(name = "IDX_PAYMENT_USER_COUPON_EXPIRATION_DATE", columnList = "EXPIRATION_DATE"),
        })
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserPointLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="USER_POINTS_LOG_ID")
    private Long id;

    @Column(name="COUPON_IS_USED", nullable = false)
    @Builder.Default
    private boolean couponIsUsed = false;
    @Column(name="COUPON_ID")
    private UUID couponId;
    @Column(name="USER_COUPON_ID")
    private Long userCouponId;
    @Column(name="COUPON_DESCRIPTION")
    private String couponDescription;
    @Column(name="COUPON_DISCOUNT_AMOUNT")
    private Long couponDiscountAmount;

    @Column(name="BEFORE_POINTS", nullable=false)
    private long beforePoints;
    @Column(name="AFTER_POINTS", nullable=false)
    private long afterPoints;
    @Column(name="POINTS_AMOUNT", nullable=false)
    private long pointsAmount;

    @Enumerated(EnumType.STRING)
    @Column(name="POINT_LOG_TYPE", nullable=false)
    private UserPointLogType type;

}
