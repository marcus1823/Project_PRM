package com.example.menaccessoriesshop.data.repository;

import com.example.menaccessoriesshop.data.model.Product;
import com.example.menaccessoriesshop.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {
    String PRODUCTS = "Product";

    @GET(PRODUCTS)
    Call<List<Product>> getAllProduct();

    @GET(PRODUCTS + "/{id}")
    Call<Product> getProductById(@Path("id") String id);

    @POST(PRODUCTS)
    Call<Product> createProduct(@Body Product product);

    @PUT(PRODUCTS + "/{id}")
    Call<Product> updateProduct(@Path("id") String id, @Body Product product);

    @DELETE(PRODUCTS + "/{id}")
    Call<Product> deleteProduct(@Path("id") String id);

    @GET(PRODUCTS)
    Call<List<Product>> getProductsByName(@Query("productName") String productName);
    @GET(PRODUCTS)
    Call<List<Product>> getTopSellingProducts(@Query("sortBy") String sortBy, @Query("order") String order);

    // lấy những sản phẩm có salesQuantity lớn nhất xuống thấp
}

