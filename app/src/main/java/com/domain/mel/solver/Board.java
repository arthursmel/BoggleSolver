package com.domain.mel.solver;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A Board object represents the board of dice in the game of boggle
 * Contains an array of dice that can be iterated through
 */
public class Board implements Iterable<Dice> {

    ArrayList<Dice> dices;
    public final static int DIMENSION = 4;
    // Number of dice on board
    private final static int LETTERS_COUNT = DIMENSION * DIMENSION;

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
    public Board (String boardLetters) throws InvalidBoardException {

        this.dices = new ArrayList<>(); // Initialising empty board of dice
        char[] boardLettersArr = boardLetters
                .replaceAll("q", Dice.QU_REPLACEMENT)
                // Replacing all instances of the 'q' dice with '.'
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
                this.dices.add(new Dice(letter));
            } catch (Dice.InvalidDiceException e) {
                // Otherwise, the character is not alphabetical (or '.' representing 'qu')
                throw new InvalidBoardException("Invalid character in the board");
            }
        }

    }

    /**
     * @return The array representation of the dice letters on the board
     */
    public String[] toStringArray() {
        ArrayList<String> result = new ArrayList<>();
        for (Dice dice : this.dices) {
            result.add(dice.getLetter());
        }
        return result.toArray(new String[result.size()]);
    }


    /**
     * @param dice the dice in which the adjacent dice will be found
     * @return an ArrayList of dice which are adjacent to the dice
     * provided in the parameter
     */
    public Dice[] getAdjacentDice(Dice dice) {
        // Get the co-ordinate of the dice provided on the boggle board
        CoOrd diceCoOrd;
        if ((diceCoOrd = this.getDiceCoOrd(dice)) == null)
            // If the board doesn't contain the dice object
            return null;

        ArrayList<Dice> adjacentDice = new ArrayList<>();

        for (int[] offset : this.offsets) {
            // For each possible co-ordinate beside the co-ordinate of
            // the dice provided
            CoOrd curCoOrd = new CoOrd(diceCoOrd.row + offset[OFFSET_ROW],
                    diceCoOrd.col + offset[OFFSET_COL]);
            if (this.isValidCoOrd(curCoOrd))
                // If the potential co-ordinate is valid
                adjacentDice.add(this.getCoOrdDice(curCoOrd));
                // Add the dice which exists at this co-ordinate
                // to the adjacent dice ArrayList
        }
        return adjacentDice.toArray(new Dice[adjacentDice.size()]);
    }

    /**
     * Finds the corresponding co-ordinate pair position of the dice on the boggle
     * board, if the dice exists on the board
     * @param dice the dice you want to find the co-ordinate pair of
     * @return the co-ordinate of the dice on the board, otherwise
     * if the dice doesn't exist on the board, null
     */
    public CoOrd getDiceCoOrd(Dice dice) {
        // Get the 1D position in the board array of the dice
        int position;
        if ((position = this.getDicePosition(dice)) == -1)
            // If the dice provided exits on the board
            return null;

        return new CoOrd(
                position / DIMENSION, // Calculating row of dice
                position % DIMENSION // Calculating column of dice
        );
    }
    /**
     * Finds the corresponding dice on the boggle board of the
     * co-ordinate pair, if the ordinate pair exists on the board
     * @param coOrd the co-ordinate pair you want to find the dice of
     * @return the dice object of the corresponding co-ordinate pair
     * if the co-ordinate pair is valid, otherwise null
     */
    public Dice getCoOrdDice(CoOrd coOrd) {
        if (!this.isValidCoOrd(coOrd))
            // If the co-ordinate pair is not valid
            return null;
        // Otherwise return the relative 1D position of the
        // dice object in the board ArrayList of dice
        return this.dices.get(coOrd.toIndex());
    }

    /**
     * Finds the 1D position in the dice ArrayList of a given
     * dice object
     * @param dice the dice to find the position of
     * @return if dice object in the ArrayList, returns the
     * 1D position, otherwise returns -1
     */
    private int getDicePosition(Dice dice) {
        for (int i = 0; i < dices.size(); i++)
            // For each element in the board
            if (dices.get(i).equals(dice))
                // If the given dice and the element
                // are equal, return current position
                return i;
        return -1; // Otherwise dice not found, error
    }

    /**
     * Checks if a co-ordinate object is within the board
     * ArrayList bounds
     * @param coOrd the co-ord to check if valid
     * @return whether the co-ord is valid or not
     */
    private boolean isValidCoOrd(CoOrd coOrd) {
        // If col of the co-ord is within the bounds of the board
        // and if the row of the co-ord is within the bounds of the board
        return coOrd.col >= 0 && coOrd.col < DIMENSION &&
                coOrd.row >= 0 && coOrd.row < DIMENSION;
    }

    @NonNull
    @Override
    public Iterator<Dice> iterator() {
        return new Iterator<Dice>() {
            @Override
            public boolean hasNext() {
                // While the iterator index is less than the board size
                return curIteratorIndex < dices.size();
            }

            @Override
            public Dice next() {
                // Get next dice object in the board, and increment index
                return dices.get(curIteratorIndex++);
            }
        };
    }

    public static class InvalidBoardException extends Exception {
        private InvalidBoardException(String message) {
            super(message);
        }
    }

    /**
     * A row column pair representing a co-ordinate on
     * the boggle board
     */
    public static class CoOrd {

        public final int row;
        public final int col;

        /**
         * Constructor for a CoOrd
         * @param row row position
         * @param col column position
         */
        CoOrd(int row, int col) {
            this.row = row;
            this.col = col;
        }

        /**
         * @return the 1D index of the co-ord
         */
        public int toIndex() {
            return this.col + (this.row * DIMENSION);
        }

        @Override
        public String toString() {
            return "CoOrd{row=" + row + ", col=" + col + '}';
        }
    }
}