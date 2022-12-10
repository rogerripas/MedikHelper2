package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Start extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        if (VoiceCmdReceiver.isRecognizerActive == true) {
            MainActivity.activateListener();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (VoiceCmdReceiver.isRecognizerActive == true) {
            MainActivity.activateListener();
        }
    }
}