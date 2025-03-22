package com.example.menaccessoriesshop.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.menaccessoriesshop.data.model.Order;
import com.example.menaccessoriesshop.data.repository.OrderRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends ViewModel {
    private MutableLiveData<List<Order>> ordersList;
    private OrderRepository orderRepository;

    public OrderViewModel() {
        orderRepository = new OrderRepository();
    }

    public LiveData<List<Order>>  getOrders() {
        if (ordersList == null) {
            ordersList = new MutableLiveData<>();
            fetchOrders();
        }
        return ordersList;
    }

    private void fetchOrders() {
        orderRepository.getOrderService().getAllOrder().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ordersList.setValue(response.body());
                } else {
                    ordersList.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                ordersList.setValue(null);
            }
        });
    }
    public LiveData<Order> getOrderById(String orderId) {
        MutableLiveData<Order> orderData = new MutableLiveData<>();
        orderRepository.getOrderService().getOrderById(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderData.setValue(response.body());
                } else {
                    orderData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                orderData.setValue(null);
            }
        });
        return orderData;
    }

}
