package com.example.menaccessoriesshop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserOrderAdapter extends ArrayAdapter<Order> {
    private Context context;
    private List<Order> userOrderList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    public UserOrderAdapter(@NonNull Context context, @NonNull List<Order> userOrders) {
        super(context, R.layout.item_order, userOrders);
        this.context = context;
        this.userOrderList = userOrders;
        sortOrdersByDate();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        }

        Order userOrder = getItem(position);
        if (userOrder != null) {
            TextView txtOrderId = convertView.findViewById(R.id.txtOrderId);
            TextView txtTotalPrice = convertView.findViewById(R.id.txtTotalPrice);
            TextView txtStatus = convertView.findViewById(R.id.txtStatus);
            TextView txtCreatedAt = convertView.findViewById(R.id.txtCreatedAt);

            txtOrderId.setText("Mã ĐH: " + userOrder.getId());
            txtTotalPrice.setText("Tổng tiền: " + userOrder.getFinalPrice() + "đ");
            txtStatus.setText("Trạng thái: " + userOrder.getStatus());
            txtCreatedAt.setText("Ngày tạo: " + userOrder.getCreatedAt());
        }
        return convertView;
    }

    private void sortOrdersByDate() {
        Collections.sort(userOrderList, (o1, o2) -> {
            try {
                Date date1 = dateFormat.parse(o1.getCreatedAt());
                Date date2 = dateFormat.parse(o2.getCreatedAt());
                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }
}
