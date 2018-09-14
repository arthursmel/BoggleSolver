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


    public Board.CoOrd getCoOrdPath(String boardLetters, String foundWord) {
        return null;
    }

    public String[] getAllAnswers(String boardLetters) throws
            Board.InvalidBoardException{

        ArrayList<String> foundWords = new ArrayList<String>();
        Board board = new Board(boardLetters);

        for (Dice dice : board.dices) {
            this.DFS(board, dice, new Stack<Dice>(), foundWords);
        }

        Collections.sort(foundWords);
        return foundWords.toArray(new String[foundWords.size()]);
    }


    private void DFS(Board board, Dice curDice, Stack<Dice> curWordPath, ArrayList<String> foundWords) {

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
                this.DFS(board, nextDice, curWordPath, foundWords);

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
