package com.example.petshop22.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshop22.R;
import com.example.petshop22.data.model.Order;
import com.example.petshop22.data.model.OrderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> orders = new ArrayList<>();

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Đơn hàng #" + order.getId());
        holder.tvStatus.setText(order.getStatus());
        holder.tvPaymentMethod.setText("Thanh toán: " + order.getPaymentMethod());
        holder.tvTotal.setText(ProductAdapter.formatPrice(order.getTotalAmount()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(order.getCreatedAt())));

        holder.itemsContainer.removeAllViews();
        for (OrderItem item : order.getItems()) {
            TextView tv = new TextView(holder.itemView.getContext());
            tv.setText(String.format(Locale.getDefault(), "%s  x%d  %s",
                    item.getProductName(), item.getQuantity(),
                    ProductAdapter.formatPrice(item.getSubtotal())));
            tv.setTextSize(13);
            tv.setPadding(0, 4, 0, 4);
            holder.itemsContainer.addView(tv);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvDate, tvPaymentMethod, tvStatus, tvTotal;
        LinearLayout itemsContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            itemsContainer = itemView.findViewById(R.id.itemsContainer);
        }
    }
}
