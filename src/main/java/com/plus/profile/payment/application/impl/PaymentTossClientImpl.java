package com.plus.profile.payment.application.impl;

import com.plus.profile.global.exception.BusinessException;
import com.plus.profile.payment.application.PaymentTossClient;
import com.plus.profile.payment.exception.PaymentExceptionCode;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;

@Service
public class PaymentTossClientImpl implements PaymentTossClient {
    /**
     HttpRequest request = HttpRequest.newBuilder()
     .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
     .header("Authorization", "Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==")
     .header("Content-Type", "application/json")
     .method("POST", HttpRequest.BodyPublishers.ofString("{\"paymentKey\":\"5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1\",\"orderId\":\"a4CWyWY5m89PNh7xJwhk1\",\"amount\":1000}"))
     .build();
     HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
     System.out.println(response.body());
     refer: https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8
     */
    @Override
    public String approve(String paymentKey, String orderId) throws Exception {

        Thread.sleep(1000);


        String sampleResponseBody = "{\n" +
                "  \"mId\": \"tosspayments\",\n" +
                "  \"lastTransactionKey\": \"9C62B18EEF0DE3EB7F4422EB6D14BC6E\",\n" +
                "  \"paymentKey\": \"5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1\",\n" +
                "  \"orderId\": \"a4CWyWY5m89PNh7xJwhk1\",\n" +
                "  \"orderName\": \"토스 티셔츠 외 2건\",\n" +
                "  \"taxExemptionAmount\": 0,\n" +
                "  \"status\": \"DONE\",\n" +
                "  \"requestedAt\": \"2024-02-13T12:17:57+09:00\",\n" +
                "  \"approvedAt\": \"2024-02-13T12:18:14+09:00\",\n" +
                "  \"useEscrow\": false,\n" +
                "  \"cultureExpense\": false,\n" +
                "  \"card\": {\n" +
                "    \"issuerCode\": \"71\",\n" +
                "    \"acquirerCode\": \"71\",\n" +
                "    \"number\": \"12345678****000*\",\n" +
                "    \"installmentPlanMonths\": 0,\n" +
                "    \"isInterestFree\": false,\n" +
                "    \"interestPayer\": null,\n" +
                "    \"approveNo\": \"00000000\",\n" +
                "    \"useCardPoint\": false,\n" +
                "    \"cardType\": \"신용\",\n" +
                "    \"ownerType\": \"개인\",\n" +
                "    \"acquireStatus\": \"READY\",\n" +
                "    \"amount\": 1000\n" +
                "  },\n" +
                "  \"virtualAccount\": null,\n" +
                "  \"transfer\": null,\n" +
                "  \"mobilePhone\": null,\n" +
                "  \"giftCertificate\": null,\n" +
                "  \"cashReceipt\": null,\n" +
                "  \"cashReceipts\": null,\n" +
                "  \"discount\": null,\n" +
                "  \"cancels\": null,\n" +
                "  \"secret\": null,\n" +
                "  \"type\": \"NORMAL\",\n" +
                "  \"easyPay\": {\n" +
                "    \"provider\": \"토스페이\",\n" +
                "    \"amount\": 0,\n" +
                "    \"discountAmount\": 0\n" +
                "  },\n" +
                "  \"country\": \"KR\",\n" +
                "  \"failure\": null,\n" +
                "  \"isPartialCancelable\": true,\n" +
                "  \"receipt\": {\n" +
                "    \"url\": \"https://dashboard.tosspayments.com/receipt/redirection?transactionId=tviva20240213121757MvuS8&ref=PX\"\n" +
                "  },\n" +
                "  \"checkout\": {\n" +
                "    \"url\": \"https://api.tosspayments.com/v1/payments/5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1/checkout\"\n" +
                "  },\n" +
                "  \"currency\": \"KRW\",\n" +
                "  \"totalAmount\": 1000,\n" +
                "  \"balanceAmount\": 1000,\n" +
                "  \"suppliedAmount\": 909,\n" +
                "  \"vat\": 91,\n" +
                "  \"taxFreeAmount\": 0,\n" +
                "  \"metadata\": null,\n" +
                "  \"method\": \"카드\",\n" +
                "  \"version\": \"2022-11-16\"\n" +
                "}\n";
        return sampleResponseBody;
    }
}
