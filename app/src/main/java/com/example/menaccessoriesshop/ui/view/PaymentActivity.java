package com.example.menaccessoriesshop.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.VNPay;
import com.example.menaccessoriesshop.adapter.PurchaseAdapter;
import com.example.menaccessoriesshop.data.model.CartItem;
import com.example.menaccessoriesshop.data.model.Order;
import com.example.menaccessoriesshop.data.model.Payment;
import com.example.menaccessoriesshop.data.model.User;
import com.example.menaccessoriesshop.data.repository.OrderRepository;
import com.example.menaccessoriesshop.data.repository.OrderService;
import com.example.menaccessoriesshop.data.repository.PaymentRepository;
import com.example.menaccessoriesshop.data.repository.PaymentService;
import com.example.menaccessoriesshop.data.repository.UserRepository;
import com.example.menaccessoriesshop.data.repository.UserService;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView rvOrderItems;
    private TextView txtTotalPrice, tvFullInfo, tvAddress;
    private List<CartItem> cartItemList;
    private PurchaseAdapter purchaseAdapter;
    private UserService userService;
    private OrderService orderService;
    private PaymentService paymentService;
    private Button btnThanhToan;
    private int totalPrice = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        AnhXa();
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        getUserInfo(userID);

        cartItemList = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedItems");
        if (cartItemList == null) {
            cartItemList = new ArrayList<>();
        }
        for (CartItem item : cartItemList) {
            totalPrice += item.getProductPrice() * item.getQuantity();
        }
        totalPrice += 20000;
        txtTotalPrice.setText("Tổng tiền: " + totalPrice + " đ");

        purchaseAdapter = new PurchaseAdapter(this, cartItemList);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(purchaseAdapter);

        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputAmount = totalPrice;

                try {

                    if (inputAmount <= 0) {
                        Toast.makeText(PaymentActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    } else {
                        createOrder(inputAmount, 0 , inputAmount);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(PaymentActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void AnhXa(){
        tvFullInfo = findViewById(R.id.tvFullInfo);
        tvAddress = findViewById(R.id.tvAddress);
        rvOrderItems = findViewById(R.id.rvOrderItems);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        userService = UserRepository.getUserService();
        orderService = OrderRepository.getOrderService();
        paymentService = PaymentRepository.getPaymentService();
        btnThanhToan = findViewById(R.id.btnConfirmPayment);
    }
    private void getUserInfo(String userID) {
        Call<User> call = userService.getUserById(userID);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvFullInfo.setText(user.getFullName() + " | " + user.getPhone());
                    tvAddress.setText(user.getAddress());
                }
                else{
                    Toast.makeText(PaymentActivity.this, "Fail to get INFO", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Fail to get INFO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        if (uri != null) {
            String responseCode = uri.getQueryParameter("vnp_ResponseCode");
            Intent intent;
            SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String orderID = sharedPreferences.getString("orderID", null);
            String orderDate = sharedPreferences.getString("orderDate", null);
            int finalPrice = sharedPreferences.getInt("orderPrice", 0);
            String userID = sharedPreferences.getString("userID", null);
            if ("00".equals(responseCode)) {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                updateOrderStatus(orderID, "Thanh toán thành công!");
                Payment payment = new Payment(userID, orderID, finalPrice, "Thành công", orderDate);
                createPayment(payment);
                intent = new Intent(this, PaymentSuccessActivity.class);
            } else {
                Toast.makeText(this, "Thanh toán thất bại!", Toast.LENGTH_LONG).show();
                updateOrderStatus(orderID, "Thanh toán thất bại!");
                Payment payment = new Payment(userID, orderID, finalPrice, "Thất bại", orderDate);
                createPayment(payment);
                intent = new Intent(this, PaymentFailActivity.class);
            }
            intent.putExtra("orderID", orderID);
            intent.putExtra("finalPrice", finalPrice);
            intent.putExtra("paymentDate", orderDate);
            startActivity(intent);
            finish();
        }
    }

    protected void CheckOut(int totalCost) {
        int paymentAmount = totalCost;
        new AlertDialog.Builder(this)
                .setTitle("Confirm Checkout?")
                .setMessage("Payment amount: " + paymentAmount + " VNĐ")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(this, "Processing payment...", Toast.LENGTH_SHORT).show();

                    String orderId = String.valueOf(System.currentTimeMillis());
                    String paymentUrl;

                    try {
                        paymentUrl = VNPay.getPaymentUrl(orderId, paymentAmount);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void createPayment(Payment payment){
        Call<Payment> call = paymentService.createPayment(payment);
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if(response.isSuccessful() && response.body() != null){
                    Toast.makeText(PaymentActivity.this, "Tạo payment thành công!", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(PaymentActivity.this, "Lỗi khi tạo payment!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createOrder(int totalPrice, int discount, int finalPrice){
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        Order newOrder = new Order(userID, cartItemList, totalPrice, 0 , finalPrice, "Đang chờ thanh toán",  formattedDate, tvAddress.getText().toString());

        Call<Order> call = orderService.createOrder(newOrder);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if(response.isSuccessful() && response.body() != null){
                    String orderID = response.body().getId();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("orderID", orderID);
                    editor.putInt("orderPrice", finalPrice);
                    editor.putString("orderDate", newOrder.getCreatedAt());
                    editor.apply();
                    CheckOut(finalPrice);
                }else
                    Toast.makeText(PaymentActivity.this, "Lỗi khi tạo đơn hàng!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateOrderStatus(String orderID, String status) {
        if (orderID == null) {
            Toast.makeText(this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        orderService.getOrderById(orderID).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Order order = response.body();
                    order.setStatus(status);

                    orderService.updateOrder(orderID, order).enqueue(new Callback<Order>() {
                        @Override
                        public void onResponse(Call<Order> call, Response<Order> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(PaymentActivity.this, "Cập nhật trạng thái thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PaymentActivity.this, "Lỗi khi cập nhật đơn hàng!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Order> call, Throwable t) {
                            Toast.makeText(PaymentActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(PaymentActivity.this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
//  9704198526191432198
//	NGUYEN VAN A
//  07/15

