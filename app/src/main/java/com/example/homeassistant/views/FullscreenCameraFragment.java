package com.example.homeassistant.views;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.homeassistant.R;
import com.longdo.mjpegviewer.MjpegView;

public class FullscreenCameraFragment extends Fragment {
    private MjpegView viewer;
    View view;
    private ImageView image;

    public FullscreenCameraFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fullscreen_camera, container, false);
        assert getArguments() != null;
        String imageUrl = FullscreenCameraFragmentArgs.fromBundle(getArguments()).getImageUrl();
        String streamUrl = FullscreenCameraFragmentArgs.fromBundle(getArguments()).getStreamUrl();

        ((MainActivity) getActivity()).setBottomNavigationVisibility(View.INVISIBLE);

        ImageView ibBack = view.findViewById(R.id.ivFullscreenBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_fullscreenCameraFragment_to_HomeFragment);
                ((MainActivity) getActivity()).setBottomNavigationVisibility(View.VISIBLE);
            }
        });

        image = view.findViewById(R.id.ivImage);
        Glide.with(view.getContext()).load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(0.5f)
                .skipMemoryCache(true)
                .into(image);

        viewer = view.findViewById(R.id.vvVideo);
        viewer.setMode(MjpegView.MODE_FIT_WIDTH);
        viewer.setAdjustHeight(true);
        viewer.setUrl(streamUrl);
        viewer.startStream();

        return view;
    }

    @Override
    public void onDestroyView() {
        viewer.stopStream();
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        image.setVisibility(View.INVISIBLE);
        super.onConfigurationChanged(newConfig);
    }
}