package com.example.menaccessoriesshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private OnItemCheckedListener listener;

    public interface OnItemCheckedListener {
        void onItemCheckedChange();
    }

    public CartAdapter(List<CartItem> cartItems, OnItemCheckedListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.txtProductName.setText(item.getProductName());
        holder.txtProductPrice.setText("Giá: " + item.getProductPrice() + "đ");
        holder.txtQuantity.setText("Số lượng: " + item.getQuantity());
        holder.cbSelectItem.setChecked(item.isSelected());

        holder.cbSelectItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (listener != null) {
                listener.onItemCheckedChange();
            }
        });

        Glide.with(holder.itemView.getContext())
                .load(item.getProductImage())
                .placeholder(R.drawable.loading)
                .error(R.drawable.error_image)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice, txtQuantity;
        CheckBox cbSelectItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);
        }
    }
}
