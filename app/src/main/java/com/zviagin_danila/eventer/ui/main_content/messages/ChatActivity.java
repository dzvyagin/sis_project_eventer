package com.zviagin_danila.eventer.ui.main_content.messages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zviagin_danila.eventer.MainActivity;
import com.zviagin_danila.eventer.R;
import com.zviagin_danila.eventer.ui.authentication.User;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView messageListView;
    private MessageAdapter adapter;
    private ProgressBar progressBar;
    private ImageButton sendImageButton;
    private Button sendMessageButton;
    private EditText messageEditText;
    ImageButton backButton;

    private String userName;
    private String recipientUserId;
    private String recipientUserName;

    private static final int RC_IMAGE_PICKER = 123;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference messagesDatabaseReference;
    private ChildEventListener messagesChildEventListener;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListener;

    FirebaseStorage storage;
    StorageReference chatImagesStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            recipientUserId = intent.getStringExtra("recipientUserId");
            recipientUserName = intent.getStringExtra("recipientUserName");
        }

        setTitle("Chat with " + recipientUserName);

        // Initialize FireBase variables
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");

        storage = FirebaseStorage.getInstance();
        chatImagesStorageReference = storage.getReference().child("chat_images");

        progressBar = findViewById(R.id.progressBar);
        sendImageButton = findViewById(R.id.sendImageButton);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageEditText = findViewById(R.id.messageEditText);

        // back button initialize
        backButton = findViewById(R.id.chat_back_button);
        backButton.setOnClickListener(v -> {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        });

        messageListView = findViewById(R.id.messageListView);
        List<Message> messages = new ArrayList<>();
        adapter = new MessageAdapter(this, R.layout.message_item, messages);
        messageListView.setAdapter(adapter);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendMessageButton.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        messageEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(500)});

        sendMessageButton.setOnClickListener(v -> {

            Message message = new Message();
            message.setText(messageEditText.getText().toString());
            message.setName(userName);
            message.setSender(auth.getCurrentUser().getUid());
            message.setRecipient(recipientUserId);
            message.setImageUrl(null);

            messagesDatabaseReference.push().setValue(message);

            messageEditText.setText("");
        });

        sendImageButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
            intent1.setType("image/*");
            intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent1, "Choose an image"),
                    RC_IMAGE_PICKER);
        });

        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userName = user.getFirstName() + " " + user.getLastName();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        usersDatabaseReference.addChildEventListener(usersChildEventListener);

        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);

                if (message.getSender().equals(auth.getCurrentUser().getUid())
                        && message.getRecipient().equals(recipientUserId)) {
                    message.setMineMessage(true);
                    adapter.add(message);
                } else if (message.getRecipient().equals(auth.getCurrentUser().getUid())
                        && message.getSender().equals(recipientUserId)) {
                    message.setMineMessage(false);
                    adapter.add(message);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        messagesDatabaseReference.addChildEventListener(messagesChildEventListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            final StorageReference imageReference = chatImagesStorageReference
                    .child(selectedImageUri.getLastPathSegment());
            UploadTask uploadTask = imageReference.putFile(selectedImageUri);

            uploadTask = imageReference.putFile(selectedImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Message message = new Message();
                    message.setImageUrl(downloadUri.toString());
                    message.setName(userName);
                    message.setSender(auth.getCurrentUser().getUid());
                    message.setRecipient(recipientUserId);
                    messagesDatabaseReference.push().setValue(message);
                } else {
                    // Handle failures
                    // ...
                }
            });
        }
    }
}