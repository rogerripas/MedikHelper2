package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Diagnosis extends Fragment {
    private View view;
    public static TextView final_diagnosis;
    public static String final_diagnosis_name;

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
                final_diagnosis_name = "Migraña sin aura";
                break;
            case 'B':
                final_diagnosis.setText("Migraña con aura");
                final_diagnosis_name = "Migraña con aura";
                break;
            case 'C':
                final_diagnosis.setText("Migraña crónica");
                final_diagnosis_name = "Migraña crónica";
                break;
            case 'D':
                final_diagnosis.setText("Cefalea tensional infrecuente");
                final_diagnosis_name = "Cefalea tensional infrecuente";
                break;
            case 'E':
                final_diagnosis.setText("Cefalea tensional frecuente");
                final_diagnosis_name = "Cefalea tensional frecuente";
                break;
            case 'F':
                final_diagnosis.setText("Cefalea tensional crónica");
                final_diagnosis_name = "Cefalea tensional crónica";
                break;
            case 'G':
                final_diagnosis.setText("Cefalea en racimos episódica");
                final_diagnosis_name = "Cefalea en racimos episódica";
                break;
            case 'H':
                final_diagnosis.setText("Cefalea en racimos crónica");
                final_diagnosis_name = "Cefalea en racimos crónica";
                break;
            case 'I':
                final_diagnosis.setText("No determinado");
                final_diagnosis_name = "No determinado";
                break;
        }

        addDiagnosis();
    }

    public static void addDiagnosis() {
        // Temporal random final_diagnosis_id
        int min1 = 10;
        int max1 = 50;
        int fin_diag_id = (int)Math.floor(Math.random()*(max1-min1+1)+min1);
        // Temporal random patient_document_number
        int min2 = 10000000;
        int max2 = 99999999;
        int pat_doc_num = (int)Math.floor(Math.random()*(max2-min2+1)+min2);
        // Temporal random appointment_id
        int min3 = 10;
        int max3 = 50;
        int app_id = (int)Math.floor(Math.random()*(max3-min3+1)+min3);


        new Thread(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(MainActivity.url, MainActivity.username, MainActivity.password);
                Statement statement = connection.createStatement();
                // add to RDS DB:

                statement.execute("INSERT INTO " + MainActivity.TABLE_NAME + "(final_diagnosis_id, patient_document_number, appointment_id, final_diagnosis) VALUES('"+ fin_diag_id + "', '" + pat_doc_num + "', '" + app_id + "', '" + final_diagnosis_name + "')");

                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}