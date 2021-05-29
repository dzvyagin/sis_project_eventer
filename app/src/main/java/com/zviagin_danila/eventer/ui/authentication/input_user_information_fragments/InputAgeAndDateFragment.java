package com.zviagin_danila.eventer.ui.authentication.input_user_information_fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.zviagin_danila.eventer.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES;
import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES_BIRTHDAY;
import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES_FIRST_NAME;
import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES_LAST_NAME;


public class InputAgeAndDateFragment extends Fragment {


    private Button selectBirthDayButton;
    private Calendar birthDayCalender;
    private SimpleDateFormat dateFormat;

    private SharedPreferences userInfoPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_age_and_date, container, false);

        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

        dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        selectBirthDayButton = view.findViewById(R.id.select_birthday_button);
        selectBirthDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getChildFragmentManager(), datePicker.toString());
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override public void onPositiveButtonClick(Long selection) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+3"));
                        calendar.setTimeInMillis(selection);
                        birthDayCalender = calendar;
                    }
                });
                selectBirthDayButton.setText(dateFormat.format(birthDayCalender.getTime()));
            }
        });
        userInfoPreferences = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfoPreferences.edit();
        editor.putString(APP_PREFERENCES_BIRTHDAY, dateFormat.format(birthDayCalender.getTime()));
        editor.apply();

        selectBirthDayButton.setFocusable(true);

        return view;
    }
}