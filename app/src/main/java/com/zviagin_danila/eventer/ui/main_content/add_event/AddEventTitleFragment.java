package com.zviagin_danila.eventer.ui.main_content.add_event;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.zviagin_danila.eventer.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AddEventTitleFragment extends Fragment {

    View view;

    final Calendar startCalender = Calendar.getInstance(TimeZone.getTimeZone("UTC+3"));
    final Calendar endCalender = Calendar.getInstance(TimeZone.getTimeZone("UTC+3"));

    TextInputLayout titleTextInputLayout;
    TextView dateStartTextView;
    TextView timeStartTextView;
    TextView dateEndTextView;
    TextView timeEndTextView;


    MaterialButton nextButton;

    MaterialDatePicker<Long> dateStartDatePicker;
    MaterialDatePicker<Long> dateEndDatePicker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_event_title, container, false);

        titleTextInputLayout = view.findViewById(R.id.input_event_title_text_input_layout);

        nextButton = view.findViewById(R.id.add_event_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDate();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, AddEventLocationFragment.newInstance(
                                startCalender.getTimeInMillis(),
                                endCalender.getTimeInMillis(),
                                titleTextInputLayout.getEditText().getText().toString().trim()))
                        .commit();
            }
        });

        dateStartTextView = view.findViewById(R.id.date_start_text_view);
        timeStartTextView = view.findViewById(R.id.time_start_text_view);
        dateEndTextView = view.findViewById(R.id.date_end_text_view);
        timeEndTextView = view.findViewById(R.id.time_end_text_view);

        dateStartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateStartDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .setTitleText("Выберите дату")
                        .build();

                dateStartDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override public void onPositiveButtonClick(Long selection) {
                        startCalender.setTimeInMillis(selection);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        dateStartTextView.setText(dateFormat.format(startCalender.getTime()));
                    }
                });

                dateStartDatePicker.show(getChildFragmentManager(), "tag");
            }
        });

        dateEndTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateEndDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .setTitleText("Выберите дату")
                        .build();

                dateEndDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override public void onPositiveButtonClick(Long selection) {
                        endCalender.setTimeInMillis(selection);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                        dateEndTextView.setText(dateFormat.format(endCalender.getTime()));
                    }
                });

                dateEndDatePicker.show(getChildFragmentManager(), "tag");
            }
        });

        timeStartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = startCalender.get(Calendar.HOUR_OF_DAY);
                int minute = startCalender.get(Calendar.MINUTE);


                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            timeStartTextView.setText(getCorrectTime(hourOfDay, minute));
                            startCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            startCalender.set(Calendar.MINUTE, minute);

                        }
                    }
                };

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });

        timeEndTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = endCalender.get(Calendar.HOUR_OF_DAY);
                int minute = endCalender.get(Calendar.MINUTE);


                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            timeEndTextView.setText(getCorrectTime(hourOfDay, minute));
                            endCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            endCalender.set(Calendar.MINUTE, minute);

                        }
                    }
                };

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
                timePickerDialog.setTitle("Choose hour:");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });

        return view;
    }

    private void saveDate() {

    }

    private String getCorrectTime(int hourOfDay, int minute) {
        String hourString;
        String minuteString;
        if (hourOfDay < 10){
            hourString = "0" + hourOfDay;
        } else {
            hourString = String.valueOf(hourOfDay);
        }

        if (minute < 10){
            minuteString = "0" + minute;
        } else {
            minuteString = String.valueOf(minute);
        }

        return hourString + ":" + minuteString;
    }

}