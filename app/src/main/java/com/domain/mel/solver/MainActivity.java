package com.domain.mel.solver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.domain.mel.solver.views.BoardView;

import java.io.IOException;


/**
 * TODO score of words
 * TODO List adapter
 * TODO asynctask for solving
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int BOARD_WIDTH_DP_LANDSCAPE = 200;
    private static final String DICTIONARY_URL = "http://www.dictionary.com/browse/";

    private ListView mainListView;
    private EditText mainEditText;
    private FloatingActionButton fab;
    private BoardView boardView;

    private Solver solver;
    private Board board;
    private String input;

    private String[] foundWords;
    private int[] foundWordsScore;


    Thread createSolverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.mainListView = findViewById(R.id.mainListView);
        this.mainEditText = findViewById(R.id.mainEditText);
        this.boardView = findViewById(R.id.board);
        this.fab = findViewById(R.id.fab);

        final ListAdapter.ListAdapterListener listViewListener = new ListAdapter.ListAdapterListener() {
            @Override
            public void onSearch(int position) {
                // Open browser with definition of the word clicked
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(DICTIONARY_URL + foundWords[position]));
                startActivity(browserIntent);
            }

            @Override
            public void onClick(int position) {

                try {
                    Board.CoOrd[] coOrds = solver.getCoOrdPath(input, foundWords[position]);
                    boardView.highlightWord(coOrds);
                } catch (Board.InvalidBoardException exception) {
                    Toast.makeText(getApplicationContext(),
                            "Invalid input",
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onCheck(int position) {
                Log.d(TAG, "onCheck");
            }
        };


        this.fab.hide();
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    // Hides keyboard automatically when fab clicked
                    hideKeyboard();

                    input = mainEditText.getText()
                            .toString()
                            .toLowerCase();
                    board = new Board(input);

                    boardView.update(board);
                    foundWords = solver.getAllWords(input);

                    mainListView.setAdapter(
                            new ListAdapter(getApplicationContext(),
                                    foundWords,
                                    new int[foundWords.length],
                                    listViewListener)
                    );


                } catch (Board.InvalidBoardException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Invalid Board",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    solver = new Solver(getApplicationContext());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fab.show();
                        }
                    });

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),
                            "Unreadable Dictionary File",
                            Toast.LENGTH_SHORT)
                            .show();
                } catch (Dictionary.InvalidDictionaryException e) {
                    Toast.makeText(getApplicationContext(),
                            "Invalid Dictionary File",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }).start();









        /*
        try {
            final String input = "nbweefghijklmnoqu";


            b = new Board(input);
            boardView.update(b);

            final Solver s = new Solver(this);

            final Board.CoOrd[] path = s.getCoOrdPath(input,"qulhewfj");
            boardView.highlightWord(path);



        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // Hide keyboard, prevent edit text from forcing keyboard open on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);



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

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow((
                null == getCurrentFocus()) ?
                null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Called when orientation changed
     * Formats the layout better for landscape
     */
    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        RelativeLayout boardWrapper = findViewById(R.id.boardWrapper);
        RelativeLayout.LayoutParams boardWrapperLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        EditText mainEditText = findViewById(R.id.mainEditText);
        RelativeLayout.LayoutParams mainEditTextLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        BoardView board = findViewById(R.id.board);
        RelativeLayout infoWrapper = findViewById(R.id.infoWrapper);
        RelativeLayout.LayoutParams infoWrapperLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        ListView mainListView = findViewById(R.id.mainListView);
        RelativeLayout.LayoutParams mainListViewLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

            boardWrapperLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            boardWrapperLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            boardWrapperLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            boardWrapperLayoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    BOARD_WIDTH_DP_LANDSCAPE,
                    getResources().getDisplayMetrics());
            boardWrapper.bringToFront();

            mainEditTextLayoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dpWidth - BOARD_WIDTH_DP_LANDSCAPE
                    , getResources().getDisplayMetrics());
            mainEditTextLayoutParams.height = mainEditText.getHeight();
            mainEditTextLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mainEditTextLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

            infoWrapperLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            infoWrapperLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            infoWrapperLayoutParams.addRule(RelativeLayout.BELOW, board.getId());

            mainListViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mainListViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mainListViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, boardWrapper.getId());
            mainListViewLayoutParams.addRule(RelativeLayout.ABOVE, mainEditText.getId());

        } else {

            boardWrapperLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            boardWrapperLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            mainEditTextLayoutParams.height = mainEditText.getHeight();
            mainEditTextLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mainEditTextLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mainEditTextLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            infoWrapperLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            infoWrapperLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            infoWrapperLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, board.getId());
            infoWrapperLayoutParams.addRule(RelativeLayout.RIGHT_OF, board.getId());

            mainListViewLayoutParams.addRule(RelativeLayout.BELOW, boardWrapper.getId());
            mainListViewLayoutParams.addRule(RelativeLayout.ABOVE, mainEditText.getId());

        }

        boardWrapper.setLayoutParams(boardWrapperLayoutParams);
        mainEditText.setLayoutParams(mainEditTextLayoutParams);
        infoWrapper.setLayoutParams(infoWrapperLayoutParams);
        mainListView.setLayoutParams(mainListViewLayoutParams);

    }




}
