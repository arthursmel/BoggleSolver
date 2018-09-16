package com.domain.mel.solver;

/**
 * A Dice object represents a dice with letter on
 * the boggle board. 'qu' dice are represented with
 * a '.' character
 */
class Dice {

    private final char letter;
    public final static String QU_REPLACEMENT = ".";

    /**
     * Constructor to create a new dice object. Must use '.'
     * char instead of 'qu'.
     * @param letter that is displayed on the dice
     * @throws InvalidDiceException occurs if an invalid character
     * is used as the letter
     */
    public Dice (char letter) throws InvalidDiceException {
        if (String.valueOf(letter).equals(QU_REPLACEMENT) ||
                Character.isLowerCase(letter))
            // If the character is valid
            this.letter = letter;
        else
            // Otherwise, error
            throw new InvalidDiceException("Invalid character");
    }

    /**
     * Gets the String of the letter displayed on the dice
     * @return the letter on the dice (note 'qu' will be returned
     * instead of '.')
     */
    public String getLetter() {
        if (Character.isLowerCase(this.letter))
            // If character isn't '.'
            return String.valueOf(this.letter);
        else
            // Otherwise return 'qu' which '.' represents
            return "qu";
    }

    @Override
    public String toString() {
        return "Dice{" + letter +'}';
    }

    public class InvalidDiceException extends Exception {
        InvalidDiceException(String message) {
            super(message);
        }
    }
}
