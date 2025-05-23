package com.plus.profile.point.domain;

import com.plus.profile.global.jpa.BaseTimeEntity;
import com.plus.profile.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="USER_COUPONS")
@Getter
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserCoupon extends BaseTimeEntity {
    @Id
    @Column(name="USER_COUPON_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="COUPON_ID", nullable = false)
    private UUID couponId;
    @Column(name="COUPON_CODE", nullable = false)
    private String couponCode;
    @Column(name="COUPON_IS_PERCENTAGE", nullable = false)
    private boolean couponIsPercentage;
    @Column(name="DISCOUNT_AMOUNT", nullable = false)
    private long discountAmount;
    @Column(name="EXPIRATION_DATE", nullable = false)
    private LocalDateTime expirationDate;
    @Column(name="DESCRIPTION", nullable = false)
    private String description;
    @Column(name="IS_USED", nullable = false)
    private boolean isUsed;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User user;

    public void useCoupon() {
        this.isUsed = true;
    }
}
