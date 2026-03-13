package com.example.petshop22.notification;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshop22.R;
import com.example.petshop22.adapter.ChatAdapter;
import com.example.petshop22.data.database.DatabaseHelper;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText edtMessage;
    private ChatAdapter adapter;
    private DatabaseHelper dbHelper;

    private long userId;
    private long productId;
    private String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dbHelper = DatabaseHelper.getInstance(this);

        SharedPreferences prefs = getSharedPreferences("petshop_prefs", MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        rvChat = findViewById(R.id.rvChat);
        edtMessage = findViewById(R.id.edtMessage);
        Button btnSend = findViewById(R.id.btnSend);

        productId = getIntent().getLongExtra("productId", -1);
        productName = getIntent().getStringExtra("productName");

        setTitle("Chat about: " + productName);

        adapter = new ChatAdapter();
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        loadMessages();

        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        adapter.setMessages(dbHelper.getMessages(userId, productId));
        rvChat.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void sendMessage() {

        String msg = edtMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        dbHelper.insertMessage(userId, productId, msg, "user");

        edtMessage.setText("");

        loadMessages();

        fakeStoreReply();
    }

    private void fakeStoreReply() {

        new Handler().postDelayed(() -> {

            dbHelper.insertMessage(
                    userId,
                    productId,
                    "Cửa hàng đã nhận tin nhắn của bạn 👍",
                    "store"
            );

            loadMessages();

        }, 1500);
    }
}