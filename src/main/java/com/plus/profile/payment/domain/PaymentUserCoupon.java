package com.plus.profile.payment.domain;

import com.plus.profile.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PAYMENT_USER_COUPONS",
        indexes = {
                @Index(name = "IDX_USERID", columnList = "USERID"),
                @Index(name = "IDX_COUPON_CODE", columnList = "COUPON_CODE"),
                @Index(name = "IDX_IS_USED", columnList = "IS_USED"),
                @Index(name = "IDX_EXPIRATION_DATE", columnList = "EXPIRATION_DATE")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class PaymentUserCoupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_USER_COUPON_ID")
    private Long id;

    @Column(name = "USERID", nullable = false)
    private UUID userId;

    @Column(name = "COUPON_ID", nullable = false)
    private UUID couponId;

    @Column(name = "USER_COUPON_ID", nullable = false)
    private Long userCouponId;

    @Column(name = "COUPON_CODE", nullable = false)
    private String couponCode;

    @Column(name = "DISCOUNT_AMOUNT", nullable = false)
    private Long discountAmount;

    @Column(name = "EXPIRATION_DATE", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "IS_USED", nullable = false)
    @Builder.Default
    private boolean isUsed = false;
}
