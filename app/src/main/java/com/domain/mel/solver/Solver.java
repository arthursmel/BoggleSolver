package com.domain.mel.solver;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;


/**
 * Allows a game of boggle to be solved, by giving all
 * possible results of the given board. Also gives the
 * path of a found word.
 */

public class Solver {

    private static final String TAG = "Solver";
    private static final int MINIMUM_WORD_LETTER_COUNT = 3;
    private Dictionary dictionary;

    private static final int ERROR_SCORE = -1;
    private static final int THREE_FOUR_LETTER_SCORE = 1;
    private static final int FIVE_LETTER_SCORE = 2;
    private static final int SIX_LETTER_SCORE = 3;
    private static final int MAX_WORD_SCORE = 5;

    /**
     * Constructor
     * @param context needed for the dictionary
     * @throws IOException occurs when the dictionary file doesn't exist
     * @throws Dictionary.InvalidDictionaryException occurs when
     * the dictionary file is invalid
     */
    public Solver(Context context) throws IOException,
            Dictionary.InvalidDictionaryException {
        this.dictionary = new Dictionary(context);
    }

    /**
     * Returns a path of co-ords of the word given on the given
     * boggle board
     * @param boardLetters the current boggle board
     * @param foundWord the word which the path will be returned
     * @return the path of co-ords of the word given on the given
     * boggle board
     * @throws Board.InvalidBoardException occurs when the given boggle
     * board is invalid
     */
    public Board.CoOrd[] getCoOrdPath(String boardLetters, String foundWord) throws
        Board.InvalidBoardException {

        Stack<Dice> tempPath = new Stack<>();
        Stack<Dice> path = new Stack<>(); // Initialising the final path which will be returned
        Stack<String> word = wordToStack(foundWord); // Converting the given word into a
        // stack (in reverse order) of strings (one char per string apart from 'qu')
        Board board = new Board(boardLetters);

        for (Dice dice : board.dices)
            // For each dice on the given board
            // Find the given word path, on completion the path will be
            // in the 'path' stack object
            this.DFSWordPath(board, dice, word, tempPath, path);

        // Returning the stack converted into an array of co-ord objects
        return stackToCoOrds(path, board);
    }

    /**
     * Helper function for getCoOrdPath
     * Recursive DFS to find a given word path in the given boggle board
     * Result is returned in 'path' parameter. The result is empty if the
     * word doesn't exist in the board. For multiple instances of the word
     * on a single board, only 1 solution will be returned
     * @param board the boggle board containing the word
     * @param curDice the root dice to start the DFS from
     * @param word the word to be found in the boggle board
     *             (as a stack of single character strings apart from 'qu')
     * @param curPath the temporary current co-ord stack (must be initialised)
     * @param path the final co-ord stack of the co-ords of the given word (must be initialised)
     */
    private void DFSWordPath(Board board, Dice curDice, Stack<String> word, Stack<Dice> curPath, Stack<Dice> path) {

        if (word.isEmpty()) {
            // If the word stack is empty, word has been found
            if (path.isEmpty())
                // If the final path to be returned is empty
                // the word has not been found before, therefore
                // path is added to final stack to be returned
                path.addAll(curPath);
            // Otherwise word has been found before, and not
            // returned to prevent duplicate co-ords
            return;
        }

        String curLetter = word.pop(); // Getting next letter

        if (curLetter.equals(curDice.getLetter())) {
            // If current word letter is the same as current dice letter
            curPath.push(curDice); // Push dice to path
            for (Dice dice : board.getAdjacentDice(curDice))
                // DFS adjacent dice to current dice
                if (!curPath.contains(dice))
                    // Preventing same dice being used twice in the one word
                    this.DFSWordPath(board, dice, word, curPath, path);

            curPath.pop(); // Pop current dice, onto next dice
        }

        word.push(curLetter); // Replace word current letter in stack
    }

    /**
     * Gets all possible valid words on the given boggle board
     * @param boardLetters the current boggle board
     * @return the possible valid words on the given board
     * @throws Board.InvalidBoardException occurs if the
     * board is invalid
     */
    public String[] getAllWords(String boardLetters) throws
            Board.InvalidBoardException{

        ArrayList<String> foundWords = new ArrayList<>();
        Board board = new Board(boardLetters);

        for (Dice dice : board.dices) {
            // For each dice on the board, dfs from that dice,
            // and return found words in the array list passed
            // as a parameter
            this.DFSAllWords(board, dice, new Stack<Dice>(), foundWords);
        }

        Collections.sort(foundWords); // Sort found words alphabetically
        // Convert array list to array and return results
        return foundWords.toArray(new String[foundWords.size()]);
    }

