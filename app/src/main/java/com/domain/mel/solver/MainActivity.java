package com.domain.mel.solver;

import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.domain.mel.solver.views.BoardView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ListView mainListView;
    private EditText mainEditText;
    private FloatingActionButton fab;
    private BoardView boardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.fab = findViewById(R.id.fab);
        this.mainListView = findViewById(R.id.mainListView);
        this.mainEditText = findViewById(R.id.mainEditText);
        this.boardView = findViewById(R.id.board);

        // Hide keyboard, prevent edit text from forcing keyboard open on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Board b = new Board("abcdefghijklmnoqu");
                    boardView.update(b);
                } catch (Exception e) {}

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            this.createHelpDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createHelpDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.help_dialog);
        ImageButton helpDismissButton = dialog.findViewById(R.id.helpDismissButton);

        helpDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }





}
