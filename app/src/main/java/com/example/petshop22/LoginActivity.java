package com.example.petshop22;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petshop22.data.database.DatabaseHelper;
import com.example.petshop22.data.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private TextView tvError;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("petshop_prefs", MODE_PRIVATE);
        long savedUserId = prefs.getLong("user_id", -1);
        if (savedUserId != -1) {
            startActivity(new Intent(this, ProductListActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        dbHelper = DatabaseHelper.getInstance(this);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tvError = findViewById(R.id.tvError);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> doLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void doLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập email và mật khẩu");
            return;
        }

        User user = dbHelper.loginUser(email, password);
        if (user != null) {
            SharedPreferences prefs = getSharedPreferences("petshop_prefs", MODE_PRIVATE);
            prefs.edit()
                    .putLong("user_id", user.getId())
                    .putString("user_name", user.getFullName())
                    .putString("user_email", user.getEmail())
                    .putString("user_phone", user.getPhone())
                    .apply();

            startActivity(new Intent(this, ProductListActivity.class));
            finish();
        } else {
            showError("Email hoặc mật khẩu không đúng");
        }
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
