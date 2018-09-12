package com.domain.mel.solver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {


            Dictionary d = new Dictionary(this);


            Log.d(TAG, "1. " + d.isWord("apple"));
            Log.d(TAG, "2. " + d.isWord("tea"));
            Log.d(TAG, "2. " + d.isWord("teas"));
            Log.d(TAG, "3. " + d.isWord("te"));
            Log.d(TAG, "4. " + d.isWord("teae"));

            Log.d(TAG, "1. " + d.isPartialWord("apple"));
            Log.d(TAG, "2. " + d.isPartialWord("tea"));
            Log.d(TAG, "2. " + d.isPartialWord("teas"));
            Log.d(TAG, "3. " + d.isPartialWord("te"));
            Log.d(TAG, "4. " + d.isPartialWord("teae"));



        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
