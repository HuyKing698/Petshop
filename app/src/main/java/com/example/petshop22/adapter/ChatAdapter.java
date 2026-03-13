package com.example.petshop22.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshop22.R;
import com.example.petshop22.data.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messages = new ArrayList<>();

    public void setMessages(List<Message> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position){

        Message m = messages.get(position);

        holder.tvMessage.setText(m.getMessage());

    }

    @Override
    public int getItemCount(){
        return messages.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView tvMessage;

        ChatViewHolder(View itemView){
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}