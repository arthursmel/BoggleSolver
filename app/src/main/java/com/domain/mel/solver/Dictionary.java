package com.domain.mel.solver;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 *
 */

public class Dictionary {

    public Dictionary() {



    }


    private class TrieNode {

        private char letter;
        private ArrayList<TrieNode> children;

        TrieNode(char letter) {
            this.letter = letter;
            this.children = new ArrayList<TrieNode>();
        }

        void addChild(TrieNode node) {
            children.add(node);
        }


    }

}
