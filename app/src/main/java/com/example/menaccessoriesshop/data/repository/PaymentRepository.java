package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.APIClient;

public class PaymentRepository {
    public static PaymentService getPaymentService(){
        return APIClient.getClient().create(PaymentService.class);
    }
}
