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
    private static final char END_OF_WORD = ' ';
    private enum SearchOutcome { BOTH, MATCH, PARTIAL_MATCH, NEITHER };
    private final TrieNode ROOT = new TrieNode();


    public Dictionary(Context context) throws IOException {

        Stack<TrieNode> stack = new Stack<>();
        TrieNode prevNode;
        TrieNode curNode = this.ROOT;

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
                // If the current character indicates to pop the stack
                prevNode = stack.pop();
                curNode = stack.peek();

            } else if ((char) curChar == END_OF_WORD){
                // If the current character indicates the end of the word
                curNode.addChild(new TrieNode());

            } else {
                // Otherwise invalid character

            }

        }

        inputStream.close();

    }


    public boolean isPartialWord(String word) {
        SearchOutcome searchOutcome = this.search(word);
        return searchOutcome == SearchOutcome.PARTIAL_MATCH || searchOutcome == SearchOutcome.BOTH;
    }

    public boolean isWord(String word) {
        SearchOutcome searchOutcome = this.search(word);
        return searchOutcome == SearchOutcome.MATCH || searchOutcome == SearchOutcome.BOTH;
    }

    private SearchOutcome search(String searchWord) {

        char[] wordArr = searchWord.toCharArray();
        int wordArrIndex = 0;
        char curWordChar = wordArr[wordArrIndex];

        TrieNode node = this.ROOT;
        while ((node = node.getChildWithLetter(curWordChar)) != null && !node.isLeaf()) {

            if (wordArrIndex == (searchWord.length() - 1)) {

                if (node.hasOnlyLeafChild()) {
                    return SearchOutcome.MATCH;
                } else if (node.hasLeafChild()){
                    return SearchOutcome.BOTH;
                } else {
                    return SearchOutcome.PARTIAL_MATCH;
                }

            }
            curWordChar = wordArr[++wordArrIndex];
        }

        return SearchOutcome.NEITHER;

    }

    private void DFS(TrieNode node) {
        for (TrieNode n : node.getChildren()) {
            Log.d(TAG, n.toString());
            this.DFS(n);
        }
    }

    private static boolean isLowercaseAlphabetical(char c) {
        return c >= (int) 'a' && c <= (int) 'z';
    }


    private class TrieNode {

        private char letter;
        private ArrayList<TrieNode> children;
        private static final char LEAF = '\0';

        /*
        Constructor for typical letter node
         */
        TrieNode(char letter) {
            this.letter = letter;
            this.children = new ArrayList<>();
        }

        /*
        Constructor for root/leaf with null char
         */
        TrieNode() {
            this.letter = LEAF;
            this.children = new ArrayList<>();
        }

        boolean isLeaf() {
            return this.children.isEmpty();
        }

        boolean hasLeafChild() {
            return this.getChildWithLetter(LEAF) != null;
        }

        boolean hasOnlyLeafChild() {
            if (this.children.size() != 1)
                return false;

            return this.children.get(0).getLetter() == LEAF;
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
