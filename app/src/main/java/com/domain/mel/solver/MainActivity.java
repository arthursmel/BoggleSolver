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
            Solver solver = new Solver(this);
            Board.CoOrd[] a = solver.getCoOrdPath("qwerpyuiopasdfgh", "payer");

            for (Board.CoOrd c : a) {
                Log.d(TAG, c.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
