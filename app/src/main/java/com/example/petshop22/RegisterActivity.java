package com.example.petshop22;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petshop22.data.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPassword, edtConfirmPassword, edtPhone;
    private TextView tvError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = DatabaseHelper.getInstance(this);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtPhone = findViewById(R.id.edtPhone);
        tvError = findViewById(R.id.tvError);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> doRegister());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void doRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirm = edtConfirmPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (fullName.isEmpty()) { showError("Vui lòng nhập họ tên"); return; }
        if (email.isEmpty()) { showError("Vui lòng nhập email"); return; }
        if (!isValidEmail(email)) {
            showError("Email không hợp lệ");
            return;
        }
        if (password.isEmpty()) { showError("Vui lòng nhập mật khẩu"); return; }
        if (password.length() < 6) { showError("Mật khẩu tối thiểu 6 ký tự"); return; }
        if (!phone.matches("^0[0-9]{9}$")) {
            showError("Số điện thoại phải 10 số");
            return;
        }
        if (!password.equals(confirm)) { showError("Mật khẩu xác nhận không khớp"); return; }

        if (dbHelper.isEmailExists(email)) {
            showError("Email đã được sử dụng");
            return;
        }

        long userId = dbHelper.registerUser(fullName, email, password, phone);
        if (userId > 0) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            showError("Đăng ký thất bại. Vui lòng thử lại.");
        }

    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^0[0-9]{9}$");
    }
}
