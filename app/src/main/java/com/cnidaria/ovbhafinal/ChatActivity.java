package com.cnidaria.ovbhafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String chatroomId;
    private String username;

    private EditText messageInputField;
    private Button sendButton;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();

        // Get chatroomId and username from intent
        chatroomId = getIntent().getStringExtra("chatroomId");
        username = getIntent().getStringExtra("username");

        // Initialize views
        messageInputField = findViewById(R.id.messageInputField);
        sendButton = findViewById(R.id.buttonSend);
        recyclerView = findViewById(R.id.recycler_view);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);

        // Retrieve existing messages
        retrieveMessages();

        // Set up message listener for real-time updates
        setUpMessageListener();

        // Set up click listener for sendButton
        sendButton.setOnClickListener(v -> sendMessage());
    }


    private void retrieveMessages() {
        db.collection("messages")
                .whereEqualTo("chatroomId", chatroomId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Message> messages = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Message message = document.toObject(Message.class);
                        messages.add(message);
                        System.out.println(message.getMessageText());
                    }

                    messageAdapter.setMessages(messages);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }


    private void setUpMessageListener() {
        db.collection("messages")
                .whereEqualTo("chatroomId", chatroomId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        return;
                    }

                    List<Message> messages = new ArrayList<>();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Message message = document.toObject(Message.class);
                        messages.add(message);
                    }
                    messageAdapter.setMessages(messages);
                });
    }

    private void sendMessage() {
        String messageText = messageInputField.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Create a new message object
            Date currentTime = Calendar.getInstance().getTime();
            Message message = new Message(username, messageText, chatroomId, currentTime);

            // Add the message to Firestore
            db.collection("messages")
                    .add(message)
                    .addOnSuccessListener(documentReference -> {
                        // Message sent successfully
                        messageInputField.setText(""); // Clear the input field after sending
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }
}
