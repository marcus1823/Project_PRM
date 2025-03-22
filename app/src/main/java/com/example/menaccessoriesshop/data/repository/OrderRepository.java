package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.APIClient;

public class OrderRepository {
    public static OrderService getOrderService(){
        return APIClient.getClient().create(OrderService.class);
    }
}
