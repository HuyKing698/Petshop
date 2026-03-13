package com.example.petshop22;
import com.example.petshop22.notification.ChatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.view.MenuItemCompat;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshop22.adapter.ProductAdapter;
import com.example.petshop22.data.database.DatabaseHelper;
import com.example.petshop22.data.model.Product;
import com.example.petshop22.notification.CartNotificationHelper;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ProductListActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;
    private LinearLayout searchLayout;
    private EditText edtSearch;
    private LinearLayout chipContainer;
    private String selectedCategory = null;
    private long userId;

    private final ActivityResultLauncher<String> notifPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) checkCartAndNotify();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        dbHelper = DatabaseHelper.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("petshop_prefs", MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("PetShop22");
        setSupportActionBar(toolbar);

        searchLayout = findViewById(R.id.searchLayout);
        edtSearch = findViewById(R.id.edtSearch);
        ImageButton btnCloseSearch = findViewById(R.id.btnCloseSearch);
        chipContainer = findViewById(R.id.chipContainer);
        RecyclerView rvProducts = findViewById(R.id.rvProducts);

        adapter = new ProductAdapter(this);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(adapter);

        btnCloseSearch.setOnClickListener(v -> {
            searchLayout.setVisibility(View.GONE);
            edtSearch.setText("");
            loadProducts();
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadProducts();
                } else {
                    adapter.setProducts(dbHelper.searchProducts(query));
                }
            }
        });

        setupCategoryChips();
        loadProducts();

        CartNotificationHelper notifHelper = new CartNotificationHelper(this);
        notifHelper.createNotificationChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                checkCartAndNotify();
            } else {
                notifPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            checkCartAndNotify();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
        invalidateOptionsMenu();
    }

    private void setupCategoryChips() {
        chipContainer.removeAllViews();

        Chip allChip = new Chip(this);
        allChip.setText("Tất cả");
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setOnClickListener(v -> {
            selectedCategory = null;
            loadProducts();
            uncheckAllChips();
            allChip.setChecked(true);
        });
        chipContainer.addView(allChip);

        for (String category : dbHelper.getCategories()) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                selectedCategory = category;
                uncheckAllChips();
                chip.setChecked(true);
                adapter.setProducts(dbHelper.getProductsByCategory(category));
            });
            chipContainer.addView(chip);
        }
    }

    private void uncheckAllChips() {
        for (int i = 0; i < chipContainer.getChildCount(); i++) {
            View child = chipContainer.getChildAt(i);
            if (child instanceof Chip) {
                ((Chip) child).setChecked(false);
            }
        }
    }

    private void loadProducts() {
        if (selectedCategory != null) {
            adapter.setProducts(dbHelper.getProductsByCategory(selectedCategory));
        } else {
            adapter.setProducts(dbHelper.getAllProducts());
        }
    }

    private void checkCartAndNotify() {
        if (userId != -1) {
            int count = dbHelper.getCartItemCount(userId);
            if (count > 0) {
                new CartNotificationHelper(this).showCartNotification(count);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem ordersItem = menu.findItem(R.id.action_orders);
        MenuItem cartItem = menu.findItem(R.id.action_cart);

        int orderCount = (userId != -1) ? dbHelper.getOrdersByUserId(userId).size() : 0;
        int cartCount = (userId != -1) ? dbHelper.getCartItemCount(userId) : 0;

        View ordersView = MenuItemCompat.getActionView(ordersItem);
        if (ordersView != null) {
            TextView tvOrdersCount = ordersView.findViewById(R.id.tvOrdersCount);
            if (tvOrdersCount != null) tvOrdersCount.setText(String.valueOf(orderCount));
            ordersView.setOnClickListener(v -> startActivity(new Intent(this, PendingDeliveryActivity.class)));
        }

        View cartView = MenuItemCompat.getActionView(cartItem);
        if (cartView != null) {
            TextView tvCartCount = cartView.findViewById(R.id.tvCartCount);
            if (tvCartCount != null) tvCartCount.setText(String.valueOf(cartCount));
            cartView.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            searchLayout.setVisibility(searchLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            if (searchLayout.getVisibility() == View.VISIBLE) edtSearch.requestFocus();
            return true;
        } else if (id == R.id.action_orders) {
            startActivity(new Intent(this, PendingDeliveryActivity.class));
            return true;
        } else if (id == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        else if (id == R.id.action_logout) {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất khỏi tài khoản?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {

                        getSharedPreferences("petshop_prefs", MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();

                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    })
                    .setNegativeButton("Hủy", null)
                    .show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }
}
