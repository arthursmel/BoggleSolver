package com.domain.mel.solver;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 *
 */

public class Solver {

    private static final String TAG = "Solver";

    public Solver(String boardLetters) {

        Board b = new Board(boardLetters);

        for (Dice d : b) {
            Log.d(TAG, "Dice: " + d + " - " + b.getAdjacentDice(d));
        }

    }



    public CoOrd getCoOrdPath(String foundWord) {
        return null;
    }

    public String[] getAllAnswers(String boardLetters) {
        return null;
    }


    private class CoOrd {

        final int row;
        final int col;

        CoOrd(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public String toString() {
            return "CoOrd{row=" + row + ", col=" + col + '}';
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
            return "Dice{" + letter +'}';
        }
    }


    private class Board implements Iterable<Dice> {

        ArrayList<Dice> board;
        final static int dimension = 4;
        private final static String QU_REPLACEMENT = ".";
        private int curIteratorIndex;

        private final int OFFSET_ROW = 0;
        private final int OFFSET_COL = 1;
        private final int[][] offsets = {
                {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}
        };

        Board (String boardLetters) {

            this.board = new ArrayList<>();
            boardLetters = boardLetters.replaceAll("qu", QU_REPLACEMENT);

            for (char letter : boardLetters.toCharArray()) {
                this.board.add(new Dice(letter));
            }

        }

        ArrayList<Dice> getAdjacentDice(Dice dice) {
            if (!this.board.contains(dice)) {
                return null;
            }

            ArrayList<Dice> adjacentDice = new ArrayList<>();
            CoOrd diceCoOrd = this.getDiceCoOrd(dice);

            for (int[] offset : this.offsets) {
                CoOrd curCoOrd = new CoOrd(diceCoOrd.row + offset[OFFSET_ROW],
                        diceCoOrd.col + offset[OFFSET_COL]);
                if (this.isValidCoOrd(curCoOrd))
                    adjacentDice.add(this.getCoOrdDice(curCoOrd));
            }

            return adjacentDice;
        }

        private CoOrd getDiceCoOrd(Dice dice) {
            int position = this.getDicePosition(dice);
            return new CoOrd(
                    position / dimension,
                    position % dimension
            );
        }

        private Dice getCoOrdDice(CoOrd coOrd) {
            return this.board.get((coOrd.row * dimension) + coOrd.col);
        }

        private int getDicePosition(Dice dice) {
            for (int i = 0; i < board.size(); i++) {
                if (board.get(i).equals(dice)) {
                    return i;
                }
            }
            return -1;
        }

        private boolean isValidCoOrd(CoOrd coOrd) {
            return coOrd.col >= 0 && coOrd.col < dimension &&
                    coOrd.row >= 0 && coOrd.row < dimension;
        }

        void clearDiscoveries() {

        }

        @NonNull
        @Override
        public Iterator<Dice> iterator() {
            return new Iterator<Dice>() {
                @Override
                public boolean hasNext() {
                    return curIteratorIndex < board.size();
                }

                @Override
                public Dice next() {
                    return board.get(curIteratorIndex++);
                }
            };
        }
    }


}
