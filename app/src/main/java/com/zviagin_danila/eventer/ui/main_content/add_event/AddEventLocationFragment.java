package com.zviagin_danila.eventer.ui.main_content.add_event;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.zviagin_danila.eventer.R;

import java.util.Calendar;


public class AddEventLocationFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "startDateTime";
    private static final String ARG_PARAM2 = "endDateTime";
    private static final String ARG_PARAM3 = "title";

    private long startDateTime;
    private long endDateTime;
    private String title;

    MaterialButton nextButton;

    public AddEventLocationFragment() {
        // Required empty public constructor
    }

    public static AddEventLocationFragment newInstance(long param1, long param2, String title) {
        AddEventLocationFragment fragment = new AddEventLocationFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        args.putLong(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            startDateTime = getArguments().getLong(ARG_PARAM1);
            endDateTime = getArguments().getLong(ARG_PARAM2);
            title = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_add_event_location, container, false);

        // Initialize map fragment
        MapFragment mapFragment = new MapFragment();
        // Open fragment
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.add_event_location_maps_container, mapFragment)
                .commit();

        // Initialize next button
        nextButton = view.findViewById(R.id.add_event_location_next_button);

        // change fragment on click button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, AddEventAdditionalInfoFragment.newInstance(
                                startDateTime,
                                endDateTime,
                                title,
                                mapFragment.getLatitude(),
                                mapFragment.getLongitude()))
                        .commit();
            }
        });

        // Return view
        return view;
    }
}