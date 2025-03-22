package com.example.menaccessoriesshop.ui.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.adapter.CartItemAdapter;
import com.example.menaccessoriesshop.data.model.CartItem;
import com.example.menaccessoriesshop.data.model.Order;
import com.example.menaccessoriesshop.data.model.User;
import com.example.menaccessoriesshop.data.repository.OrderRepository;
import com.example.menaccessoriesshop.data.repository.OrderService;
import com.example.menaccessoriesshop.data.repository.UserRepository;
import com.example.menaccessoriesshop.data.repository.UserService;
import com.example.menaccessoriesshop.ui.viewmodel.OrderViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private ListView listViewCartItems;
    private CartItemAdapter cartItemAdapter;
    private OrderViewModel orderViewModel;
    private User user;
    private Spinner spinnerOrderStatus;
    private Order order;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order_detail);

        // Khởi tạo các view
        spinnerOrderStatus = findViewById(R.id.spinnerOrderStatus);
        listViewCartItems = findViewById(R.id.listViewCartItems);
        btnBack = findViewById(R.id.buttonBack);
        Button btnUpdateStatus = findViewById(R.id.buttonUpdateStatus);

        // Thiết lập ViewModel
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Thiết lập Spinner cho trạng thái đơn hàng
        String[] orderStatuses = {"Giao hàng thành công", "Giao hàng thất bại"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, orderStatuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderStatus.setAdapter(adapter);

        // Lấy orderId từ Intent
        String orderId = getIntent().getStringExtra("order_id");

        if (orderId != null) {
            // Lấy order từ ViewModel
            orderViewModel.getOrderById(orderId).observe(this, new Observer<Order>() {
                @Override
                public void onChanged(Order order) {
                    if (order != null) {
                        OrderDetailActivity.this.order = order; // Lưu order vào biến instance
                        List<CartItem> cartItems = order.getItems();
                        cartItemAdapter = new CartItemAdapter(OrderDetailActivity.this, cartItems);
                        listViewCartItems.setAdapter(cartItemAdapter);

                        // Sau khi có order, lấy thông tin user
                        String userId = order.getUserID();
                        if (userId != null) {
                            UserService userService = UserRepository.getUserService();
                            userService.getUserById(userId).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        User user = response.body();
                                        TextView userName = findViewById(R.id.user_name);
                                        userName.setText("Người Mua: " + user.getFullName());
                                        TextView userPhone = findViewById(R.id.user_phone);
                                        userPhone.setText("SDT: " + user.getPhone());
                                        TextView userAddress = findViewById(R.id.txtAddress);
                                        userAddress.setText("Địa chỉ khách hàng: " + order.getAddress());
                                    } else {
                                        Toast.makeText(OrderDetailActivity.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối khi tải người dùng", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "Không thể tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Xử lý sự kiện nhấn nút Update Status
        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order != null) {
                    String selectedStatus = spinnerOrderStatus.getSelectedItem().toString();
                    order.setStatus(selectedStatus);

                    // Gọi phương thức cập nhật trạng thái
                    OrderService orderService = OrderRepository.getOrderService();
                    orderService.updateOrder(orderId, order).enqueue(new Callback<Order>() {
                        @Override
                        public void onResponse(Call<Order> call, Response<Order> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(OrderDetailActivity.this, "Trạng thái đơn hàng đã được cập nhật: " + selectedStatus, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OrderDetailActivity.this, "Cập nhật trạng thái không thành công", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Order> call, Throwable t) {
                            Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Đơn hàng không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý sự kiện nhấn nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
