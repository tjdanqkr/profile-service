package com.plus.profile.payment.domain;

import com.plus.profile.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "PAYMENT_CANCELLATIONS")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class PaymentCancellation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CANCELLATION_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID", nullable = false)
    private PaymentTransaction paymentTransaction;

    @Column(name = "CANCEL_AMOUNT", nullable = false)
    private long cancelAmount;

    @Column(name = "CANCEL_REASON")
    private String cancelReason;

    @Column(name = "CANCEL_RECEIPT_URL")
    private String cancelReceiptUrl;

    @Lob
    @Column(name = "CANCEL_RESPONSE", columnDefinition = "TEXT")
    private String cancelResponse;
}
