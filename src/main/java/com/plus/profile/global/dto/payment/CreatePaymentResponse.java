package com.plus.profile.global.dto.payment;

import java.util.UUID;



public record CreatePaymentResponse(
        UUID orderId, String orderName
        , long transactionAmount
        , String transactionStatus
        , String pgType) {
}
