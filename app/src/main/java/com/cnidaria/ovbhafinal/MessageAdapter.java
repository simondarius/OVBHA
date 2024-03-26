package com.cnidaria.ovbhafinal;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;


    private static String currentUsername;
    public MessageAdapter() {
        this.messages = new ArrayList<>();
    }

    public MessageAdapter(String currentUsername) {
        this.currentUsername = currentUsername;
        this.messages = new ArrayList<>();
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView senderTextView;
        private TextView contentTextView;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
        }

        public void bind(Message message) {
            String username=message.getUsername();
            System.out.println(currentUsername);
            if(username.equals(currentUsername)){
                senderTextView.setText("Me");
                senderTextView.setTextColor(Color.RED);
            }else{
                senderTextView.setText(username);
            }


            contentTextView.setText(message.getMessageText());

        }
    }
}
