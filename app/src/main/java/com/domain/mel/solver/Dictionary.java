package com.domain.mel.solver;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

        this.DFS(root);
        inputStream.close();

    }


    public boolean isPartialWord() {
        return true;
    }

    public boolean isWord() {
        return true;
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
            str.append(" ");
            for (TrieNode n : this.children) {
                str.append(n.getLetter());
                str.append(", ");
            }
            return str.toString();
        }


    }

}
