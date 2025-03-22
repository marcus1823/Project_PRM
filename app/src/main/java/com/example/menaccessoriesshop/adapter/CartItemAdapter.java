package com.example.menaccessoriesshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.CartItem;
import com.bumptech.glide.Glide;

import java.util.List;

public class CartItemAdapter extends BaseAdapter {
    private Context context;
    private List<CartItem> cartItems;

    public CartItemAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart_item, parent, false);
        }

        CartItem cartItem = cartItems.get(position);

        TextView productName = convertView.findViewById(R.id.product_name);
        TextView productPrice = convertView.findViewById(R.id.product_price);
        TextView productQuantity = convertView.findViewById(R.id.product_quantity);
        ImageView productImage = convertView.findViewById(R.id.product_image);

        productName.setText(cartItem.getProductName());
        productPrice.setText(String.format("$%.2f", cartItem.getProductPrice()));
        productQuantity.setText("Quantity: " + cartItem.getQuantity());

        // Sử dụng Glide để tải hình ảnh
        Glide.with(context)
                .load(cartItem.getProductImage())
                .placeholder(R.drawable.loading) // Thay thế bằng hình ảnh thay thế nếu cần
                .error(R.drawable.fail) // Thay thế bằng hình ảnh lỗi nếu cần
                .into(productImage);

        return convertView;
    }
}
