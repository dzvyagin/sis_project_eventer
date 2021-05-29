package com.zviagin_danila.eventer.ui.authentication.input_user_information_fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zviagin_danila.eventer.R;

import java.util.Objects;

import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES;
import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES_FIRST_NAME;
import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES_LAST_NAME;


public class InputNameFragment extends Fragment {

    private TextInputLayout inputFirstName;
    private TextInputLayout inputLastName;

    private SharedPreferences userInfoPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_name, container, false);

        inputFirstName = view.findViewById(R.id.input_first_name_edit_text);
        inputLastName = view.findViewById(R.id.input_first_name_edit_text);

        userInfoPreferences = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfoPreferences.edit();
        editor.putString(APP_PREFERENCES_FIRST_NAME, inputFirstName.getEditText().getText().toString().trim());
        editor.putString(APP_PREFERENCES_LAST_NAME, inputLastName.getEditText().getText().toString().trim());
        editor.apply();

        // Inflate the layout for this fragment
        return view;
    }
}