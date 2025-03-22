package com.example.menaccessoriesshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.CartItem;
import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {
    private Context context;
    private List<CartItem> cartItemList;

    public PurchaseAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.txtProductName.setText(item.getProductName());
        holder.txtProductPrice.setText("Giá: " + item.getProductPrice() + " đ");
        holder.txtQuantity.setText("Số lượng: " + item.getQuantity());
        holder.txtFinalMoney.setText("Thành tiền: " + (item.getQuantity() * item.getProductPrice()) + " đ");

        Glide.with(context)
                .load(item.getProductImage())
                .placeholder(R.drawable.loading) // Ảnh mặc định nếu lỗi
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtProductName, txtProductPrice, txtQuantity, txtFinalMoney;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtFinalMoney = itemView.findViewById(R.id.txtFinalMoney);
        }
    }
}
