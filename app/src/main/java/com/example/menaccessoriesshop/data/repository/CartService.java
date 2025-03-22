package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.data.model.Cart;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface CartService {
    String CARTS = "Cart";
    @GET(CARTS)
    Call<List<Cart>> getAllCarts();

    @GET(CARTS)
    Call<List<Cart>> getCartByUser(@Query("userID") String userID);

    @POST(CARTS)
    Call<Cart> createCart(@Body Cart cart);

    @PUT(CARTS + "/{id}")
    Call<Cart> updateCart(@Path("id") String cartId, @Body Cart cart);

    @DELETE(CARTS + "/{id}")
    Call<Void> deleteCart(@Path("id") String cartId);
}

