package com.example.myapplication;

/*
Copyright (c) 2017, Vuzix Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

*  Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

*  Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.

*  Neither the name of Vuzix Corporation nor the names of
   its contributors may be used to endorse or promote products derived
   from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient;


/**
 * Class to encapsulate all voice commands
 */
public class VoiceCmdReceiver  extends BroadcastReceiver {
    // Voice command substitutions. These substitutions are returned when phrases are recognized.
    // This is done by registering a phrase with a substitution. This eliminates localization issues
    // and is encouraged
    final String MATCH_A = "a";
    final String MATCH_B = "b";
    final String MATCH_C = "c";
    final String MATCH_D = "d";
    final String MATCH_E = "e";
    final String MATCH_F = "f";
    final String MATCH_G = "g";
    final String MATCH_H = "h";
    final String MATCH_I = "i";

    // Voice command custom intent names
    final String TOAST_EVENT = "other_toast";

    private MainActivity mMainActivity;

    public static boolean isRecognizerActive;

    /**
     * Constructor which takes care of all speech recognizer registration
     * @param iActivity MainActivity from which we are created
     */
    public VoiceCmdReceiver(MainActivity iActivity)
    {
        mMainActivity = iActivity;
        mMainActivity.registerReceiver(this, new IntentFilter(VuzixSpeechClient.ACTION_VOICE_COMMAND));

        try {
            // Create a VuzixSpeechClient from the SDK
            VuzixSpeechClient sc = new VuzixSpeechClient(iActivity);
            // Delete specific phrases. This is useful if there are some that sound similar to yours, but
            // you want to keep the majority of them intact
            //sc.deletePhrase("go home");
            //sc.deletePhrase("go back");

            // Delete every phrase in the dictionary! (Available in SDK version 1.3 and newer)
            //
            // Note! When developing applications on the Vuzix Blade and Vuzix M400, deleting all
            // phrases in the dictionary removes the wake-up word(s) and voice-off words. The M300
            // cannot change the wake-up word, so "hello vuzix" is unaffected by the deletePhrase call.
            sc.deletePhrase("*");

            // For Blade and M400, the wake-word can be modified. This call has no impact on the M300,
            // which ignores added/deleted wake words.
            //
            // Please always keep the wake word "hello vuzix" to prevent confusion. You can add additional
            // wake words specific to your application by imitating the code below
            try {
                sc.insertWakeWordPhrase("hello vuzix");      // Add-back the default phrase for consistency (Blade and M400 only)
                sc.insertWakeWordPhrase("hello sample app"); // Add application specific wake-up phrase (Blade and M400 only)
            } catch (NoSuchMethodError e) {
                Log.i(mMainActivity.LOG_TAG, "Setting wake words is not supported. It is introduced in M300 v1.6.6, Blade v2.6, and M400 v1.0.0");
            }

            try {
                // For all platforms, the voice-off phrase can be modified
                sc.insertVoiceOffPhrase("voice off");      // Add-back the default phrase for consistency
                sc.insertVoiceOffPhrase("privacy please"); // Add application specific stop listening phrase
            } catch (NoSuchMethodError e) {
                Log.i(mMainActivity.LOG_TAG, "Setting voice off is not supported. It is introduced in M300 v1.6.6, Blade v2.6, and M400 v1.0.0");
            }

            // Insert phrases for our broadcast handler
            //
            // ** NOTE **
            // The "s:" is required in the SDK version 1.2, but is not required in the latest JAR distribution
            // or SDK version 1.3.  But it is harmless when not required. It indicates that the recognizer is making a
            // substitution.  When the multi-word string is matched (in any language) the associated MATCH string
            // will be sent to the BroadcastReceiver
            sc.insertPhrase("a", MATCH_A);
            sc.insertPhrase("b", MATCH_B);
            sc.insertPhrase("c", MATCH_C);
            sc.insertPhrase("d", MATCH_D);
            sc.insertPhrase("e", MATCH_E);
            sc.insertPhrase("f", MATCH_F);
            sc.insertPhrase("g", MATCH_G);
            sc.insertPhrase("h", MATCH_H);
            sc.insertPhrase("i", MATCH_I);

            // See what we've done
            Log.i(mMainActivity.LOG_TAG, sc.dump());

            // The recognizer may not yet be enabled in Settings. We can enable this directly
            VuzixSpeechClient.EnableRecognizer(mMainActivity, true);
        } catch(NoClassDefFoundError e) {
            // We get this exception if the SDK stubs against which we compiled cannot be resolved
            // at runtime. This occurs if the code is not being run on a Vuzix device supporting the voice
            // SDK
            Log.e(mMainActivity.LOG_TAG, e.getMessage());
            e.printStackTrace();
            iActivity.finish();
        } catch (Exception e) {
            Log.e(mMainActivity.LOG_TAG, "Error setting custom vocabulary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * All custom phrases registered with insertPhrase() are handled here.
     *
     * Custom intents may also be directed here, but this example does not demonstrate this.
     *
     * Keycodes are never handled via this interface
     *
     * @param context Context in which the phrase is handled
     * @param intent Intent associated with the recognized phrase
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        // All phrases registered with insertPhrase() match ACTION_VOICE_COMMAND as do
        // recognizer status updates
        if (intent.getAction().equals(VuzixSpeechClient.ACTION_VOICE_COMMAND)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                // We will determine what type of message this is based upon the extras provided
                if (extras.containsKey(VuzixSpeechClient.PHRASE_STRING_EXTRA)) {
                    // If we get a phrase string extra, this was a recognized spoken phrase.
                    // The extra will contain the text that was recognized, unless a substitution
                    // was provided.  All phrases in this example have substitutions as it is
                    // considered best practice
                    String phrase = intent.getStringExtra(VuzixSpeechClient.PHRASE_STRING_EXTRA);

                    // Determine the specific phrase that was recognized and act accordingly
                    if (phrase.equals(MATCH_A)) {
                        if (mMainActivity.viewPager.getCurrentItem() == 14) {
                            MainActivity.res_diagnosis = 'A';
                            Diagnosis.final_diagnosis.setText("Migraña sin aura");
                            Toast.makeText(mMainActivity, "A", Toast.LENGTH_SHORT).show();
                            Log.i("Diagnosis", String.valueOf(MainActivity.res_diagnosis));
                        } else {
                            mMainActivity.answers[mMainActivity.viewPager.getCurrentItem() - 1] ='A';
                            Toast.makeText(mMainActivity, "A", Toast.LENGTH_SHORT).show();
                            // Log.i("Option", "A");
                            // Log.i("Position", String.format("%d", mMainActivity.viewPager.getCurrentItem()));
                            // Log.i("Answers", String.format("Results = %s", Arrays.toString(mMainActivity.answers)));
                        }
                    } else if (phrase.equals(MATCH_B)) {
                        if (mMainActivity.viewPager.getCurrentItem() == 14) {
                            MainActivity.res_diagnosis = 'B';
                            Diagnosis.final_diagnosis.setText("Migraña con aura");
                            Toast.makeText(mMainActivity, "B", Toast.LENGTH_SHORT).show();
                            Log.i("Diagnosis", String.valueOf(MainActivity.res_diagnosis));
                        } else {
                            mMainActivity.answers[mMainActivity.viewPager.getCurrentItem() - 1] ='B';
                            Toast.makeText(mMainActivity, "B", Toast.LENGTH_SHORT).show();
                            // Log.i("Option", "B");
                            // Log.i("Position", String.format("%d", mMainActivity.viewPager.getCurrentItem()));
                            // Log.i("Answers", String.format("Results = %s", Arrays.toString(mMainActivity.answers)));
                        }
                    } else if (phrase.equals(MATCH_C)) {
                        if (mMainActivity.viewPager.getCurrentItem() == 14) {
                            MainActivity.res_diagnosis = 'C';
                            Diagnosis.final_diagnosis.setText("Migraña crónica");
                            Toast.makeText(mMainActivity, "C", Toast.LENGTH_SHORT).show();
                            Log.i("Diagnosis", String.valueOf(MainActivity.res_diagnosis));
                        } else if (mMainActivity.viewPager.getCurrentItem() == 3) {
                            mMainActivity.answers[mMainActivity.viewPager.getCurrentItem() - 1] ='C';
                            Toast.makeText(mMainActivity, "C", Toast.LENGTH_SHORT).show();
                            // Log.i("Option", "C");
                            // Log.i("Position", String.format("%d", mMainActivity.viewPager.getCurrentItem()));
                            // Log.i("Answers", String.format("Results = %s", Arrays.toString(mMainActivity.answers)));
                        } else {
                            Toast.makeText(mMainActivity, "Alternativa incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else if (phrase.equals(MATCH_D)) {
                        if (mMainActivity.viewPager.getCurrentItem() == 14) {
                            MainActivity.res_diagnosis = 'D';
                            Diagnosis.final_diagnosis.setText("Cefalea tensional infrecuente");
                            Toast.makeText(mMainActivity, "D", Toast.LENGTH_SHORT).show();
                            Log.i("Diagnosis", String.valueOf(MainActivity.res_diagnosis));
                        } else if (mMainActivity.viewPager.getCurrentItem() == 3) {
                            mMainActivity.answers[mMainActivity.viewPager.getCurrentItem() - 1] ='D';
                            Toast.makeText(mMainActivity, "D", Toast.LENGTH_SHORT).show();
                            // Log.i("Option", "D");
                            // Log.i("Position", String.format("%d", mMainActivity.viewPager.getCurrentItem()));
                            // Log.i("Answers", String.format("Results = %s", Arrays.toString(mMainActivity.answers)));
                        } else {
                            Toast.makeText(mMainActivity, "Alternativa incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else if (phrase.equals(MATCH_E)) {
                        if (mMainActivity.viewPager.getCurrentItem() == 14) {
                            MainActivity.res_diagnosis = 'E';
                            Diagnosis.final_diagnosis.setText("Cefalea tensional frecuente");
                            Toast.makeText(mMainActivity, "E", Toast.LENGTH_SHORT).show();
                            Log.i("Diagnosis", String.valueOf(MainActivity.res_diagnosis));
                        } else if (mMainActivity.viewPager.getCurrentItem() == 3) {
                            mMainActivity.answers[mMainActivity.viewPager.getCurrentItem() - 1] ='E';
                            Toast.makeText(mMainActivity, "E", Toast.LENGTH_SHORT).show();
                            // Log.i("Option", "E");
                            // Log.i("Position", String.format("%d", mMainActivity.viewPager.getCurrentItem()));
                            // Log.i("Answers", String.format("Results = %s", Arrays.toString(mMainActivity.answers)));
                        } else {
                            Toast.makeText(mMainActivity, "Alternativa incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else if (phrase.equals(MATCH_F)) {
                        if (mMainActivity.viewPager.getCurrentItem() == 14) {
                            MainActivity.res_diagnosis = 'F';
                            Diagnosis.final_diagnosis.setText("Cefalea tensional crónica");
                            Toast.makeText(mMainActivity, "F", Toast.LENGTH_SHORT).show();
                            Log.i("Diagnosis", String.valueOf(MainActivity.res_diagnosis));
                        } else if (mMainActivity.viewPager.getCurrentItem() == 3) {
                            mMainActivity.answers[mMainActivity.viewPager.getCurrentItem() - 1] ='F';
                            Toast.makeText(mMainActivity, "F", Toast.LENGTH_SHORT).show();
                            // Log.i("Option", "F");
                            // Log.i("Position", String.format("%d", mMainActivity.viewPager.getCurrentItem()));
                            // Log.i("Answers", String.format("Results = %s", Arrays.toString(mMainActivity.answers)));
                        } else {
                            Toast.makeText(mMainActivity, "Alternativa incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else if (phrase.equals(MATCH_G)) {
                        if (mMainActivity.viewPager.getCurrentItem() == 14) {
                            MainActivity.res_diagnosis = 'G';
                            Diagnosis.final_diagnosis.setText("Cefalea en racimos episódica");
                            Toast.makeText(mMainActivity, "G", Toast.LENGTH_SHORT).show();
                            Log.i("Diagnosis", String.valueOf(MainActivity.res_diagnosis));
                            // Log.i("Option", "G");
                            // Log.i("Position", String.format("%d", mMainActivity.viewPager.getCurrentItem()));
                        } else {
                            Toast.makeText(mMainActivity, "Alternativa incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else if (phrase.equals(MATCH_H)) {
                        if (mMainActivity.viewPager.getCurrentItem() == 14) {
                            MainActivity.res_diagnosis = 'H';
                            Diagnosis.final_diagnosis.setText("Cefalea en racimos crónica");
                            Toast.makeText(mMainActivity, "H", Toast.LENGTH_SHORT).show();
                            Log.i("Diagnosis", String.valueOf(MainActivity.res_diagnosis));
                            // Log.i("Option", "H");
                            // Log.i("Position", String.format("%d", mMainActivity.viewPager.getCurrentItem()));
                        } else {
                            Toast.makeText(mMainActivity, "Alternativa incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else if (phrase.equals(MATCH_I)) {
                        if (mMainActivity.viewPager.getCurrentItem() == 14) {
                            MainActivity.res_diagnosis = 'I';
                            Diagnosis.final_diagnosis.setText("No deteminado");
                            Toast.makeText(mMainActivity, "I", Toast.LENGTH_SHORT).show();
                            Log.i("Diagnosis", String.valueOf(MainActivity.res_diagnosis));
                            // Log.i("Option", "I");
                            // Log.i("Position", String.format("%d", mMainActivity.viewPager.getCurrentItem()));
                        } else {
                            Toast.makeText(mMainActivity, "Alternativa incorrecta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mMainActivity, "Alternativa incorrecta", Toast.LENGTH_SHORT).show();
                    }

                } else if (extras.containsKey(VuzixSpeechClient.RECOGNIZER_ACTIVE_BOOL_EXTRA)) {
                    // if we get a recognizer active bool extra, it means the recognizer was
                    // activated or stopped
                    isRecognizerActive = extras.getBoolean(VuzixSpeechClient.RECOGNIZER_ACTIVE_BOOL_EXTRA, false);
                    mMainActivity.RecognizerChangeCallback(isRecognizerActive);
                } else {
                    Log.e(mMainActivity.LOG_TAG, "Voice Intent not handled");
                }
            }
        }
        else {
            Log.e(mMainActivity.LOG_TAG, "Other Intent not handled " + intent.getAction() );
        }
    }

    /**
     * Called to unregister for voice commands. An important cleanup step.
     */
    public void unregister() {
        try {
            mMainActivity.unregisterReceiver(this);
            Log.i(mMainActivity.LOG_TAG, "Custom vocab removed");
            mMainActivity = null;
        }catch (Exception e) {
            Log.e(mMainActivity.LOG_TAG, "Custom vocab died " + e.getMessage());
        }
    }

    /**
     * Handler called when "Listen" button is clicked. Activates the speech recognizer identically to
     * saying "Hello Vuzix"
     *
     * @param bOnOrOff boolean True to enable listening, false to cancel it
     */
    public void TriggerRecognizerToListen(boolean bOnOrOff) {
        try {
            VuzixSpeechClient.TriggerVoiceAudio(mMainActivity, bOnOrOff);
        } catch (NoClassDefFoundError e) {
            // The voice SDK was added in version 1.2. The constructor will have failed if the
            // target device is not a Vuzix device that is compatible with SDK version 1.2.  But the
            // trigger command with the bool was added in SDK version 1.4.  It is possible the Vuzix
            // device does not yet have the TriggerVoiceAudio interface. If so, we get this exception.
            //Toast.makeText(mMainActivity, R.string.upgrade_vuzix, Toast.LENGTH_LONG).show();
        }
    }

}

