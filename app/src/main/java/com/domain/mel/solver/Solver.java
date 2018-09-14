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


    public Board.CoOrd getCoOrdPath(String foundWord) {
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
        String curWord = this.stackToWord(curWordPath);
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

    private String stackToWord(Stack<Dice> wordPath) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Dice d : wordPath) {
            stringBuilder.append(d.getLetter());
        }
        return stringBuilder.toString();
    }

}
