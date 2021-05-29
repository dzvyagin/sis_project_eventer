package com.zviagin_danila.eventer.ui.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import com.zviagin_danila.eventer.MainActivity;
import com.zviagin_danila.eventer.R;
import com.zviagin_danila.eventer.ui.authentication.input_user_information_fragments.InputAgeAndDateFragment;
import com.zviagin_danila.eventer.ui.authentication.input_user_information_fragments.InputImageAndDescriptionFragment;
import com.zviagin_danila.eventer.ui.authentication.input_user_information_fragments.InputNameFragment;

import java.util.HashMap;
import java.util.Map;

import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES;
import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES_FIRST_NAME;
import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES_LAST_NAME;

public class InputUserInformationActivity extends AppCompatActivity {

    private Button nextButton;
    private int currentFragment;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseFirestore firestore;
    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_user_information);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
        usersDatabaseReference = database.getReference().child("users");

        nextButton = findViewById(R.id.input_information_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeInputInformation();
            }
        });
        nextButton.setText("Завершить");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.input_information_container, new InputNameFragment())
                .commit();



    }


    private void completeInputInformation() {
        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setTitle("Complete?")
                .setMessage("Are you sure want to complete?")
                .setCancelable(false)
                .setPositiveButton("Complete", R.drawable.ic_baseline_check_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        FirebaseUser user = auth.getCurrentUser();
                        createUser(user);
                        Intent intent = new Intent(InputUserInformationActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", R.drawable.ic_close, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();
    }

    private void createUser(FirebaseUser firebaseUser) {
        SharedPreferences userInfo = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setPhoneNumber(firebaseUser.getPhoneNumber());
        user.setFirstName(userInfo.getString(APP_PREFERENCES_FIRST_NAME, "Error"));
        user.setLastName(userInfo.getString(APP_PREFERENCES_LAST_NAME, "Error"));
        usersDatabaseReference.child(firebaseUser.getUid()).setValue(user);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", userInfo.getString(APP_PREFERENCES_FIRST_NAME, "Error"));
        userMap.put("lastName", userInfo.getString(APP_PREFERENCES_LAST_NAME, "Error"));
        userMap.put("phone", firebaseUser.getPhoneNumber());
        userMap.put("uid", firebaseUser.getUid());
        firestore.collection("users").add(userMap);

    }
}