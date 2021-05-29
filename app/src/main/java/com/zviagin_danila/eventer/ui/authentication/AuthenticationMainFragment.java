package com.zviagin_danila.eventer.ui.authentication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.zviagin_danila.eventer.R;

public class AuthenticationMainFragment extends Fragment {

    MaterialButton loginByNumber;
    MaterialButton loginByGoogle;
    MaterialButton loginByFacebook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_authentication_main, container, false);

        loginByNumber = view.findViewById(R.id.login_phone_number_button);
        loginByNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new InputMobileNumberFragment())
                        .commit();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}