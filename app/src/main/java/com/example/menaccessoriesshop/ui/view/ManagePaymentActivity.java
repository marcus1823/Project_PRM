package com.example.menaccessoriesshop.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.adapter.PaymentAdapter;
import com.example.menaccessoriesshop.data.model.Payment;
import com.example.menaccessoriesshop.data.repository.PaymentRepository;
import com.example.menaccessoriesshop.data.repository.PaymentService;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagePaymentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private PaymentService paymentService;
    private RecyclerView recyclerView;
    private PaymentAdapter adapter;
    private TextView tvTotalRevenue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quản lý thanh toán");
        paymentService = PaymentRepository.getPaymentService();
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        recyclerView = findViewById(R.id.recycler_view_payment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        paymentService = PaymentRepository.getPaymentService();
        loadPayments();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_manage_product) {
            startActivity(new Intent(this, ManageProductActivity.class));
        } else if (id == R.id.nav_manage_store) {
            startActivity(new Intent(this, ManageStoreActivity.class));
        } else if (id == R.id.nav_manage_order) {
            startActivity(new Intent(this, ManageOrderActivity.class));
        } else if (id == R.id.nav_manage_payment) {
            startActivity(new Intent(this, ManagePaymentActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadPayments() {
        paymentService.getAllPayment().enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Payment> paymentList = response.body();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    Collections.sort(paymentList, (p1, p2) -> {
                        try {
                            return sdf.parse(p2.getTime()).compareTo(sdf.parse(p1.getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

                    int totalRevenue = 0;
                    for (Payment payment : paymentList) {
                        if ("Thành công".equals(payment.getStatus())) {
                            totalRevenue += payment.getAmount();
                        }
                    }

                    tvTotalRevenue.setText("Tổng doanh thu: " + totalRevenue + "₫");

                    adapter = new PaymentAdapter(paymentList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ManagePaymentActivity.this, "Failed to load payments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {
                Toast.makeText(ManagePaymentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
