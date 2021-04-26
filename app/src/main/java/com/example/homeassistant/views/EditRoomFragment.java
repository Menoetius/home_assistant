package com.example.homeassistant.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homeassistant.R;
import com.example.homeassistant.helpers.DatabaseHelper;
import com.example.homeassistant.model.Room;
import com.example.homeassistant.model.RoomModel;
import com.example.homeassistant.viewmodels.MainViewModel;
import com.example.homeassistant.views.devices.LightFragment;

import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;


public class EditRoomFragment extends Fragment {
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;

    private DatabaseHelper db;
    private RoomModel roomModel;
    private Room room;
    private MainViewModel model;

    private ImageView ivSelected;
    private EditText etRoomName;
    private Button bCamera;
    private Button bGallery;
    private String roomId;
    private TextView tvRoomName;

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

        this.ivSelected = view.findViewById(R.id.ivSelected);
        this.etRoomName = view.findViewById(R.id.etChoosenRoomName);
        this.bCamera = view.findViewById(R.id.bCamera);
        this.bGallery = view.findViewById(R.id.bGallery);
        this.tvRoomName = view.findViewById(R.id.tvRoomName);

        if (roomModel != null) {
            etRoomName.setText(roomModel.getName());
            ivSelected.setImageBitmap(roomModel.getBackgroundImageAsBitmap());
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
        Bitmap image = ((BitmapDrawable) ivSelected.getDrawable()).getBitmap();
        if (roomModel == null) {
            roomModel = new RoomModel(-1, roomId, etRoomName.getText().toString(), image);
            db.addRoom(roomModel);
        } else {
            roomModel.setBackgroundImage(image);
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
                ivSelected.setImageBitmap(BitmapFactory.decodeStream(imageStream));
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