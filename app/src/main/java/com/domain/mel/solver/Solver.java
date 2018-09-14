package com.domain.mel.solver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;


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

        ArrayList<String> foundWords = new ArrayList<String>();


        for (Dice d : this.board.board) {
            this.DFS(d, new Stack<Dice>(), foundWords);

        }

        Collections.sort(foundWords);

        for (String word : foundWords) {
            Log.d(TAG, "Word:" + word);
        }




    }





    public CoOrd getCoOrdPath(String foundWord) {
        return null;
    }

    public String[] getAllAnswers(String boardLetters) {
        return null;
    }


    private void DFS(Dice curDice, Stack<Dice> curWordPath, ArrayList<String> foundWords) {

        if (curWordPath == null) {
            return;
        }

        String nextWord;
        String curWord = this.stackToString(curWordPath);
        curWordPath.push(curDice);

        if (foundWords != null)
            if (this.dictionary.isWord(curWord) && !foundWords.contains(curWord))
                foundWords.add(curWord);

        for (Dice nextDice : this.board.getAdjacentDice(curDice)) {
            nextWord = curWord + curDice.getLetter();

            if (!curWordPath.contains(nextDice) && this.dictionary.isPartialWord(nextWord))
                this.DFS(nextDice, curWordPath, foundWords);

        }

        curWordPath.pop();

    }

    private String stackToString(Stack<Dice> wordPath) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Dice d : wordPath) {
            stringBuilder.append(d.getLetter());
        }
        return stringBuilder.toString();
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
        final static String QU_REPLACEMENT = ".";

        Dice (char letter) throws InvalidDiceException {
            if (String.valueOf(letter).equals(QU_REPLACEMENT) ||
                    Character.isLowerCase(letter))
                this.letter = letter;
            else
                throw new InvalidDiceException("Invalid character");

        }

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

    /**
     * A Board object represents the board of dice in the game of boggle
     * Contains an array of dice that can be iterated through
     */
    private class Board implements Iterable<Dice> {

        ArrayList<Dice> board;
        final static int DIMENSION = 4;
        // Number of dice on board
        final static int LETTERS_COUNT = DIMENSION * DIMENSION;

        private int curIteratorIndex; // Current index of dice iterator

        private final int OFFSET_ROW = 0;
        private final int OFFSET_COL = 1;
        private final int[][] offsets = {
                {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}
        }; // (Row, Column) pairs which are used to valid create edges between dice

        /**
         * Constructor to initialise a boggle board object
         * @param boardLetters the letters on the board
         * @throws InvalidBoardException occurs if an invalid character is used in the input,
         * valid letters being 'a' to 'z' (where 'qu' is considered as a single letter)
         */
        Board (String boardLetters) throws InvalidBoardException {

            this.board = new ArrayList<>(); // Initialising empty board of dice
            char[] boardLettersArr = boardLetters
                    .replaceAll("qu", Dice.QU_REPLACEMENT)
                    // Replacing all instances of the 'qu' dice with '.'
                    // in order to keep type as char
                    .toCharArray(); // Converting string to an array of chars

            if (boardLettersArr.length != LETTERS_COUNT)
                // If an invalid number of letters have been used to initialise board
                throw new InvalidBoardException("Number of letters must be: " + LETTERS_COUNT + ", currently: " +
                    boardLettersArr.length);

            for (char letter : boardLettersArr) {
                // For each letter in the inputted characters
                try {
                    // Try to add the dice with the letter to the board
                    this.board.add(new Dice(letter));
                } catch (Dice.InvalidDiceException e) {
                    // Otherwise, the character is not alphabetical (or '.' representing 'qu')
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
