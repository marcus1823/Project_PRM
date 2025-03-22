package com.example.menaccessoriesshop.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.menaccessoriesshop.R;
import com.example.menaccessoriesshop.data.model.Payment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private List<Payment> paymentList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    public PaymentAdapter(List<Payment> paymentList) {
        this.paymentList = paymentList;
        sortPaymentsByDate();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Payment payment = paymentList.get(position);
        holder.tvOrderId.setText("ID đơn hàng: " + payment.getOrderId());
        holder.tvAmount.setText("Tổng tiền: " + payment.getAmount());
        holder.tvDate.setText("Ngày: " + payment.getTime());

        holder.tvStatus.setText("Trạng thái: " + payment.getStatus());
        if (payment.getStatus().equalsIgnoreCase("Thành công")) {
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
        }
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvAmount, tvStatus, tvDate;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    public void updateList(List<Payment> newList) {
        this.paymentList = newList;
        sortPaymentsByDate();
        notifyDataSetChanged();
    }

    private void sortPaymentsByDate() {
        Collections.sort(paymentList, (p1, p2) -> {
            try {
                Date date1 = dateFormat.parse(p1.getTime());
                Date date2 = dateFormat.parse(p2.getTime());
                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }


}
