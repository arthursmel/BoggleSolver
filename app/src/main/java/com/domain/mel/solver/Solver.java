package com.domain.mel.solver;

import android.content.Context;

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
    private Dictionary dictionary;

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
     * @param curPath the temporary current co-ord stack
     * @param path the final co-ord stack of the co-ords of the given word
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
     *
     * @param board
     * @param curDice
     * @param curWordPath
     * @param foundWords
     */
    private void DFSAllWords(Board board, Dice curDice, Stack<Dice> curWordPath, ArrayList<String> foundWords) {

        if (curWordPath == null) {
            return;
        }

        String nextWord;
        String curWord = stackToWord(curWordPath);
        curWordPath.push(curDice);

        if (foundWords != null)
            if (this.dictionary.isWord(curWord) && !foundWords.contains(curWord))
                foundWords.add(curWord);

        for (Dice nextDice : board.getAdjacentDice(curDice)) {
            nextWord = curWord + curDice.getLetter();

            if (!curWordPath.contains(nextDice) && this.dictionary.isPartialWord(nextWord))
                this.DFSAllWords(board, nextDice, curWordPath, foundWords);

        }

        curWordPath.pop();

    }

    private static Stack<String> wordToStack(String word) {
        Stack<String> stack = new Stack<>();
        word = word.replaceAll("qu", Dice.QU_REPLACEMENT);
        for (String letter : word.split("")) {
            if (!letter.isEmpty())
                stack.push(
                        (letter.equals(Dice.QU_REPLACEMENT)) ? "qu" : letter
                );
        }
        Collections.reverse(stack);
        return stack;
    }

    private static String stackToWord(Stack<Dice> path) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Dice d : path) {
            stringBuilder.append(d.getLetter());
        }
        return stringBuilder.toString();
    }

    private static Board.CoOrd[] stackToCoOrds(Stack<Dice> path, Board board) {
        ArrayList<Board.CoOrd> coOrds = new ArrayList<>();
        for (Dice dice : path) {
            coOrds.add(board.getDiceCoOrd(dice));
        }
        return coOrds.toArray(new Board.CoOrd[coOrds.size()]);
    }

}
