package com.plus.profile.payment.domain;

import com.plus.profile.global.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

// 결제 트랜잭션
@Entity
@Table(name = "PAYMENT_TRANSACTIONS")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class PaymentTransaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TRANSACTION_ID")
    private UUID id;

    @Column(name = "USER_ID", nullable = false)
    private UUID userId;

    @Column(name = "ORDER_ID", nullable = false)
    private String orderId;

    @Column(name = "ORDER_NAME", nullable = false)
    private String orderName;

    @Column(name = "TRANSACTION_AMOUNT", nullable = false)
    private long transactionAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_STATUS", nullable = false)
    private PaymentTransactionStatusType transactionStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "PG_TYPE", nullable = false)
    private PayGatewayCompany pgType;

    @Column(name = "RECEIPT_URL")
    private String receiptUrl;

    @Lob
    @Column(name = "PAYMENT_RESPONSE", columnDefinition = "TEXT")
    private String paymentResponse;

    @Column(name = "IS_CANCELED", nullable = false)
    @Builder.Default
    private boolean isCanceled = false;

    @OneToOne(mappedBy = "paymentTransaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private PaymentCancellation paymentCancellation;
}


