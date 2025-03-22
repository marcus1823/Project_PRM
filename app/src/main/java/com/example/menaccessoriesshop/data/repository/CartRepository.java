package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.APIClient;

public class CartRepository {
    public static CartService getCartService(){
        return APIClient.getClient().create(CartService.class);
    }
}
