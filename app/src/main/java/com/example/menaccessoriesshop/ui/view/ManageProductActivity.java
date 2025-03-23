package com.example.menaccessoriesshop.ui.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.adapter.ProductAdminAdapter;
import com.example.menaccessoriesshop.data.model.Product;
import com.example.menaccessoriesshop.data.repository.ProductService;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerViewProducts;
    private ProductAdminAdapter adapter;
    private ProductService productService;
    private Button btnActions;
    private EditText edtSearch;
    private Product selectedProduct;
    private List<Product> productList = new ArrayList<>(); // Danh sách sản phẩm gốc
    private List<Product> originalList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quản lý sản phẩm");
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        btnActions = findViewById(R.id.btnActions);
        edtSearch = findViewById(R.id.edtSearch);

        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://67df9223a76352338f9aaee49.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        productService = retrofit.create(ProductService.class);
        loadProducts();

        btnActions.setOnClickListener(v -> showPopupMenu(v));
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.product_actions_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_add) {
                showAddProductDialog();
            } else if (itemId == R.id.action_update) {
                showUpdateProductDialog();
            } else if (itemId == R.id.action_delete) {
                deleteSelectedProduct();
            }
            return true;
        });

        popupMenu.show();
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

    // ------------------- HÀM LOAD DỮ LIỆU -------------------
    private void loadProducts() {
        productService.getAllProduct().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    originalList.clear();
                    originalList.addAll(response.body()); // Lưu danh sách gốc

                    productList.clear();
                    productList.addAll(originalList); // Hiển thị danh sách ban đầu

                    adapter = new ProductAdminAdapter(ManageProductActivity.this, productList);
                    adapter.setOnItemClickListener(product -> selectedProduct = product);
                    recyclerViewProducts.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ManageProductActivity.this, "Lỗi tải danh sách sản phẩm!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ------------------- HÀM TÌM KIẾM SẢN PHẨM -------------------
    private void filterProducts(String keyword) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.updateList(filteredList); // Cập nhật danh sách trong Adapter
    }

    // ------------------- HÀM THÊM SẢN PHẨM -------------------
    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Sản Phẩm Mới");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        EditText edtName = view.findViewById(R.id.edtProductName);
        EditText edtPrice = view.findViewById(R.id.edtProductPrice);
        EditText edtImageUrl = view.findViewById(R.id.edtProductImageUrl);

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String imageUrl = edtImageUrl.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            Product newProduct = new Product(null, name, price, "", imageUrl, 0, "", true, 0);
            productService.createProduct(newProduct).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ManageProductActivity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Toast.makeText(ManageProductActivity.this, "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // ------------------- HÀM CẬP NHẬT SẢN PHẨM -------------------
    private void showUpdateProductDialog() {
        if (selectedProduct == null) {
            Toast.makeText(this, "Vui lòng chọn sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cập Nhật Sản Phẩm");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        EditText edtName = view.findViewById(R.id.edtProductName);
        EditText edtPrice = view.findViewById(R.id.edtProductPrice);
        EditText edtImageUrl = view.findViewById(R.id.edtProductImageUrl);

        edtName.setText(selectedProduct.getProductName());
        edtPrice.setText(String.valueOf(selectedProduct.getProductPrice()));
        edtImageUrl.setText(selectedProduct.getProductImage());

        builder.setView(view);
        builder.setPositiveButton("Cập Nhật", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String imageUrl = edtImageUrl.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedProduct.setProductName(name);
            selectedProduct.setProductPrice(price);
            selectedProduct.setProductImage(imageUrl);

            productService.updateProduct(selectedProduct.getId(), selectedProduct).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ManageProductActivity.this, "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Toast.makeText(ManageProductActivity.this, "Lỗi khi cập nhật sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // ------------------- HÀM XÓA SẢN PHẨM -------------------
    private void deleteSelectedProduct() {
        if (selectedProduct == null) {
            Toast.makeText(this, "Vui lòng chọn sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }

        productService.deleteProduct(selectedProduct.getId()).enqueue(new Callback<Product>() { // Dùng Callback<Product>
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageProductActivity.this, "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    loadProducts();
                } else {
                    Toast.makeText(ManageProductActivity.this, "Lỗi khi xóa! Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ManageProductActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

}
