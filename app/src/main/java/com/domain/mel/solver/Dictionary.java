package com.domain.mel.solver;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;

/**
 * A class where you can search if a word occurs in
 * the english dictionary, either the complete word only (MATCH)
 * or as part of another valid word only (PARTIAL_MATCH), or as part
 * of another word as well as being a complete word itself (BOTH).
 *
 * A trie is used to represent the english dictionary, and a modified
 * iterative DFS algorithm used to search the word given.
 */

public class Dictionary {

    private static final String TAG = "Dictionary"; // For logs
    // serialised dictionary file name
    private static final String DICT_FILE_NAME = "dict.txt";
    private static final char POP_STACK = '.'; // char used to symbolise stack pop
    // char used to symbolise the end of a word
    private static final char END_OF_WORD = (char) 13;
    /* Possible search outcomes for words that have been searched
    BOTH => the word exists in the dictionary, and may also be the beginning of another word
    MATCH => the word exists in the dictionary, the word is not part of the beginning of any other word
    PARTIAL_MATCH => the word occurs as the beginning of at least one other word in the dictionary
    NEITHER => the word does not occur in the dictionary, either as the beginning of another word
    or as a word itself
    */
    private enum SearchOutcome { BOTH, MATCH, PARTIAL_MATCH, NEITHER };
    private final TrieNode ROOT = new TrieNode(); // The root node of the trie

    /**
     * @param context used to read the dictionary file from assets
     * @throws IOException occurs if the dictionary file cannot be found
     */
    public Dictionary(Context context) throws IOException, InvalidDictionaryException {

        Stack<TrieNode> stack = new Stack<>();
        stack.push(this.ROOT);

        TrieNode prevNode;
        TrieNode curNode = this.ROOT;

        // Reading in dictionary file
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(
                context.getAssets().open(DICT_FILE_NAME)
        ));

        int curChar;
        while ((curChar = inputStream.read()) != -1) {
            // while current character is not the end of file...

            if (Character.isLowerCase((char) curChar)) {
                prevNode = curNode;
                curNode = new TrieNode((char) curChar);
                stack.push(curNode);
                prevNode.children.add(curNode);

            } else if ((char) curChar == POP_STACK) {
                // If the current character indicates to pop the stack
                if (curNode == ROOT)
                    // Can't pop stack if there's nothing on it
                    throw new InvalidDictionaryException("Invalid serialisation in dictionary file");

                prevNode = stack.pop();
                curNode = stack.peek();

            } else if ((char) curChar == END_OF_WORD){
                // If the current character indicates the end of the word
                if (curNode == ROOT)
                    // Can't add an empty word
                    throw new InvalidDictionaryException("Invalid serialisation in dictionary file");
                curNode.addChild(new TrieNode());

            } else {
                // Otherwise invalid character
                throw new InvalidDictionaryException("Invalid Character in dictionary file: " + (char) curChar);
            }

        }
        inputStream.close();

    }

    /**
     * @param word to search whether it is the beginning of at least one other word in the dictionary
     * @return whether the word occurs as the beginning of at least one other word in the dictionary
     */
    public boolean isPartialWord(String word) {
        SearchOutcome searchOutcome = this.search(word);
        // Word may or may not be a word by itself, or only as the beginning of another word
        return searchOutcome == SearchOutcome.PARTIAL_MATCH || searchOutcome == SearchOutcome.BOTH;
    }

    /**
     * @param word to search whether the word exists in the dictionary
     * @return whether the word exists in the dictionary
     */
    public boolean isWord(String word) {
        SearchOutcome searchOutcome = this.search(word);
        // Word may only exist as a word by itself, not as the beginning of another word
        return searchOutcome == SearchOutcome.MATCH || searchOutcome == SearchOutcome.BOTH;
    }

    /**
     * @param searchWord the word to search for in the dictionary to check whether it is a full match,
     *                   partial match, both, or neither
     * @return the search outcome of the word in the dictionary
     */
    private SearchOutcome search(String searchWord) {

        char[] wordArr = searchWord.toCharArray(); // Convert the string to array of letters
        int wordArrIndex = 0; // Used to index the array of letters
        char curWordChar = wordArr[wordArrIndex]; // Current character is the initial letter of the word

        TrieNode node = this.ROOT; // Starting at the root node
        while ((node = node.getChildWithLetter(curWordChar)) != null && !node.isLeaf()) {
            /* While the current node contains the next letter in the array as a child,
            and the current node is not a leaf (no at the bottom of the tree), update the
            current node to be the child that contains the next letter in the array,
            keep searching */

            // If the current letter is the last letter in the word
            if (wordArrIndex == (searchWord.length() - 1)) {

                if (node.hasOnlyLeafChild()) {
                    return SearchOutcome.MATCH;
                } else if (node.hasLeafChild()){
                    // Word is both a partial match, and a match
                    return SearchOutcome.BOTH;
                } else {
                    return SearchOutcome.PARTIAL_MATCH;
                }

            }
            curWordChar = wordArr[++wordArrIndex]; // Update current char & index
        }

        // Otherwise, no match found
        return SearchOutcome.NEITHER;

    }

    /**
     * @param node
     */
    private void DFS(TrieNode node) {
        for (TrieNode n : node.getChildren()) {
            Log.d(TAG, n.toString());
            this.DFS(n);
        }
    }


    /**
     * A class used to represent a node in the trie
     * Contains a letter and an array of children nodes
     * The node may be a leaf/root which has no letter
     */
    private class TrieNode {

        private char letter;
        private ArrayList<TrieNode> children;
        // Null character used for root/leaf nodes
        private static final char LEAF = '\0';

        /** Constructor for typical letter node */
        TrieNode(char letter) {
            this.letter = letter;
            this.children = new ArrayList<>();
        }

        /** Constructor for root/leaf with null char */
        TrieNode() {
            this.letter = LEAF;
            this.children = new ArrayList<>();
        }

        /** @return whether the node is a leaf or not */
        boolean isLeaf() {
            // Node is a leaf if it has no children
            return this.children.isEmpty();
        }

        /** @return whether the node has a child which is a leaf node */
        boolean hasLeafChild() {
            return this.getChildWithLetter(LEAF) != null;
        }

        /** @return whether the node has a single child, and that child is a leaf */
        boolean hasOnlyLeafChild() {
            // Check if node only has one child
            if (this.children.size() != 1)
                return false;

            // Return whether the only child is a leaf
            return this.children.get(0).getLetter() == LEAF;
        }

        /** @param node to add as a child */
        void addChild(TrieNode node) {
            children.add(node);
        }

        /** @return null if the node has no child with that letter, otherwise
         * returns the node child with that letter */
        TrieNode getChildWithLetter(char letter) {
            for (TrieNode n : this.children) {
                if (n.getLetter() == letter) {
                    return n;
                }
            }
            return null;
        }

        /** @return the letter the node represents */
        char getLetter() {
            return this.letter;
        }

        /** @return the children objects of this node, returns an empty
         * ArrayList if there are no children */
        ArrayList<TrieNode> getChildren() {
            return this.children;
        }


        /** @return the string representation of the node */
        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append(this.letter);
            str.append("-[");
            for (TrieNode n : this.children) {
                str.append(n.getLetter());
                str.append(" ");
            }
            return str.toString() + "]";
        }

    }

    /**
     * An exception class used to notify about invalid serialisation in the
     * dictionary file
     */
    public class InvalidDictionaryException extends Exception {
        public InvalidDictionaryException(String message) {
            super(message);
        }
    }

}
