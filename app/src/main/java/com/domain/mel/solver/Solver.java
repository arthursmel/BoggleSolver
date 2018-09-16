package com.domain.mel.solver;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;


/**
 *
 */

public class Solver {

    private static final String TAG = "Solver";
    private Dictionary dictionary;

    public Solver(Context context) throws IOException,
            Dictionary.InvalidDictionaryException {
        this.dictionary = new Dictionary(context);
    }


    public Board.CoOrd[] getCoOrdPath(String boardLetters, String foundWord) throws
        Board.InvalidBoardException {

        Stack<Dice> tempPath = new Stack<>();
        Stack<Dice> path = new Stack<>();
        Stack<String> word = wordToStack(foundWord);
        Board board = new Board(boardLetters);

        for (Dice dice : board.dices)
            this.DFSWordPath(board, dice, word, tempPath, path);

        return stackToCoOrds(path, board);
    }

    private void DFSWordPath(Board board, Dice curDice, Stack<String> word, Stack<Dice> curPath, Stack<Dice> path) {

        if (word.isEmpty()) {

            if (path.isEmpty())
                path.addAll(curPath);

            return;
        }

        String curLetter = word.pop();

        if (curLetter.equals(curDice.getLetter())) {
            curPath.push(curDice);
            for (Dice dice : board.getAdjacentDice(curDice))
                this.DFSWordPath(board, dice, word, curPath, path);

            curPath.pop();
        }

        word.push(curLetter);
    }

    public String[] getAllWords(String boardLetters) throws
            Board.InvalidBoardException{

        ArrayList<String> foundWords = new ArrayList<>();
        Board board = new Board(boardLetters);

        for (Dice dice : board.dices) {
            this.DFSAllWords(board, dice, new Stack<Dice>(), foundWords);
        }

        Collections.sort(foundWords);
        return foundWords.toArray(new String[foundWords.size()]);
    }


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
