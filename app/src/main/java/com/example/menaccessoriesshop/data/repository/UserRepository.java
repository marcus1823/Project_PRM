package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.APIClient;

public class UserRepository {
    public static UserService getUserService(){
        return APIClient.getClient().create(UserService.class);
    }
}
