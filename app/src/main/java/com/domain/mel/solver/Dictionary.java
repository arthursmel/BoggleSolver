package com.domain.mel.solver;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 *
 */

public class Dictionary {

    private static final String TAG = "Dictionary";
    private static final String DICT_FILE_NAME = "dict.txt";
    private static final char POP_STACK = '.';
    private enum SearchOutcome { MATCH, PARTIAL_MATCH, NO_MATCH };

    private TrieNode root;

    public Dictionary(Context context) throws FileNotFoundException, IOException {

        Stack<TrieNode> stack = new Stack<>();
        this.root = new TrieNode();
        TrieNode prevNode = null;
        TrieNode curNode = this.root;

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(
                context.getAssets().open(DICT_FILE_NAME)
        ));

        int curChar;
        while ((curChar = inputStream.read()) != -1) {

            Log.d(TAG, " char: " + (char) curChar);

            if (isLowercaseAlphabetical((char) curChar)) {
                prevNode = curNode;
                curNode = new TrieNode((char) curChar);
                stack.push(curNode);
                prevNode.children.add(curNode);

            } else if ((char) curChar == POP_STACK) {
                // If the current character indicated to pop the stack
                prevNode = stack.pop();
                curNode = stack.peek();

            } else {
                // Otherwise invalid character

            }

        }

        inputStream.close();

    }


    public boolean isPartialWord(String word) {
        return this.search(word) == SearchOutcome.PARTIAL_MATCH;
    }

    public boolean isWord(String word) {
        return this.search(word) == SearchOutcome.MATCH;
    }

    private SearchOutcome search(String searchWord) {

        char[] wordArr = searchWord.toCharArray();
        int wordArrIndex = 0;
        char curWordChar = wordArr[wordArrIndex];

        TrieNode node = this.root;
        while ((node = node.getChildWithLetter(curWordChar)) != null) {

            if (wordArrIndex == (searchWord.length() - 1)) {

                if (node.isLeaf()) {
                    return SearchOutcome.MATCH;
                } else {
                    return SearchOutcome.PARTIAL_MATCH;
                }

            }

            curWordChar = wordArr[++wordArrIndex];

        }
        return SearchOutcome.NO_MATCH;

    }

    private static boolean isLowercaseAlphabetical(char c) {
        return c >= (int) 'a' && c <= (int) 'z';
    }


    private class TrieNode {

        private char letter;
        private ArrayList<TrieNode> children;

        /*
        Constructor for typical letter node
         */
        TrieNode(char letter) {
            this.letter = letter;
            this.children = new ArrayList<>();
        }

        /*
        Constructor for root with null char
         */
        TrieNode() {
            this.letter = '\0';
            this.children = new ArrayList<>();
        }

        boolean isLeaf() {
            return this.children.isEmpty();
        }

        void addChild(TrieNode node) {
            children.add(node);
        }

        /*
        Returns null if the child with that letter doesn't exist
         */
        TrieNode getChildWithLetter(char letter) {
            for (TrieNode n : this.children) {
                if (n.getLetter() == letter) {
                    return n;
                }
            }
            return null;
        }

        char getLetter() {
            return this.letter;
        }

        ArrayList<TrieNode> getChildren() {
            return this.children;
        }


        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append(this.letter);
            str.append("[");
            for (TrieNode n : this.children) {
                str.append(n.getLetter());
                str.append(", ");
            }
            return str.toString() + "]";
        }


    }

}
