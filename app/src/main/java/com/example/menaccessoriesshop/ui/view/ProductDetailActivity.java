package com.example.menaccessoriesshop.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.Cart;
import com.example.menaccessoriesshop.data.model.CartItem;
import com.example.menaccessoriesshop.data.model.Product;
import com.example.menaccessoriesshop.data.repository.CartRepository;
import com.example.menaccessoriesshop.data.repository.CartService;
import com.example.menaccessoriesshop.data.repository.ProductRepository;
import com.example.menaccessoriesshop.data.repository.ProductService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView imgProduct;
    private TextView txtProductName, txtProductPrice, txtProductDescription, txtProductStock, txtSalesQuantity, txtQuantity;
    private Button btnIncrease, btnDecrease, btnAddToCart, btnBack;
    private ProductService productService;
    private CartService cartService;
    private Product currentProduct;
    private int currentQuantity = 1; // Số lượng sản phẩm khi thêm vào giỏ

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtProductDescription = findViewById(R.id.txtProductDescription);
        txtProductStock = findViewById(R.id.txtProductStock);
        txtSalesQuantity = findViewById(R.id.txtSalesQuantity);
        txtQuantity = findViewById(R.id.txtQuantity);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBack = findViewById(R.id.btnBack);

        productService = ProductRepository.getProductService();
        cartService = CartRepository.getCartService();

        String productId = getIntent().getStringExtra("PRODUCT_ID");
        if (productId != null) {
            fetchProductDetails(productId);
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnIncrease.setOnClickListener(v -> {
            if (currentProduct != null && currentQuantity < currentProduct.getProductStock()) {
                currentQuantity++;
                txtQuantity.setText(String.valueOf(currentQuantity));
            }
        });

        btnDecrease.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                txtQuantity.setText(String.valueOf(currentQuantity));
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null) {
                addToCart();
            }
        });
    }

    private void fetchProductDetails(String productId) {
        productService.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentProduct = response.body();
                    txtProductName.setText(currentProduct.getProductName());
                    txtProductPrice.setText("Giá sản phẩm: " + currentProduct.getProductPrice() + "đ");
                    txtProductDescription.setText("Mô tả: " + currentProduct.getProductDescription());
                    txtProductStock.setText("Số lượng tồn kho: " + currentProduct.getProductStock());
                    txtSalesQuantity.setText("Số lượng đã bán: " + currentProduct.getSalesQuantity());
                    txtQuantity.setText(String.valueOf(currentQuantity));

                    Glide.with(ProductDetailActivity.this)
                            .load(currentProduct.getProductImage())
                            .placeholder(R.drawable.loading)
                            .into(imgProduct);

                    // Kiểm tra tồn kho và cập nhật trạng thái của nút "Thêm vào giỏ hàng"
                    int stock = currentProduct.getProductStock();
                    if (stock <= 0) {
                        btnAddToCart.setEnabled(false);
                        btnAddToCart.setText("Hết hàng");
                    } else {
                        btnAddToCart.setEnabled(true);
                        btnAddToCart.setText("Thêm vào giỏ hàng");
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi tải dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("PRODUCT_DETAIL", "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userID", null);
        if (userId == null) {
            Toast.makeText(this, "Người dùng chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        cartService.getCartByUser(userId).enqueue(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cart> carts = response.body();
                    if (carts.isEmpty()) {
                        createNewCart(userId);
                    } else {
                        updateCart(carts.get(0));
                    }
                } else {
                    createNewCart(userId);
                }
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                Log.e("CART", "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewCart(String userId) {
        List<CartItem> items = new ArrayList<>();
        items.add(new CartItem(
                currentProduct.getId(),
                currentProduct.getProductName(),
                currentProduct.getProductImage(),
                currentQuantity,
                currentProduct.getProductPrice(),
                false
        ));

        Cart newCart = new Cart(userId, items);
        cartService.createCart(newCart).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailActivity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi khi tạo giỏ hàng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e("CART", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void updateCart(Cart existingCart) {
        List<CartItem> items = existingCart.getItems();
        if (items == null) {
            items = new ArrayList<>();
        }

        boolean itemExists = false;
        for (CartItem item : items) {
            if (item.getProductId().equals(currentProduct.getId())) {
                item.setQuantity(item.getQuantity() + currentQuantity);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            items.add(new CartItem(currentProduct.getId(), currentProduct.getProductName(), currentProduct.getProductImage(), currentQuantity, currentProduct.getProductPrice(), false));
        }

        existingCart.setItems(items);
        cartService.updateCart(existingCart.getId(), existingCart).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                Toast.makeText(ProductDetailActivity.this, "Cập nhật giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.e("CART", "Lỗi: " + t.getMessage());
            }
        });
    }
}
