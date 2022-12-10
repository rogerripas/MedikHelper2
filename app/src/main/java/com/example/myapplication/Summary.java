package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Summary extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_summary, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (VoiceCmdReceiver.isRecognizerActive == true) {
            MainActivity.activateListener();
        }

        update_summary();
    }

    private void update_summary(){
        if (MainActivity.answers[0] == 'B'){
            TextView tv_duration = (TextView) view.findViewById(R.id.answer_duration);
            tv_duration.setText("NO");
        }

        if (MainActivity.answers[1] == 'B'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_location);
            tv_location.setText("NO");
        }

        TextView tv_type = (TextView) view.findViewById(R.id.answer_type);
        switch(MainActivity.answers[2]) {
            case 'A':
                tv_type.setText("Pulsátil");
                break;
            case 'B':
                tv_type.setText("Palpitante");
                break;
            case 'C':
                tv_type.setText("Opresivo");
                break;
            case 'D':
                tv_type.setText("Punzante");
                break;
            case 'E':
                tv_type.setText("Lancinante");
                break;
            case 'F':
                tv_type.setText("Penetrante");
                break;
        }

        if (MainActivity.answers[3] == 'B'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_frequency_15);
            tv_location.setText("NO");
        }

        if (MainActivity.answers[4] == 'B'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_frequency_1);
            tv_location.setText("NO");
        }

        if (MainActivity.answers[5] == 'B'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_exercise);
            tv_location.setText("NO");
        }

        if (MainActivity.answers[6] == 'B'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_photophobia);
            tv_location.setText("NO");
        }

        if (MainActivity.answers[7] == 'B'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_phonophobia);
            tv_location.setText("NO");
        }

        if (MainActivity.answers[8] == 'B'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_aura);
            tv_location.setText("NO");
        }

        if (MainActivity.answers[9] == 'B'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_autonomic);
            tv_location.setText("NO");
        }

        if (MainActivity.answers[10] == 'B'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_alcohol);
            tv_location.setText("NO");
        }

        if (MainActivity.answers[11] == 'A'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_remission);
            tv_location.setText("SI");
        } else if (MainActivity.answers[11] == 'B'){
            TextView tv_location = (TextView) view.findViewById(R.id.answer_remission);
            tv_location.setText("NO");
        }

        // TextView tv = (TextView) view.findViewById(R.id.text_test);
        // tv.setText(Arrays.toString(MainActivity.answers));

        diagnosis(MainActivity.answers);
    }

    private void diagnosis(char [] answers){
        answers = MainActivity.answers;
        char freq_greater_1 = 'A';
        freq_greater_1 = answers[4];
        char freq_greater_15 = 'A';
        freq_greater_15 = answers[3];
        char dur_smaller_180 = 'A';
        dur_smaller_180 = answers[0];
        char bilateral_loc = 'A';
        bilateral_loc = answers[1];

        char type_pain = 'A';
        if (answers[2] == 'A' || answers[2] == 'B'){
            type_pain = 'A';
        } else if (answers[2] == 'C' || answers[2] == 'D'){
            type_pain = 'B';
        } else if (answers[2] == 'E' || answers[2] == 'F'){
            type_pain = 'C';
        }

        char photo_and_phono = 'A';
        if (answers[6] == 'A' && answers[7] == 'A'){
            photo_and_phono = 'A';
        } else {
            photo_and_phono = 'B';
        }

        char photo_or_phono = 'A';
        if (answers[6] == 'A' || answers[7] == 'A'){
            photo_or_phono = 'A';
        } else {
            photo_or_phono = 'B';
        }

        char exercise_trig = 'A';
        exercise_trig = answers[5];
        char aura_sympt = 'A';
        aura_sympt = answers[8];
        char autonomic_sympt = 'A';
        autonomic_sympt = answers[9];
        char alcohol_trig = 'A';
        alcohol_trig = answers[10];
        char remission = 'A';
        remission = answers[11];

        MainActivity.res_diagnosis = calculate(freq_greater_1, freq_greater_15, dur_smaller_180, bilateral_loc, type_pain, photo_and_phono, photo_or_phono, exercise_trig, aura_sympt, autonomic_sympt, alcohol_trig, remission);

        Log.i("Diagnosis", String.valueOf(MainActivity.res_diagnosis));
    }

    private char calculate(char freq_greater_1, char freq_greater_15, char dur_smaller_180, char bilateral_loc, char type_pain, char photo_and_phono, char photo_or_phono, char exercise_trig, char aura_sympt, char autonomic_sympt, char alcohol_trig, char remission){
        char res = 'A';

        if (dur_smaller_180 == 'A'){
            if (bilateral_loc == 'A'){
                res = common_branch(exercise_trig, type_pain, photo_and_phono, freq_greater_15, aura_sympt, photo_or_phono, freq_greater_1);
            } else {
                if (autonomic_sympt == 'A'){
                    if (alcohol_trig == 'A'){
                        if (type_pain == 'C'){
                            if (remission == 'A'){
                                res = 'G';
                            } else {
                                res = 'H';
                            }
                        } else {
                            res = 'I';
                        }
                    } else {
                        res = 'I';
                    }
                } else {
                    res = 'I';
                }
            }
        } else {
            if (bilateral_loc == 'A'){
                res = common_branch(exercise_trig, type_pain, photo_and_phono, freq_greater_15, aura_sympt, photo_or_phono, freq_greater_1);
            } else {
                if (exercise_trig == 'A'){
                    if (type_pain == 'A'){
                        if (photo_and_phono == 'A'){
                            if (freq_greater_15 == 'A'){
                                res = 'C';
                            } else {
                                if (aura_sympt == 'A'){
                                    res = 'B';
                                } else {
                                    res = 'A';
                                }
                            }
                        } else {
                            res = 'I';
                        }
                    } else {
                        res = 'I';
                    }
                } else {
                    res = 'I';
                }
            }
        }

        return res;
    }

    private char common_branch(char exercise_trig, char type_pain, char photo_and_phono, char freq_greater_15, char aura_sympt, char photo_or_phono, char freq_greater_1){
        char res = 'A';

        if (exercise_trig == 'A'){
            if (type_pain == 'A'){
                if (photo_and_phono == 'A'){
                    if (freq_greater_15 == 'A'){
                        res = 'C';
                    } else {
                        if (aura_sympt == 'A'){
                            res = 'B';
                        } else {
                            res = 'A';
                        }
                    }
                } else {
                    res = 'I';
                }
            } else {
                res = 'I';
            }
        } else {
            if (type_pain == 'B'){
                if (photo_or_phono == 'A'){
                    if (freq_greater_15 == 'A'){
                        res = 'F';
                    } else {
                        if (freq_greater_1 == 'A'){
                            res = 'E';
                        } else {
                            res = 'D';
                        }
                    }
                } else {
                    res = 'I';
                }
            } else {
                res = 'I';
            }
        }
        return res;
    }
}