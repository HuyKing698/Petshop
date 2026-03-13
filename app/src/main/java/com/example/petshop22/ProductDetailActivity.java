package com.example.petshop22;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.petshop22.adapter.ProductAdapter;
import com.example.petshop22.data.database.DatabaseHelper;
import com.example.petshop22.data.model.Product;
import com.example.petshop22.notification.ChatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private Product product;
    private TextView tvQuantity, tvSubtotal;
    Button btnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Chi tiết sản phẩm");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        long productId = getIntent().getLongExtra("product_id", -1);
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        product = dbHelper.getProductById(productId);

        if (product == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageView imgProduct = findViewById(R.id.imgProduct);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvStock = findViewById(R.id.tvStock);
        TextView tvDescription = findViewById(R.id.tvDescription);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        btnChat = findViewById(R.id.btnChat);
        tvProductName.setText(product.getName());
        tvPrice.setText(ProductAdapter.formatPrice(product.getPrice()));
        tvDescription.setText(product.getDescription());
        tvCategory.setText(product.getCategory());

        int color = ProductAdapter.getCategoryColor(product.getCategory());
        tvCategory.setTextColor(color);

        if (product.getStock() > 0) {
            tvStock.setText(String.format("Còn %d sản phẩm", product.getStock()));
        } else {
            tvStock.setText("Hết hàng");
            tvStock.setTextColor(getColor(R.color.error));
            btnAddToCart.setEnabled(false);
        }

        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(imgProduct);

        updateQuantityUI();

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityUI();
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (quantity < product.getStock()) {
                quantity++;
                updateQuantityUI();
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("petshop_prefs", MODE_PRIVATE);
            long userId = prefs.getLong("user_id", -1);
            if (userId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper.addToCart(userId, product.getId(), quantity);
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            finish();
        });
        btnChat.setOnClickListener(v -> {

            Intent intent = new Intent(ProductDetailActivity.this, ChatActivity.class);

            intent.putExtra("productId", product.getId());
            intent.putExtra("productName", product.getName());

            startActivity(intent);

        });
    }

    private void updateQuantityUI() {
        tvQuantity.setText(String.valueOf(quantity));
        tvSubtotal.setText("Tạm tính: " + ProductAdapter.formatPrice(product.getPrice() * quantity));
    }
}
