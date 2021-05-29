package com.zviagin_danila.eventer.ui.main_content.add_event;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.zviagin_danila.eventer.R;
import com.zviagin_danila.eventer.ui.main_content.home.HomeFragment;

import java.util.HashMap;
import java.util.Map;

import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES;
import static com.zviagin_danila.eventer.Utils.APP_PREFERENCES_CURRENT_ID;


public class AddEventOptionalFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "startDateTime";
    private static final String ARG_PARAM2 = "endDateTime";
    private static final String ARG_PARAM3 = "title";
    private static final String ARG_PARAM4 = "location_latitude";
    private static final String ARG_PARAM5 = "location_longitude";
    private static final String ARG_PARAM6 = "imageUri";

    private static final String TAG = "Add event";

    private long startDateTime;
    private long endDateTime;
    private String title;
    private double location_latitude;
    private double location_longitude;
    private String imageUriString;
    private Uri imageUri;

    Switch switchPrice;
    MaterialButton addEventButton;
    TextInputLayout priceTextInputLayout;
    TextInputLayout descriptionTextInputLayout;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;

    public AddEventOptionalFragment() {
        // Required empty public constructor
    }

    public static AddEventOptionalFragment newInstance(long param1, long param2, String param3,
                                                       double param4, double param5, String param6) {
        AddEventOptionalFragment fragment = new AddEventOptionalFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        args.putLong(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putDouble(ARG_PARAM4, param4);
        args.putDouble(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
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
            location_latitude = getArguments().getDouble(ARG_PARAM4);
            location_longitude = getArguments().getDouble(ARG_PARAM5);
            imageUri = Uri.parse(getArguments().getString(ARG_PARAM6));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_add_event_optional, container, false);

        // Initialize FireStore Cloud from adding event
        firebaseFirestore = FirebaseFirestore.getInstance();
        //Initialize FireAuth from check current user UId
        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize Storage from save image
        firebaseStorage = FirebaseStorage.getInstance();

        addEventButton = view.findViewById(R.id.add_event_button);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, new HomeFragment())
                        .commit();
            }
        });

        priceTextInputLayout = view.findViewById(R.id.add_event_price_text_input_layout);
        descriptionTextInputLayout = view.findViewById(R.id.add_event_description_text_input_layout);

        switchPrice = view.findViewById(R.id.switch_coast);
        switchPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    priceTextInputLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // return fragment view
        return view;
    }

    private void saveEvent() {

        // Create a new event
        Map<String, Object> event = new HashMap<>();
        event.put("authorUId", firebaseAuth.getCurrentUser().getUid());
        event.put("startDateTime", startDateTime);
        event.put("endDateTime", endDateTime);
        event.put("title", title);
        event.put("location_latitude", location_latitude);
        event.put("location_longitude", location_longitude);
        event.put("description", descriptionTextInputLayout.getEditText().getText().toString().trim());
        String price = priceTextInputLayout.getEditText().getText().toString().trim();
        event.put("price", price != null ? price : 0);
        event.put("eventId", nextEventId());
        event.put("phoneNumber", firebaseAuth.getCurrentUser().getPhoneNumber());


        // Add a new document with a generated ID
        firebaseFirestore.collection("events")
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private Integer nextEventId() {
        int id;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(APP_PREFERENCES_CURRENT_ID)) {
            id = sharedPreferences.getInt(APP_PREFERENCES_CURRENT_ID, 0) + 1;
            SharedPreferences.Editor editor =  sharedPreferences.edit();
            editor.putInt(APP_PREFERENCES_CURRENT_ID, id);
            editor.apply();
        } else {
            id = 0;
            SharedPreferences.Editor editor =  sharedPreferences.edit();
            editor.putInt(APP_PREFERENCES_CURRENT_ID, id);
            editor.apply();
        }
        return id;
    }
}