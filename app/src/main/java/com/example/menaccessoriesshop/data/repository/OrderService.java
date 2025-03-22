package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.data.model.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderService {
    String ORDERS = "Order";
    @GET(ORDERS)
    Call<List<Order>> getAllOrder();
    @GET(ORDERS + "/{id}")
    Call<Order> getOrderById(@Path("id") String orderId);

    @GET(ORDERS)
    Call<List<Order>> getOrderByUserID(@Query("userID") String userID);

    @POST(ORDERS)
    Call<Order> createOrder(@Body Order order);

    @PUT(ORDERS + "/{id}")
    Call<Order> updateOrder(@Path("id") String orderId, @Body Order order);

    @DELETE(ORDERS + "/{id}")
    Call<Void> deleteOrder(@Path("id") String orderId);

}
