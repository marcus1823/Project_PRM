package com.example.menaccessoriesshop.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menaccessoriesshop.R;

public class PaymentSuccessActivity extends AppCompatActivity {
    private TextView tvPaymentDate, tvOrderId, tvAmount;
    private Button btnMyOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        tvPaymentDate = findViewById(R.id.tvPaymentDate1);
        tvOrderId = findViewById(R.id.tvOrderId1);
        tvAmount = findViewById(R.id.tvAmount1);
        btnMyOrders = findViewById(R.id.btnMyOrders);

        Intent intent = getIntent();
        String orderID = intent.getStringExtra("orderID");
        int finalPrice = intent.getIntExtra("finalPrice", 0);
        String paymentDate = intent.getStringExtra("paymentDate");

        tvOrderId.setText("Mã đơn hàng: #" + orderID);
        tvPaymentDate.setText("Ngày thanh toán: " + paymentDate);
        tvAmount.setText("Số tiền: " + finalPrice + " VNĐ");

        btnMyOrders.setOnClickListener(v -> {
            Intent intentHome = new Intent(PaymentSuccessActivity.this, MainActivity.class);
            startActivity(intentHome);
            finish();
        });

    }
}
