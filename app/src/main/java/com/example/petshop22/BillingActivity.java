package com.example.petshop22;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.petshop22.adapter.ProductAdapter;
import com.example.petshop22.data.database.DatabaseHelper;
import com.example.petshop22.data.model.CartItem;

import java.util.List;

public class BillingActivity extends AppCompatActivity {

    private EditText edtName, edtPhone, edtAddress;
    private RadioGroup radioGroupPayment;
    private TextView tvTotal, tvOrderSummary;
    private LinearLayout orderItemsContainer;
    private Button btnConfirmPayment;
    private DatabaseHelper dbHelper;
    private long userId;
    private List<CartItem> cartItems;
    private double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Thanh toán");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = DatabaseHelper.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("petshop_prefs", MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        tvTotal = findViewById(R.id.tvTotal);
        tvOrderSummary = findViewById(R.id.tvOrderSummary);
        orderItemsContainer = findViewById(R.id.orderItemsContainer);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        edtName.setText(prefs.getString("user_name", ""));
        edtPhone.setText(prefs.getString("user_phone", ""));

        loadOrderSummary();

        btnConfirmPayment.setOnClickListener(v -> confirmPayment());
    }

    private void loadOrderSummary() {
        cartItems = dbHelper.getCartItems(userId);
        total = 0;
        orderItemsContainer.removeAllViews();

        tvOrderSummary.setText("Đơn hàng (" + cartItems.size() + " sản phẩm)");

        for (CartItem item : cartItems) {
            if (item.getProduct() == null) continue;
            total += item.getSubtotal();

            TextView tv = new TextView(this);
            tv.setText(String.format("%s  x%d  %s",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    ProductAdapter.formatPrice(item.getSubtotal())));
            tv.setTextSize(13);
            tv.setPadding(0, 6, 0, 6);
            orderItemsContainer.addView(tv);
        }

        tvTotal.setText(ProductAdapter.formatPrice(total));
    }

    private void confirmPayment() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty()) { Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show(); return; }
        if (phone.isEmpty()) { Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show(); return; }
        if (address.isEmpty()) { Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show(); return; }
        if (cartItems.isEmpty()) { Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show(); return; }

        String paymentMethod = getSelectedPaymentMethod();

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage("Tổng: " + ProductAdapter.formatPrice(total) +
                        "\nPhương thức: " + paymentMethod +
                        "\n\nBạn có chắc muốn xác nhận?")
                .setPositiveButton("Xác nhận", (d, w) ->
                        processNormalPayment(name, phone, address, paymentMethod))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String getSelectedPaymentMethod() {
        int checkedId = radioGroupPayment.getCheckedRadioButtonId();
        if (checkedId == R.id.radioBank) return "Chuyển khoản ngân hàng";
        if (checkedId == R.id.radioMomo) return "Ví MoMo";
        return "COD";
    }

    private void processNormalPayment(String name, String phone, String address, String method) {
        long orderId = dbHelper.createOrder(userId, name, phone, address, method, cartItems, total);
        if (orderId > 0) {
            dbHelper.clearCart(userId);
            showSuccessDialog();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thanh toán thành công!")
                .setMessage("Đơn hàng của bạn đã được xác nhận. Chúng tôi sẽ liên hệ để giao hàng sớm nhất.")
                .setCancelable(false)
                .setPositiveButton("Về trang chủ", (d, w) -> {
                    Intent intent = new Intent(this, ProductListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}
