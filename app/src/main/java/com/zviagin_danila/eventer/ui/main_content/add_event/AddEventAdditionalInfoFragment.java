package com.zviagin_danila.eventer.ui.main_content.add_event;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.annotations.NotNull;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;
import com.zviagin_danila.eventer.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


public class AddEventAdditionalInfoFragment extends Fragment implements BottomSheetImagePicker.OnImagesSelectedListener {

    private static final int PICK_PHOTO_FOR_AVATAR = 1;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "startDateTime";
    private static final String ARG_PARAM2 = "endDateTime";
    private static final String ARG_PARAM3 = "title";
    private static final String ARG_PARAM4 = "location_latitude";
    private static final String ARG_PARAM5 = "location_longitude";

    private long startDateTime;
    private long endDateTime;
    private String title;
    private double location_latitude;
    private double location_longitude;
    private String imageUriString = "";

    MaterialButton addImageButton;
    Button inputImageButton;
    MaterialButton nextButton;

    private ViewGroup imageContainer;

    public AddEventAdditionalInfoFragment() {
        // Required empty public constructor
    }


    public static AddEventAdditionalInfoFragment newInstance(long param1, long param2, String param3,
                                                             double param4, double param5) {
        AddEventAdditionalInfoFragment fragment = new AddEventAdditionalInfoFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        args.putLong(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putDouble(ARG_PARAM4, param4);
        args.putDouble(ARG_PARAM5, param5);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_add_event_additional_info, container, false);

        nextButton = view.findViewById(R.id.add_event_additional_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, AddEventOptionalFragment.newInstance(
                                startDateTime,
                                endDateTime,
                                title,
                                location_latitude,
                                location_longitude,
                                imageUriString))
                        .commit();
            }
        });

        imageContainer = view.findViewById(R.id.add_event_image_container);
        addImageButton = view.findViewById(R.id.add_event_add_photo);
        addImageButton.setOnClickListener(v -> {
            new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                    .cameraButton(ButtonType.Button)
                    .galleryButton(ButtonType.Button)
                    .singleSelectTitle(R.string.pick_single)
                    .peekHeight(R.dimen.peekHeight)
                    .columnSize(R.dimen.columnSize)
                    .requestTag("single")
                    .show(getChildFragmentManager(), null);
        });


        // return view
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onImagesSelected(@NotNull List<? extends Uri> uris, @Nullable String tag) {
        imageContainer.removeAllViews();
        for (Uri uri : uris) {
            imageUriString = uri.toString();
            ImageView iv = (ImageView) LayoutInflater.from(getActivity()).inflate(R.layout.scrollitem_image, imageContainer, false);
            imageContainer.addView(iv);
            Glide.with(this).load(uri).into(iv);
        }
    }
}