package com.plus.profile.payment.domain.repository;

import com.plus.profile.payment.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByOrderId(UUID orderId);
    Optional<PaymentTransaction> findByOrderIdAndUserId(UUID orderId, UUID userId);
}
