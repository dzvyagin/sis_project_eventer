package com.zviagin_danila.eventer.ui.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.zviagin_danila.eventer.MainActivity;
import com.zviagin_danila.eventer.R;

import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;


public class InputMobileNumberFragment extends Fragment {

    MaterialButton nextButton;
    EditText numberEditText;

    FirebaseAuth auth;

    CountryCodePicker countryCodePicker;

    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_input_mobile_number, container, false);

        countryCodePicker = (CountryCodePicker) view.findViewById(R.id.ccp);
        numberEditText = view.findViewById(R.id.input_number_edit_text);
        countryCodePicker.registerCarrierNumberEditText(numberEditText);

        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("ru");

        nextButton = view.findViewById(R.id.input_number_next_button);
        nextButton.setOnClickListener(v -> {

            if (!countryCodePicker.isValidFullNumber()) {
                numberEditText.setError("Enter a valid phone");
                numberEditText.requestFocus();
                return;
            }
            String phoneNumber = countryCodePicker.getFullNumberWithPlus();

            Intent intent = new Intent(getActivity(), VerifyPhoneActivity.class);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
        });
        // Inflate the layout for this fragment
        return view;
    }
}