package com.example.homeassistant.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.homeassistant.R;
import com.example.homeassistant.helpers.DatabaseHelper;
import com.example.homeassistant.model.Room;
import com.example.homeassistant.model.RoomModel;
import com.example.homeassistant.viewmodels.MainViewModel;

import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;


public class EditRoomFragment extends Fragment {
    private static final int GALLERY_REQUEST_CODE = 50;
    private static final int CAMERA_REQUEST_CODE = 60;

    private DatabaseHelper db;
    private RoomModel roomModel;
    private Room room;
    private MainViewModel model;

    Bitmap savedImage;

    private ImageView ivSelected;
    private EditText etRoomName;
    private Button bCamera;
    private Button bGallery;
    private String roomId;

    public EditRoomFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        roomId = EditRoomFragmentArgs.fromBundle(getArguments()).getRoomId();

        model = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);

        db = new DatabaseHelper(getActivity().getApplicationContext());

        roomModel = db.getRoomModelByRoomId(roomId);
        if (roomModel == null) {
            room = model.getBrokerData().getValue().getRoomById(roomId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_room, container, false);

        ImageButton ibBack = view.findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditRoomFragment.this.getActivity().onBackPressed();
            }
        });
        ImageButton ibSave = view.findViewById(R.id.ibSave);
        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        this.ivSelected = view.findViewById(R.id.ivSelected);
        this.etRoomName = view.findViewById(R.id.etChoosenRoomName);
        this.bCamera = view.findViewById(R.id.bCamera);
        this.bGallery = view.findViewById(R.id.bGallery);

        if (roomModel != null) {
            etRoomName.setText(roomModel.getName());
            savedImage = roomModel.getBackgroundImageAsBitmap();
            Glide.with(EditRoomFragment.this)
                    .load(savedImage)
                    .thumbnail(0.5f)
                    .into(ivSelected);
        } else {
            etRoomName.setText(room.getName());
        }


        bCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        bGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return view;
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
    }

    public void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    public void save() {
        if (roomModel == null) {
            roomModel = new RoomModel(-1, roomId, etRoomName.getText().toString(), savedImage);
            db.addRoom(roomModel);
        } else {
            if (savedImage != ((BitmapDrawable) ivSelected.getDrawable()).getBitmap()) {
                roomModel.setBackgroundImage(savedImage);
            }
            roomModel.setName(etRoomName.getText().toString());
            db.updateRoom(roomModel);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            try {
                Uri selectedImage = data.getData();
                InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                savedImage = BitmapFactory.decodeStream(imageStream);
                Glide.with(EditRoomFragment.this)
                        .load(savedImage)
                        .into(ivSelected);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivSelected.setImageBitmap(imageBitmap);
        }
    }
}