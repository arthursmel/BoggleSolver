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
    private TrieNode root;


    public Dictionary(Context context) throws FileNotFoundException, IOException {

        Stack<TrieNode> stack = new Stack<>();

        this.root = new TrieNode();
        TrieNode prevNode = root;
        TrieNode curNode = null;


        BufferedReader inputStream = new BufferedReader(new InputStreamReader(
                context.getAssets().open(DICT_FILE_NAME)
        ));
        int curChar;

        while ((curChar = inputStream.read()) != -1) {

            Log.d(TAG, " char: " + (char) curChar);

        }

        inputStream.close();

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

        void addChild(TrieNode node) {
            children.add(node);
        }


    }

}
