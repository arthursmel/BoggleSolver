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

        ArrayList<Board.CoOrd> coOrds = new ArrayList<>();
        Stack<String> word = new Stack<>();
        Stack<Dice> tempPath = new Stack<>();
        Stack<Dice> path = new Stack<>();
        Board board = new Board(boardLetters);


        for (String letter : foundWord.split("")) {
            if (!letter.isEmpty())
                word.push(letter);
        }
        Collections.reverse(word);


        for (Dice dice : board.dices) {
            this.DFSWordPath(board, dice, word, tempPath, path);
        }

        Log.d(TAG, "Size" + path);


        for (Dice dice : path) {
            coOrds.add(board.getDiceCoOrd(dice));
        }

        return coOrds.toArray(new Board.CoOrd[coOrds.size()]);
    }

    private void DFSWordPath(Board board, Dice curDice, Stack<String> word, Stack<Dice> curPath, Stack<Dice> path) {

        if (word.isEmpty()) {

            if (path.isEmpty())
                path.addAll(curPath);

            Log.d(TAG, path.toString());
            return;
        }

        String curLetter = word.pop();

        if (curLetter.equals(curDice.getLetter())) {
            curPath.push(curDice);
            for (Dice dice : board.getAdjacentDice(curDice)) {
                this.DFSWordPath(board, dice, word, curPath, path);
            }

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
        String curWord = this.stackToWord(curWordPath);
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

    private String stackToWord(Stack<Dice> wordPath) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Dice d : wordPath) {
            stringBuilder.append(d.getLetter());
        }
        return stringBuilder.toString();
    }

}
