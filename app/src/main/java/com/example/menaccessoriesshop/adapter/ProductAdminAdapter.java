package com.example.menaccessoriesshop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private List<Product> originalList; // Danh sách gốc để lọc
    private OnItemClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdminAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.originalList = new ArrayList<>(productList); // Lưu danh sách gốc
    }
    public void updateList(List<Product> newList) {
        productList.clear(); // Xóa danh sách cũ
        productList.addAll(newList); // Thêm danh sách mới
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
        this.originalList = new ArrayList<>(productList); // Cập nhật danh sách gốc khi thay đổi danh sách
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_admin, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.txtProductName.setText(product.getProductName());
        holder.txtProductPrice.setText(product.getProductPrice() + " VNĐ");

        Glide.with(context)
                .load(product.getProductImage())
                .placeholder(R.drawable.ic_add)
                .error(R.drawable.loading)
                .into(holder.imgProduct);

        if (selectedPosition == holder.getAdapterPosition()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.selected_item));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();

            if (listener != null) {
                listener.onItemClick(product);
            } else {
                Toast.makeText(context, "Không có hành động được thiết lập", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Phương thức lọc sản phẩm theo tên
    // Phương thức lọc sản phẩm theo tên
    public void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            // Khi ô search trống, hiển thị lại toàn bộ danh sách gốc
            filteredList.addAll(originalList);
        } else {
            for (Product product : originalList) {
                if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }

        // Cập nhật danh sách hiển thị
        productList.clear();
        productList.addAll(filteredList);
        notifyDataSetChanged();
    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
        }
    }
}
