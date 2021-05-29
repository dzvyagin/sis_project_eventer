package com.zviagin_danila.eventer.ui.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zviagin_danila.eventer.MainActivity;
import com.zviagin_danila.eventer.R;

import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class VerifyPhoneActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    FirebaseFirestore firestore;
    private DatabaseReference usersDatabaseReference;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private ProgressBar progressBar;
    private EditText enterCodeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();
        mAuth.getFirebaseAuthSettings()
                .setAppVerificationDisabledForTesting(true);

        firestore = FirebaseFirestore.getInstance();

        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child("users");

        progressBar = findViewById(R.id.enterCodeProgressBar);
        enterCodeEditText = findViewById(R.id.enterCodeEditText);

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        Log.w("phoneNumber", phoneNumber);
        sendVerificationCode(phoneNumber);

        findViewById(R.id.enterCodeSignInButton).setOnClickListener(v -> {
            String codeToEditText = enterCodeEditText.getText().toString().trim();

            if (codeToEditText.isEmpty() || codeToEditText.length() < 6) {
                enterCodeEditText.setError("Enter correct code...");
                enterCodeEditText.requestFocus();
                return;
            }

            verifyCode(codeToEditText);
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void sendVerificationCode(String phoneNumber) {
        progressBar.setVisibility(View.VISIBLE);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                // This callback will be invoked in two situations:
                                // 1 - Instant verification. In some cases the phone number can be instantly
                                //     verified without needing to send or enter a verification code.
                                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                                //     detect the incoming verification SMS and perform verification without
                                //     user action.
                                Log.d(TAG, "onVerificationCompleted:" + credential);

                                String smsCode = credential.getSmsCode();
                                if (smsCode != null) {
                                    enterCodeEditText.setText(smsCode);
                                    verifyCode(smsCode);
                                }
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                // This callback is invoked in an invalid request for verification is made,
                                // for instance if the the phone number format is not valid.
                                Log.w(TAG, "onVerificationFailed", e);

                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(VerifyPhoneActivity.this, e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    // The SMS quota for the project has been exceeded
                                }

                                // Show a message and update the UI
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                // The SMS verification code has been sent to the provided phone number, we
                                // now need to ask the user to enter the code and then construct a credential
                                // by combining the code with a verification ID.
                                Log.d(TAG, "onCodeSent:" + verificationId);

                                // Save verification ID and resending token so we can use them later
                                mVerificationId = verificationId;
                                mResendToken = token;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            //Get User ID
                            final String userId = user.getUid();

                            firestore.collection("users")
                                    .whereEqualTo("uid", userId)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });

                            Intent intent = new Intent(VerifyPhoneActivity.this, InputUserInformationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(VerifyPhoneActivity.this,
                                        task.getException().getLocalizedMessage(), Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
                });
    }
}