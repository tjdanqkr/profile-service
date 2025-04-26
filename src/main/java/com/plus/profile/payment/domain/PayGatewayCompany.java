package com.plus.profile.payment.domain;

import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMethod;
@Getter
public enum PayGatewayCompany {
    TOSS("https://api.tosspayments.com/v1/payments/{orderId}", RequestMethod.GET),
    KAKAO_PAY("https://kapi.kakao.com/v1/payment/order", RequestMethod.POST),
    OTHER("https://test.com/v1/payments", RequestMethod.POST);

    private final String successUrl;
    private final RequestMethod method;

    PayGatewayCompany(String successUrl, RequestMethod method) {
        this.successUrl = successUrl;
        this.method = method;
    }
    public String getBody(String orderId) {
        if(method == RequestMethod.POST) {
            return "{ \"cid\": " + "\"cid\"" + ",\"orderId\": \"" + orderId + "\" }";
        }
        return successUrl.replace("{orderId}", orderId);
    }
}
