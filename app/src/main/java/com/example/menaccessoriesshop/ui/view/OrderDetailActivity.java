package com.example.menaccessoriesshop.ui.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.adapter.PurchaseAdapter;
import com.example.menaccessoriesshop.data.model.CartItem;
import com.example.menaccessoriesshop.data.model.Order;
import com.example.menaccessoriesshop.data.repository.OrderRepository;
import com.example.menaccessoriesshop.data.repository.OrderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCartItems;
    private TextView user_name, user_phone, txtAddress;
    private Button buttonBack;

    private PurchaseAdapter purchaseAdapter;
    private OrderService orderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order_detail);

        recyclerViewCartItems = findViewById(R.id.recyclerViewCartItems);
        user_name = findViewById(R.id.user_name);
        user_phone = findViewById(R.id.user_phone);
        txtAddress = findViewById(R.id.txtAddress);
        buttonBack = findViewById(R.id.buttonBack);

        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));
        orderService = OrderRepository.getOrderService();

        String orderId = getIntent().getStringExtra("order_id");
        if (orderId != null) {
            fetchOrderDetail(orderId);
        } else {
            Toast.makeText(this, "Không có order ID", Toast.LENGTH_SHORT).show();
        }


        buttonBack.setOnClickListener(v -> finish());
    }

    private void fetchOrderDetail(String orderId) {
        orderService.getOrderById(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body();
                    List<CartItem> cartItems = order.getItems();

                    purchaseAdapter = new PurchaseAdapter(OrderDetailActivity.this, cartItems);
                    recyclerViewCartItems.setAdapter(purchaseAdapter);

                    txtAddress.setText("Địa chỉ: " + order.getAddress());
                    user_name.setText("Người đặt: User " + order.getUserID());
                    user_phone.setText("SĐT: ___");
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}