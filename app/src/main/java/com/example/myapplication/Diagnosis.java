package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Diagnosis extends Fragment {
    private View view;
    public static TextView final_diagnosis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_diagnosis, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        final_diagnosis = (TextView) view.findViewById(R.id.final_diagnosis);

        switch(MainActivity.res_diagnosis) {
            case 'A':
                final_diagnosis.setText("Migraña sin aura");
                break;
            case 'B':
                final_diagnosis.setText("Migraña con aura");
                break;
            case 'C':
                final_diagnosis.setText("Migraña crónica");
                break;
            case 'D':
                final_diagnosis.setText("Cefalea tensional infrecuente");
                break;
            case 'E':
                final_diagnosis.setText("Cefalea tensional frecuente");
                break;
            case 'F':
                final_diagnosis.setText("Cefalea tensional crónica");
                break;
            case 'G':
                final_diagnosis.setText("Cefalea en racimos episódica");
                break;
            case 'H':
                final_diagnosis.setText("Cefalea en racimos crónica");
                break;
            case 'I':
                final_diagnosis.setText("No determinado");
                break;
        }
    }
}