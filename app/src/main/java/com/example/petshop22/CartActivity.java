package com.example.petshop22;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshop22.adapter.CartAdapter;
import com.example.petshop22.adapter.ProductAdapter;
import com.example.petshop22.data.database.DatabaseHelper;
import com.example.petshop22.data.model.CartItem;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartActionListener {

    private DatabaseHelper dbHelper;
    private CartAdapter adapter;
    private TextView tvTotal, tvEmpty;
    private LinearLayout bottomBar;
    private RecyclerView rvCartItems;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Giỏ hàng");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = DatabaseHelper.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("petshop_prefs", MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotal = findViewById(R.id.tvTotal);
        tvEmpty = findViewById(R.id.tvEmpty);
        bottomBar = findViewById(R.id.bottomBar);
        Button btnCheckout = findViewById(R.id.btnCheckout);

        adapter = new CartAdapter(this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có muốn tiếp tục đến trang thanh toán?")
                    .setPositiveButton("Tiếp tục", (d, w) -> {
                        startActivity(new Intent(this, BillingActivity.class));
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        loadCart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        List<CartItem> items = dbHelper.getCartItems(userId);
        adapter.setItems(items);

        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
            double total = 0;
            for (CartItem item : items) total += item.getSubtotal();
            tvTotal.setText(ProductAdapter.formatPrice(total));
        }
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        dbHelper.updateCartItemQuantity(item.getId(), newQuantity);
        loadCart();
    }

    @Override
    public void onRemoveItem(CartItem item) {

        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa sản phẩm này khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {

                    dbHelper.removeFromCart(item.getId());
                    loadCart();

                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
