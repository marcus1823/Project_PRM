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

public class OrderAdapter extends ArrayAdapter<Order> {
    private Context context;
    private List<Order> orderList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());


    public OrderAdapter(@NonNull Context context, @NonNull List<Order> orders) {
        super(context, R.layout.item_order, orders);
        this.context = context;
        this.orderList = orders;
        sortPaymentsByDate();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        }

        Order order = getItem(position);
        if (order != null) {
            TextView txtOrderId = convertView.findViewById(R.id.txtOrderId);
            TextView txtTotalPrice = convertView.findViewById(R.id.txtTotalPrice);
            TextView txtStatus = convertView.findViewById(R.id.txtStatus);
            TextView txtCreatedAt = convertView.findViewById(R.id.txtCreatedAt);
            TextView txtDiscount = convertView.findViewById(R.id.txtDiscount);

            txtDiscount.setText("Giảm giá: " + order.getDiscount());
            txtOrderId.setText("Mã ĐH: " + order.getId());
            txtTotalPrice.setText("Tổng tiền: " + order.getFinalPrice() + "đ");
            txtCreatedAt.setText("Ngày tạo: " + order.getCreatedAt());
            txtStatus.setText("Trạng thái: " + order.getStatus());
        }
        return convertView;
    }
    private void sortPaymentsByDate() {
        Collections.sort(orderList, (p1, p2) -> {
            try {
                Date date1 = dateFormat.parse(p1.getCreatedAt());
                Date date2 = dateFormat.parse(p2.getCreatedAt());
                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

}
