package com.example.petshop22;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshop22.adapter.OrderAdapter;
import com.example.petshop22.data.database.DatabaseHelper;
import com.example.petshop22.data.model.Order;

import java.util.List;

public class PendingDeliveryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_delivery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Đơn hàng của tôi");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("petshop_prefs", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        List<Order> orders = dbHelper.getOrdersByUserId(userId);

        RecyclerView rvOrders = findViewById(R.id.rvOrders);
        TextView tvEmpty = findViewById(R.id.tvEmpty);

        if (orders.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvOrders.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvOrders.setVisibility(View.VISIBLE);

            OrderAdapter adapter = new OrderAdapter();
            adapter.setOrders(orders);
            rvOrders.setLayoutManager(new LinearLayoutManager(this));
            rvOrders.setAdapter(adapter);
        }
    }
}
