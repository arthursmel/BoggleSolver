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


/**
 * TODO score of words
 * TODO List adapter
 * TODO asynctask for solving
 */
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

        Board b;
        try {
            final String input = "nbweefghijklmnoqu";


            b = new Board(input);
            boardView.update(b);

            final Solver s = new Solver(this);

            final Board.CoOrd[] path = s.getCoOrdPath(input,"qulhewfj");
            boardView.highlightWord(path);

            this.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {




                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Hide keyboard, prevent edit text from forcing keyboard open on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



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
