package com.domain.mel.solver.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.domain.mel.solver.Board;
import com.domain.mel.solver.R;

import java.util.ArrayList;

/**
 * A custom view to represent the boggle board graphically
 */

public class BoardView extends View {

    private static final String TAG = "BoardView";

    private static final int PADDING = 5; // Dimension for between dices (dp)
    private static final int BORDER_PADDING = 10; // Dimension for edges of view (dp)
    private int size; // Current pixel dimension of the view itself

    // Canvas objects
    private Paint paint;
    private ArrayList<Rect> diceRects;

    // Array of which dice co-ords should be joined on the board by a line (to show path of word)
    private Board.CoOrd[] isSelectedLetter;
    private static final float STROKE_WIDTH = 20f; // stroke width for the path
    private static final int ALPHA_DEFAULT = 255;
    private static final int ALPHA_PATH = 200; // Alpha value for the path

    private String[] letters; // Letters on each dice in their 1D positions

    /**
     * Constructor
     */
    public BoardView(Context context) {
        super(context);
        init(null);
    }

    /**
     * Constructor
     */
    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Constructor
     */
    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * Constructor
     */
    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * Converts density pixel value to the corresponding pixel value
     * @param dpValue the dp value to convert
     * @return the corresponding pixel value
     */
    private float dpToPixels(int dpValue) {
        Resources r = getResources();
        return TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP,
                dpValue, r.getDisplayMetrics()
        );
    }

    /**
     * Init called before onMeasure & onDraw
     * Initialises class object variables
     */
    private void init(@Nullable AttributeSet set) {

        // Initialising array that will contain the letters of the current dices
        this.letters = new String[Board.DIMENSION * Board.DIMENSION];
        // Initialising array which sets highlighted state of each letter
        this.isSelectedLetter = new Board.CoOrd[0];

        // Creating canvas objects
        this.paint = new Paint();
        this.diceRects = new ArrayList<>(); // Contains rects which will be drawn on the canvas

        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(STROKE_WIDTH);
        this.paint.setAlpha(ALPHA_DEFAULT);

        for (int diceCount = 0; diceCount < (Board.DIMENSION * Board.DIMENSION); diceCount++) {
            // For each dice, add a new rect to the array
            // The position of the dice will be updated during draw
            this.diceRects.add(new Rect());
        }

    }

    /**
     * Used to get the current dimension and set the view
     * to be a square
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Get the current width & height
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        // Get the smallest dimension
        // and make it the dimension of
        // the squares side
        if (width > height)
            this.size = height;
        else
            this.size = width;

        // Set the view to be a square
        setMeasuredDimension(this.size, this.size);
    }

    /**
     * Called each time the layout is refreshed
     * Draws the 16 dice on the view
     * Draws the path for any selected dice
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Converting the padding values from dp into the pixel values
        int padding = (int) this.dpToPixels(PADDING); // Padding between dice
        // Border padding is half the padding between the dice
        // and places all around board
        int paddingBorder = (int) this.dpToPixels(BORDER_PADDING) / 2;

        // Rect object dimensions
        Rect curDiceRect;
        int diceSize = this.size / Board.DIMENSION;
        int diceRow;
        int diceCol;

        // Text size is the size of the board divided by the number of dice
        int textSize = (int) this.dpToPixels(
                (this.size / (Board.DIMENSION * Board.DIMENSION))
        );

        for (int i = 0; i < (Board.DIMENSION * Board.DIMENSION); i++) {
            // For each dice rect

            // 1) Drawing each rectangle

            diceRow = i / Board.DIMENSION; // current row of rect
            diceCol = i % Board.DIMENSION; // current col of rect

            curDiceRect = diceRects.get(i);

            // Setting the dimensions of the current rect object
            curDiceRect.top = (diceRow * diceSize) +
                    // If the dice rect is on the edge of the view
                    // include the border padding
                    ((diceRow == 0) ? paddingBorder : padding);
            curDiceRect.bottom = (diceRow * diceSize) + diceSize -
                    ((diceRow == Board.DIMENSION - 1) ? paddingBorder : 0);

            curDiceRect.left = (diceCol * diceSize) +
                    ((diceCol == 0) ? paddingBorder : padding);
            curDiceRect.right = (diceCol * diceSize) + diceSize -
                    ((diceCol == Board.DIMENSION - 1) ? paddingBorder : 0);

            // Setting paint object params for rect
            this.paint.setColor(Color.LTGRAY);
            this.paint.setAlpha(ALPHA_DEFAULT);
            canvas.drawRect(curDiceRect, this.paint);

            // 2) Drawing each letter for the dice

            if (this.letters[i] != null ) {
                // If there have been letter given to draw

                // Setting paint object params for text
                this.paint.setColor(Color.BLACK);
                this.paint.setTextSize(textSize);
                this.paint.setTextAlign(Paint.Align.CENTER);

                canvas.drawText(this.letters[i].toUpperCase(),
                        // X value is the center of the corresponding dice rect
                        // as the text aligns center
                        curDiceRect.centerX(),
                        // Y value is 1/4 way up from the bottom of the dice
                        // to prevent text from clipping off the top of the rect
                        curDiceRect.bottom - (curDiceRect.height() / 4),
                        this.paint);

            }
        }

        // 3) Drawing any path lines

        // Co-ords of the path lines start and ends
        Board.CoOrd curCoOrd;
        Board.CoOrd nextCoOrd;

        // Rects to draw the lines between
        Rect curSelectedRect;
        Rect nextSelectedRect;

        // Setting paint object params for path lines
        this.paint.setColor(getResources().getColor(R.color.colorAccent));
        this.paint.setAlpha(ALPHA_PATH);

        for (int i = 0; i < this.isSelectedLetter.length - 1; i++) {
            // For each selected dice

            // Get co-ords to draw the line from and to
            curCoOrd = this.isSelectedLetter[i];
            nextCoOrd = this.isSelectedLetter[i + 1];

            curSelectedRect = this.diceRects.get(curCoOrd.toIndex());
            nextSelectedRect = this.diceRects.get(nextCoOrd.toIndex());

            canvas.drawLine(curSelectedRect.centerX(), curSelectedRect.centerY(),
                            nextSelectedRect.centerX(), nextSelectedRect.centerY(),
                            this.paint);
        }

    }

    /**
     * Draws a new board representing graphically the board object given
     * @param board the board to be drawn in the view
     */
    public void update(Board board) {
        this.letters = board.toStringArray(); // Updating letters
        this.isSelectedLetter = new Board.CoOrd[0]; // Clearing any paths
        invalidate(); // Redrawing
    }

    /**
     * Creates a path between dice to display the word solution
     * to the user
     * @param coOrds the co-ords of the dice involved in the word solution
     */
    public void highlightWord(Board.CoOrd[] coOrds) {
        this.isSelectedLetter = coOrds; // Creating any paths
        invalidate(); // Redrawing
    }

    /**
     * Removes any word paths from the board
     */
    public void deHighlightWord() {
        this.isSelectedLetter = new Board.CoOrd[0]; // Clearing any paths
        invalidate(); // Redrawing
    }


}
