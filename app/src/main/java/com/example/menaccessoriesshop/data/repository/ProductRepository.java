package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.APIClient;

public class ProductRepository {
    public static ProductService getProductService(){
        return APIClient.getClient().create(ProductService.class);
    }
}
