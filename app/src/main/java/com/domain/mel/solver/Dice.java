package com.domain.mel.solver;

class Dice {

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
