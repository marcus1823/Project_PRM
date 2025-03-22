package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.APIClient;

public class StoreRepository {
    public static StoreService getStoreService(){
        return APIClient.getClient().create(StoreService.class);
    }
}
