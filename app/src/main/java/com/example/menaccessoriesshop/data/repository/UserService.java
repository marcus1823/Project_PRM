package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.data.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {
    String USERS = "User";

    @GET(USERS)
    Call<User[]> getAllUsers();

    @GET(USERS + "/{id}")
    Call<User> getUserById(@Path("id") Object id);

    @POST(USERS)
    Call<User> createUser(@Body User trainee);

    @PUT(USERS + "/{id}")
    Call<User> updateUser(@Path("id") Object id, @Body User trainee);

    @DELETE(USERS + "/{id}")
    Call<User> deleteUser(@Path("id") Object id);

    @GET(USERS)
    Call<User[]> getUserByEmail(@Query("email") String email);
}

