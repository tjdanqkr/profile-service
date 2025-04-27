package com.plus.profile.payment.domain.repository;

import com.plus.profile.payment.domain.PaymentCancellation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCancellationRepository extends JpaRepository<PaymentCancellation, Long> {

}
