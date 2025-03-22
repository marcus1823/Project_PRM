package com.example.menaccessoriesshop.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.adapter.CartAdapter;
import com.example.menaccessoriesshop.data.model.Cart;
import com.example.menaccessoriesshop.data.model.CartItem;
import com.example.menaccessoriesshop.data.repository.CartRepository;
import com.example.menaccessoriesshop.data.repository.CartService;
import com.example.menaccessoriesshop.ui.viewmodel.CartViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private CartViewModel mViewModel;
    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private TextView tvTotalItems;
    private CartService cartService;
    private List<CartItem> cartItemList = new ArrayList<>();
    private Button btnDelete;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        rvCartItems = view.findViewById(R.id.rvCartItems);
        tvTotalItems = view.findViewById(R.id.tvTotalItems);

        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));

        cartService = CartRepository.getCartService();
        btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> deleteSelectedItems());
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", null);

        if (userID != null) {
            fetchCartItems(userID);
        } else {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
        Button btnConfirmSelection = view.findViewById(R.id.btnConfirmSelection);
        btnConfirmSelection.setOnClickListener(v -> {
            ArrayList<CartItem> selectedItems = new ArrayList<>();

            for (CartItem item : cartItemList) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }

            if (selectedItems.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getContext(), PaymentActivity.class);
            intent.putExtra("selectedItems", selectedItems);
            startActivity(intent);
        });
        return view;
    }

    private void fetchCartItems(String userID) {
        Call<List<Cart>> call = cartService.getCartByUser(userID);
        call.enqueue(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cart> carts = response.body();
                    if (!carts.isEmpty()) {
                        cartItemList = carts.get(0).getItems();
                        Log.d("CartData", "Cart items loaded: " + cartItemList.size());
                        updateCartUI();
                        Cart cart = carts.get(0);
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("cartID", cart.getId()).apply();
                    } else {
                        Log.d("CartData", "No cart found for user: " + userID);
                    }
                } else {
                    Log.e("CartError", "API Response Code: " + response.code());
                }
            }


            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi tải giỏ hàng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateCartUI() {
        cartAdapter = new CartAdapter(cartItemList, this::updateTotalItems);
        rvCartItems.setAdapter(cartAdapter);
        updateTotalItems();
    }

    private void updateTotalItems() {
        int totalItems = cartItemList.size();
        tvTotalItems.setText("Tổng số sản phẩm: " + totalItems);
    }

    private void deleteSelectedItems() {
        List<CartItem> itemsToDelete = new ArrayList<>();

        for (CartItem item : cartItemList) {
            if (item.isSelected()) {
                itemsToDelete.add(item);
            }
        }

        if (itemsToDelete.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn sản phẩm để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        cartItemList.removeAll(itemsToDelete);
        updateCartOnServer(cartItemList);
        cartAdapter.notifyDataSetChanged();
        updateTotalItems();
    }

    private void updateCartOnServer(List<CartItem> updatedCartItems) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String cartID = sharedPreferences.getString("cartID", null);

        if (cartID == null) {
            Toast.makeText(getContext(), "Không tìm thấy Cart ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Cart updatedCart = new Cart(cartID, updatedCartItems);

        Call<Cart> call = cartService.updateCart(cartID, updatedCart);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã cập nhật giỏ hàng!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi cập nhật giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("TAG delete cart", "onFailure:  " + t.getMessage());
            }
        });
    }



}
