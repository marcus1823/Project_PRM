package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.data.model.Product;
import com.example.menaccessoriesshop.data.model.Store;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
public interface StoreService {

    String STORES = "Store";

    @GET(STORES)
    Call<List<Store>> getAllStore();

    @GET(STORES + "/{id}")
    Call<Store> getStoreById(@Path("id") String id);

    @POST(STORES)
    Call<Store> createStore(@Body Store store);

    @PUT(STORES + "/{id}")
    Call<Store> updateStore(@Path("id") String id, @Body Store store);

    @DELETE(STORES + "{id}")
    Call<Store> deleteStore(@Path("id") String id);
}
