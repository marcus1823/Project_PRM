package com.example.menaccessoriesshop.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.adapter.UserOrderAdapter;
import com.example.menaccessoriesshop.data.model.Order;
import com.example.menaccessoriesshop.data.repository.OrderRepository;
import com.example.menaccessoriesshop.data.repository.OrderService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserOrderActivity extends AppCompatActivity {
    private ListView listViewUserOrders;
    private UserOrderAdapter userOrderAdapter;
    private OrderService orderService;
    private List<Order> userOrderList;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order);

        listViewUserOrders = findViewById(R.id.listViewUserOrders);
        btnBack = findViewById(R.id.btnBack);
        orderService = OrderRepository.getOrderService();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", null);
        if (userId != null) {
            fetchUserOrders(userId);
        } else {
            Toast.makeText(this, "Không tìm thấy user ID", Toast.LENGTH_SHORT).show();
        }

        listViewUserOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order selectedOrder = (Order) parent.getItemAtPosition(position);
                Intent intent = new Intent(UserOrderActivity.this, UserOrderDetailActivity.class);
                intent.putExtra("order_id", selectedOrder.getId());
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchUserOrders(String userId) {
        orderService.getOrderByUserID(userId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userOrderList = response.body();
                    userOrderList.removeIf(order -> !order.getUserID().equals(userId));
                    userOrderAdapter = new UserOrderAdapter(UserOrderActivity.this, userOrderList);
                    listViewUserOrders.setAdapter(userOrderAdapter);
                } else {
                    Toast.makeText(UserOrderActivity.this, "Không thể tải danh sách đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(UserOrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
