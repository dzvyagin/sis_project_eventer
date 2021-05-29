package com.zviagin_danila.eventer.ui.authentication.input_user_information_fragments;

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
import com.google.firebase.database.annotations.NotNull;
import com.kroegerama.imgpicker.BottomSheetImagePicker;
import com.kroegerama.imgpicker.ButtonType;
import com.zviagin_danila.eventer.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


public class InputImageAndDescriptionFragment extends Fragment implements BottomSheetImagePicker.OnImagesSelectedListener {

    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    Button inputImageButton;

    private ViewGroup imageContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_image_and_description, container, false);

        imageContainer = view.findViewById(R.id.image_container);
        inputImageButton = view.findViewById(R.id.input_image_button);
        inputImageButton.setOnClickListener(v -> {
            new BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                    .cameraButton(ButtonType.Button)
                    .galleryButton(ButtonType.Button)
                    .singleSelectTitle(R.string.pick_single)
                    .peekHeight(R.dimen.peekHeight)
                    .columnSize(R.dimen.columnSize)
                    .requestTag("single")
                    .show(getChildFragmentManager(), null);
        });
        // Inflate the layout for this fragment
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
            ImageView iv = (ImageView) LayoutInflater.from(getActivity()).inflate(R.layout.scrollitem_image, imageContainer, false);
            imageContainer.addView(iv);
            Glide.with(this).load(uri).into(iv);
        }
    }
}