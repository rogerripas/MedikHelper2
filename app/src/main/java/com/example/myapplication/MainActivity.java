package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;
import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {

    public static ViewPager viewPager;
    public static char [] answers = {'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A', 'A'} ;
    public static char res_diagnosis = 'A';

    public final String LOG_TAG = "VoiceSample";
    public final String CUSTOM_SDK_INTENT = "com.vuzix.sample.vuzix_voicecontrolwithsdk.CustomIntent";
    public static VoiceCmdReceiver mVoiceCmdReceiver;
    public static boolean mRecognizerActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);

        Adapter adapter = new Adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new Start());
        adapter.addFragment(new Question01());
        adapter.addFragment(new Question02());
        adapter.addFragment(new Question03());
        adapter.addFragment(new Question04());
        adapter.addFragment(new Question05());
        adapter.addFragment(new Question06());
        adapter.addFragment(new Question07());
        adapter.addFragment(new Question08());
        adapter.addFragment(new Question09());
        adapter.addFragment(new Question10());
        adapter.addFragment(new Question11());
        adapter.addFragment(new Question12());
        adapter.addFragment(new Summary());
        adapter.addFragment(new Diagnosis());
        viewPager.setAdapter(adapter);

        // Create the voice command receiver class
        mVoiceCmdReceiver = new VoiceCmdReceiver(this);

        // Now register another intent handler to demonstrate intents sent from the service
        myIntentReceiver = new MyIntentReceiver();
        registerReceiver(myIntentReceiver , new IntentFilter(CUSTOM_SDK_INTENT));

    }

    public static void activateListener(){
        mRecognizerActive = !mRecognizerActive;
        mVoiceCmdReceiver.TriggerRecognizerToListen(mRecognizerActive);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == event.KEYCODE_DPAD_CENTER){
            switch (MainActivity.viewPager.getCurrentItem()) {
                case 0:
                case 13:
                    break;
                default:
                    // Log.i("Info","Mic On");
                    activateListener();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Unregister from the speech SDK
     */
    @Override
    protected void onDestroy() {
        mVoiceCmdReceiver.unregister();
        unregisterReceiver(myIntentReceiver);
        super.onDestroy();
    }

    /**
     * Utility to get the name of the current method for logging
     * @return String name of the current method
     */
    public String getMethodName() {
        return LOG_TAG + ":" + this.getClass().getSimpleName() + "." + new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * Helper to show a toast
     * @param iStr String message to place in toast
     */
    private void popupToast(String iStr) {
        Toast myToast = Toast.makeText(MainActivity.this, iStr, Toast.LENGTH_LONG);
        myToast.show();
    }

    /**
     * A callback for the SDK to notify us if the recognizer starts or stop listening
     *
     * @param isRecognizerActive boolean - true when listening
     */
    public void RecognizerChangeCallback(boolean isRecognizerActive) {
        Log.d(LOG_TAG, getMethodName());
        mRecognizerActive = isRecognizerActive;
    }

    /**
     * You may prefer using explicit intents for each recognized phrase. This receiver demonstrates that.
     */
    private MyIntentReceiver  myIntentReceiver;

    public class MyIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(LOG_TAG, getMethodName());
            Toast.makeText(context, "Custom Intent Detected", Toast.LENGTH_LONG).show();
        }
    }

}