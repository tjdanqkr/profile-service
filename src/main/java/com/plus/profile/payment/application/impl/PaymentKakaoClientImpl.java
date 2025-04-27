package com.plus.profile.payment.application.impl;

import com.plus.profile.payment.application.PaymentKakaoClient;
import org.springframework.stereotype.Service;

@Service
public class PaymentKakaoClientImpl implements PaymentKakaoClient {
    /**
     curl --location 'https://open-api.kakaopay.com/online/v1/payment/approve' \
     --header 'Authorization: SECRET_KEY ${SECRET_KEY}' \
     --header 'Content-Type: application/json' \
     --data '{
     "cid": "TC0ONETIME",
     "tid": "T1234567890123456789",
     "partner_order_id": "partner_order_id",
     "partner_user_id": "partner_user_id",
     "pg_token": "xxxxxxxxxxxxxxxxxxxx"
     }'
     */

    @Override
    public String approve(String pgToken, String orderId, String supportKey) throws InterruptedException {

        Thread.sleep(1000);
        String response = "{\n" +
                "  \"aid\": \"A5678901234567890123\",\n" +
                "  \"tid\": \"T1234567890123456789\",\n" +
                "  \"cid\": \"TC0ONETIME\",\n" +
                "  \"partner_order_id\": \"partner_order_id\",\n" +
                "  \"partner_user_id\": \"partner_user_id\",\n" +
                "  \"payment_method_type\": \"MONEY\",\n" +
                "  \"item_name\": \"초코파이\",\n" +
                "  \"quantity\": 1,\n" +
                "  \"amount\": {\n" +
                "    \"total\": 2200,\n" +
                "    \"tax_free\": 0,\n" +
                "    \"vat\": 200,\n" +
                "    \"point\": 0,\n" +
                "    \"discount\": 0,\n" +
                "    \"green_deposit\": 0\n" +
                "  },\n" +
                "  \"created_at\": \"2023-07-15T21:18:22\",\n" +
                "  \"approved_at\": \"2023-07-15T21:18:22\"\n" +
                "}";
        return response;
    }
}
