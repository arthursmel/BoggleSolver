package com.domain.mel.solver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


/**
 *
 */

public class Solver {

    private static final String TAG = "Solver";
    private Dictionary dictionary;
    private Board board;

    public Solver(String boardLetters, Context context) throws IOException,
            Dictionary.InvalidDictionaryException, Board.InvalidBoardException {


        this.dictionary = new Dictionary(context);
        this.board = new Board(boardLetters);

        for (Dice d : this.board)
            Log.d(TAG, "Dice: " + d + " - " + this.board
                    .getAdjacentDice(d));





    }





    public CoOrd getCoOrdPath(String foundWord) {
        return null;
    }

    public String[] getAllAnswers(String boardLetters) {
        return null;
    }


    private void DFS(Dice curDice, String curWord, ArrayList<String> foundWords) {

        curDice.setDiscovered();
        curWord += curDice.getLetter();

        if (this.dictionary.isWord(curWord))
            if (foundWords != null)
                foundWords.add(curWord);

        for (Dice nextDice : this.board.getAdjacentDice(curDice)) {
            String nextWord = curWord + nextDice.getLetter();
            if (!nextDice.isDiscovered() && this.dictionary.isPartialWord(nextWord))
                this.DFS(nextDice, nextWord, foundWords);

        }

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

        private final char letter;
        private boolean discovered;
        final static String QU_REPLACEMENT = ".";

        Dice (char letter) throws InvalidDiceException {
            if (String.valueOf(letter).equals(QU_REPLACEMENT) ||
                    Character.isLowerCase(letter))
                this.letter = letter;
            else
                throw new InvalidDiceException("Invalid character");

        }

        void setDiscovered() { this.discovered = true; }

        void setNotDiscovered() { this.discovered = false; }

        boolean isDiscovered() { return this.discovered; }

        String getLetter() {
            if (Character.isLowerCase(this.letter))
                return String.valueOf(this.letter);
            else
                return "qu";
        }

        @Override
        public String toString() {
            return "Dice{" + letter +'}';
        }

        class InvalidDiceException extends Exception {
            InvalidDiceException(String message) {
                super(message);
            }
        }
    }


    private class Board implements Iterable<Dice> {

        ArrayList<Dice> board;
        final static int DIMENSION = 4;
        final static int LETTERS_COUNT = DIMENSION * DIMENSION;


        private int curIteratorIndex;

        private final int OFFSET_ROW = 0;
        private final int OFFSET_COL = 1;
        private final int[][] offsets = {
                {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}
        };

        Board (String boardLetters) throws InvalidBoardException {

            this.board = new ArrayList<>();
            char[] boardLettersArr = boardLetters
                    .replaceAll("qu", Dice.QU_REPLACEMENT)
                    .toCharArray();

            if (boardLettersArr.length != LETTERS_COUNT)
                throw new InvalidBoardException("Number of letters must be: " + LETTERS_COUNT + ", currently: " +
                    boardLettersArr.length);

            for (char letter : boardLettersArr) {

                try {
                    this.board.add(new Dice(letter));
                } catch (Dice.InvalidDiceException e) {
                    e.printStackTrace();
                    throw new InvalidBoardException("Invalid character in the board");
                }

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
                    position / DIMENSION,
                    position % DIMENSION
            );
        }

        private Dice getCoOrdDice(CoOrd coOrd) {
            return this.board.get((coOrd.row * DIMENSION) + coOrd.col);
        }

        private int getDicePosition(Dice dice) {
            for (int i = 0; i < board.size(); i++)
                if (board.get(i).equals(dice))
                    return i;
            return -1;
        }

        private boolean isValidCoOrd(CoOrd coOrd) {
            return coOrd.col >= 0 && coOrd.col < DIMENSION &&
                    coOrd.row >= 0 && coOrd.row < DIMENSION;
        }

        void clearDiscoveries() {
            for (Dice d : this.board)
                d.setNotDiscovered();
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

        private class InvalidBoardException extends Exception {
            private InvalidBoardException(String message) {
                super(message);
            }
        }
    }


}
