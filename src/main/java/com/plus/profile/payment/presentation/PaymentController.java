package com.plus.profile.payment.presentation;

import com.plus.profile.global.dto.PayGatewayCompany;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    @PostMapping("/api/v1/payment/{type}/callback")
    public void paymentCallback(@PathVariable PayGatewayCompany type) {


    }
}
