package com.domain.mel.solver;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */

public class Solver {

    private static final String TAG = "Solver";

    public Solver(String boardLetters) {

        Board b = new Board(boardLetters);

        for (Dice d : b) {
            Log.d(TAG, "Dice: " + b.getDiceCoOrd(d));
        }

    }



    public CoOrd getCoOrdPath(String foundWord) {
        return null;
    }

    public String[] getAllAnswers(String boardLetters) {
        return null;
    }



    private class CoOrd {

        private int row;
        private int col;

        CoOrd(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() { return row; }
        public int getCol() { return col; }

        @Override
        public String toString() {
            return "CoOrd{" +
                    "row=" + row +
                    ", col=" + col +
                    '}';
        }
    }

    private class Dice {

        private char letter;
        private boolean discovered;

        Dice (char letter) {
            this.letter = letter;
        }

        void isDiscovered() { this.discovered = true; }

        void notDiscovered() { this.discovered = false; }

        @Override
        public String toString() {
            return "Dice{" +
                    "letter=" + letter +
                    ", discovered=" + discovered +
                    '}';
        }
    }


    private class Board implements Iterator, Iterable<Dice> {

        ArrayList<Dice> board;
        final static int dimension = 4;
        private final static String QU_REPLACEMENT = ".";
        private int curIteratorIndex;

        Board (String boardLetters) {

            this.board = new ArrayList<>();
            boardLetters = boardLetters.replaceAll("qu", QU_REPLACEMENT);

            for (char letter : boardLetters.toCharArray()) {
                this.board.add(new Dice(letter));
            }

        }

        ArrayList<Dice> getAdjacentDice(Dice dice) {



            return null;

            /*

            row = pos % dim
            col = pos / dim

            if (col + 1 < dim)
                add board[pos + 1]
            if (col - 1 >= 0)
                add board[pos - 1]

            if (row + 1 < dim)
                add board[pos + dim]
            */

        }

        private CoOrd getDiceCoOrd(Dice dice) {
            int position = this.getDicePosition(dice);
            return new CoOrd(
                    position % dimension,
                    position / dimension
            );
        }

        private int getDicePosition(Dice dice) {
            for (int i = 0; i < board.size(); i++) {
                if (board.get(i).equals(dice)) {
                    return i;
                }
            }
            return -1;
        }

        void clearDiscoveries() {

        }


        @Override
        public boolean hasNext() {
            return this.curIteratorIndex < this.board.size();
        }

        @Override
        public Dice next() {
            return this.board.get(this.curIteratorIndex++);
        }

        @NonNull
        @Override
        public Iterator<Dice> iterator() {
            return this;
        }
    }


}
