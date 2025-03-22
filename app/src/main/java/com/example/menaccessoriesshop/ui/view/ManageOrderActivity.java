package com.example.menaccessoriesshop.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.adapter.OrderAdapter;
import com.example.menaccessoriesshop.data.model.Order;
import com.example.menaccessoriesshop.data.repository.OrderRepository;
import com.example.menaccessoriesshop.data.repository.OrderService;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageOrderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView listViewOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private OrderService orderService;

    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quản lý đơn hàng");
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        listViewOrders = findViewById(R.id.listViewOrders);
        orderService = OrderRepository.getOrderService();

        fetchOrders();

        listViewOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order selectedOrder = (Order) parent.getItemAtPosition(position);
                Intent intent = new Intent(ManageOrderActivity.this, OrderDetailActivity.class);
                intent.putExtra("order_id", selectedOrder.getId());
                startActivity(intent);
            }
        });
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    private void fetchOrders() {
        orderService.getAllOrder().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList = response.body();
                    orderAdapter = new OrderAdapter(ManageOrderActivity.this, orderList);
                    listViewOrders.setAdapter(orderAdapter);
                } else {
                    Toast.makeText(ManageOrderActivity.this, "Không thể tải danh sách đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(ManageOrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
