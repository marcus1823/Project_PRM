package com.example.menaccessoriesshop.data.repository;
import com.example.menaccessoriesshop.data.model.Payment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface PaymentService {
    String PAYMENTS = "Payment";
    @GET(PAYMENTS)
    Call<List<Payment>> getAllPayment();
    @GET(PAYMENTS + "/{id}")
    Call<Payment> getPaymentById(@Path("id") String paymentId);

    @POST(PAYMENTS)
    Call<Payment> createPayment(@Body Payment payment);

    @PUT(PAYMENTS + "/{id}")
    Call<Payment> updatePayment(@Path("id") String paymentId, @Body Payment payment);

    @DELETE(PAYMENTS + "/{id}")
    Call<Void> deletePayment(@Path("id") String paymentId);

    @GET(PAYMENTS)
    Call<List<Payment>> getPaymentByUserId(@Query("userId") String userId);
}
