package com.domain.mel.solver.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.domain.mel.solver.Board;
import com.domain.mel.solver.R;

import java.util.ArrayList;


public class BoardView extends View {

    private static final String TAG = "BoardView";
    private static final int PADDING = 5;
    private static final int BORDER_PADDING = 10;
    private Paint paint;
    private ArrayList<Rect> dices;
    private boolean[] isHighlightedLetter;
    private String[] letters;
    private int size;

    public BoardView(Context context) {
        super(context);
        init(null);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private float dpToPixels(int dpValue) {
        Resources r = getResources();
        return TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP,
                dpValue, r.getDisplayMetrics()
        );
    }


    private void init(@Nullable AttributeSet set) {

        this.paint = new Paint();
        this.dices = new ArrayList<>();
        this.letters = new String[Board.DIMENSION * Board.DIMENSION];
        this.isHighlightedLetter = new boolean[this.letters.length];

        for (int diceCount = 0; diceCount < (Board.DIMENSION * Board.DIMENSION); diceCount++) {
            this.dices.add(new Rect());
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width > height)
            size = height;
        else
            size = width;

        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int padding = (int) this.dpToPixels(PADDING);
        int paddingBorder = (int) this.dpToPixels(BORDER_PADDING) / 2;

        Rect curDice;
        int diceSize = this.size / Board.DIMENSION;
        int diceRow;
        int diceCol;

        int textX;
        int textY;
        int textSize = (int) this.dpToPixels(
                (this.size / (Board.DIMENSION * Board.DIMENSION))
        );

        for (int i = 0; i < (Board.DIMENSION * Board.DIMENSION); i++) {

            diceRow = i / Board.DIMENSION;
            diceCol = i % Board.DIMENSION;

            curDice = dices.get(i);

            curDice.top = (diceRow * diceSize) +
                    ((diceRow == 0) ? paddingBorder : padding);
            curDice.bottom = (diceRow * diceSize) + diceSize -
                    ((diceRow == Board.DIMENSION - 1) ? paddingBorder : 0);

            curDice.left = (diceCol * diceSize) +
                    ((diceCol == 0) ? paddingBorder : padding);
            curDice.right = (diceCol * diceSize) + diceSize -
                    ((diceCol == Board.DIMENSION - 1) ? paddingBorder : 0);

            this.paint.setColor(Color.LTGRAY);
            canvas.drawRect(curDice, this.paint);

            if (this.letters[i] != null ) {

                if (this.isHighlightedLetter[i])
                    this.paint.setColor(getResources().getColor(R.color.colorAccent));
                else
                    this.paint.setColor(Color.BLACK);

                this.paint.setTextSize(textSize);
                this.paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(this.letters[i].toUpperCase(),
                        curDice.centerX(),
                        curDice.bottom - (curDice.height() / 4),
                        this.paint);
            }

        }

    }

    public void update(Board board) {
        this.letters = board.toStringArray();
        this.isHighlightedLetter = new boolean[this.letters.length];
        invalidate();
    }

    public void highlightWord(Board.CoOrd[] coOrds) {
        for (Board.CoOrd coOrd : coOrds) {
            this.isHighlightedLetter[coOrd.toIndex()] = true;
            Log.d(TAG, coOrd.toIndex() + " - " );
        }
        invalidate();
    }


}
