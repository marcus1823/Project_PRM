package com.example.menaccessoriesshop.ui.view;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.adapter.ProductAdapter;
import com.example.menaccessoriesshop.data.model.Product;
import com.example.menaccessoriesshop.data.repository.ProductRepository;
import com.example.menaccessoriesshop.data.repository.ProductService;
import com.example.menaccessoriesshop.data.repository.StoreRepository;
import com.example.menaccessoriesshop.ui.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {
    private CategoryViewModel mViewModel;
    private RecyclerView recyclerProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private ProductService productService;
    private EditText edtSearch;
    private ImageButton btnSearch;
    private Button btnSortName, btnSortPrice;

    private int sortStateName = 0;  // 0: Không sắp xếp, 1: A-Z, 2: Z-A
    private int sortStatePrice = 0; // 0: Không sắp xếp, 1: Cao->Thấp, 2: Thấp->Cao

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        AnhXa(view);
        fetchAllProducts();
        return view;
    }

    private void AnhXa(View view) {
        recyclerProducts = view.findViewById(R.id.recyclerProducts);
        recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        //productAdapter = new ProductAdapter(productList);
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerProducts.setAdapter(productAdapter);
        productService = ProductRepository.getProductService();

        edtSearch = view.findViewById(R.id.edtSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSortName = view.findViewById(R.id.btnSortName);
        btnSortPrice = view.findViewById(R.id.btnSortPrice);

        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            if (!keyword.isEmpty())
                fetchProducts(keyword);
            else
                fetchAllProducts();
        });

        btnSortName.setOnClickListener(v -> sortByName());
        btnSortPrice.setOnClickListener(v -> sortByPrice());
    }

    private void fetchProducts(String findProduct) {
        productService.getProductsByName(findProduct).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                } else {
                    Log.e("PRODUCT", "Lỗi lấy danh sách sản phẩm theo tên");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("PRODUCT", "Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    private void fetchAllProducts() {
        productService.getAllProduct().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    applySorting(); // Áp dụng sắp xếp sau khi lấy dữ liệu
                } else {
                    Log.e("PRODUCT", "Lỗi lấy danh sách sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("PRODUCT", "Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    private void sortByName() {
        sortStateName = (sortStateName + 1) % 3; // Xoay vòng giữa 0, 1, 2
        sortStatePrice = 0; // Reset sắp xếp giá

        applySorting();
    }

    private void sortByPrice() {
        sortStatePrice = (sortStatePrice + 1) % 3; // Xoay vòng giữa 0, 1, 2
        sortStateName = 0; // Reset sắp xếp tên

        applySorting();
    }

    private void applySorting() {
        if (sortStateName == 1) {
            Collections.sort(productList, Comparator.comparing(Product::getProductName));
            btnSortName.setText("S/x tên Z-A");
        } else if (sortStateName == 2) {
            Collections.sort(productList, (p1, p2) -> p2.getProductName().compareTo(p1.getProductName()));
            btnSortName.setText("S/x tên A-Z");
        } else {
            btnSortName.setText("S/x tên");
        }

        if (sortStatePrice == 1) {
            Collections.sort(productList, (p1, p2) -> Double.compare(p2.getProductPrice(), p1.getProductPrice()));
            btnSortPrice.setText("S/x giá ↓");
        } else if (sortStatePrice == 2) {
            Collections.sort(productList, (p1, p2) -> Double.compare(p1.getProductPrice(), p2.getProductPrice()));
            btnSortPrice.setText("S/x giá ↑");
        } else {
            btnSortPrice.setText("S/x giá");
        }

        productAdapter.notifyDataSetChanged();
    }
}