    /**
     * Helper function for getAllWords
     * Recursive DFS to find all words in the given boggle board
     * Results are returned in 'foundWords' parameter.
     * For multiple instances of the word on a single board, only 1 solution will be returned
     * @param board the boggle board for all words to be found
     * @param curDice the root dice to start the DFS from
     * @param curWordPath the temporary current path (must be initialised)
     * @param foundWords the final array list of all found words (must be initialised)
     */
    private void DFSAllWords(Board board, Dice curDice, Stack<Dice> curWordPath, ArrayList<String> foundWords) {

        String nextWord;
        // Push current dice to the stack
        curWordPath.push(curDice);
        // Converting current stack to the corresponding current word
        String curWord = stackToWord(curWordPath);

        if (this.dictionary.isWord(curWord) &&
                !foundWords.contains(curWord) &&
                curWord.length() >= MINIMUM_WORD_LETTER_COUNT)
            // If the current word is valid, and not already found,
            // and longer than the minimum word letter count, add to result
            foundWords.add(curWord);

        for (Dice nextDice : board.getAdjacentDice(curDice)) {
            // For each dice in the dices adjacent to the current dice
            nextWord = curWord + nextDice.getLetter();
            // Add the dice letter to the current partial word

            if (!curWordPath.contains(nextDice) && this.dictionary.isPartialWord(nextWord)){

                // If the next dice has not already been used to create the current word,
                // and the current word is a possible valid word, DFS from the next dice
                this.DFSAllWords(board, nextDice, curWordPath, foundWords);}

        }

        // Finished searching from this dice, pop from stack
        curWordPath.pop();
    }

    /**
     * Converts the given string into a stack of strings with 1 character
     * apart from 'qu'
     * @param word the word to be converted into a stack
     * @return the resulting stack from the given word
     */
    private static Stack<String> wordToStack(String word) {
        Stack<String> stack = new Stack<>();

        // Replacing any dice with 'qu' in order for split to
        // separate characters
        word = word.replaceAll("qu", Dice.QU_REPLACEMENT);

        for (String letter : word.split("")) {
            if (!letter.isEmpty()) // Prevents the empty char from
                // split from being added to the stack
                stack.push(
                        // Replacing 'qu' back again
                        (letter.equals(Dice.QU_REPLACEMENT)) ? "qu" : letter
                );
        }
        // Reversing stack so first letter of the word is at the top
        Collections.reverse(stack);
        return stack;
    }

    /**
     * Converts a stack of dice into the corresponding word
     * @param path the dice path of the word
     * @return the string of the corresponding word if the
     * path is not null, otherwise null
     */
    private static String stackToWord(Stack<Dice> path) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Dice d : path) {
            // For each dice in the path, get the dice letter
            // and add to result string
            stringBuilder.append(d.getLetter());
        }
        return stringBuilder.toString();
    }

    /**
     * Converts a stack of dice into an array of their
     * respective co-ords
     * @param path the stack of dice to convert into co-ords
     * @param board the current boggle board
     * @return the stack of co-ords of the corresponding dice given
     */
    private static Board.CoOrd[] stackToCoOrds(Stack<Dice> path, Board board) {
        ArrayList<Board.CoOrd> coOrds = new ArrayList<>();
        for (Dice dice : path) {
            // For each dice in the given path
            // Get dice co-ord and add to result
            coOrds.add(board.getDiceCoOrd(dice));
        }
        // Convert array list to array
        return coOrds.toArray(new Board.CoOrd[coOrds.size()]);
    }

    /**
     * @param words the found words on the board
     * @return the respective score of the found words on the board
     * or -1 if the word is invalid
     */
    public static Integer[] getWordScores(String[] words) {
        ArrayList<Integer> scores = new ArrayList<>();
        int wordLength;

        for (String word : words) {

            wordLength = word.length();
            if (wordLength < MINIMUM_WORD_LETTER_COUNT) {
                scores.add(ERROR_SCORE);
            } else {
                switch (word.length()) {
                    case 3:
                    case 4:
                        scores.add(THREE_FOUR_LETTER_SCORE);
                        break;
                    case 5:
                        scores.add(FIVE_LETTER_SCORE);
                        break;
                    case 6:
                        scores.add(SIX_LETTER_SCORE);
                        break;
                    default:
                        scores.add(MAX_WORD_SCORE);
                }
            }

        }
        return scores.toArray(new Integer[scores.size()]);
    }

    /**
     * @return the max score possible (if user found all
     * valid words)
     */
    public static int getTotalScore(String[] words) {
        int result = 0;
        Integer[] scores = getWordScores(words);
        for (Integer score : scores) {
            result += score;
        }
        return result;
    }

}
