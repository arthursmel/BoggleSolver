package com.domain.mel.solver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class BoardTest {

    @Test
    public void ConstructorTest() throws Exception {
        StringBuilder result = new StringBuilder();
        String testString = "abcdefghijklmnoqu";

        Board board = new Board("abcdefghijklmnoqu");
        for (Dice d : board) {
            result.append(d.getLetter());
        }
        assertEquals(testString, result.toString());
    }

    @Test
    public void getDiceCoOrdTest() throws Exception {
        Board board = new Board("abcdefghijklmnoqu");
        Board.CoOrd coOrd1 = board.getDiceCoOrd(board.dices.get(0));
        Board.CoOrd coOrd2 = new Board.CoOrd(0, 0);

        assertEquals(coOrd1.row, coOrd2.row);
        assertEquals(coOrd1.col, coOrd2.col);
    }

    @Test
    public void getCoOrdDiceTest() throws Exception {
        Board board = new Board("abcdefghijklmnoqu");
        Dice dice1 = board.getCoOrdDice(new Board.CoOrd(0, 0));
        Dice dice2 = board.dices.get(0);

        assertEquals(dice1.getLetter(), dice2.getLetter());
    }

    @Test
    public void getAdjacentDiceTest() throws Exception {
        Board board = new Board("abcdefghijklmnoqu");

        String[] expectedValues = {"b", "f", "e"};
        Dice dice = board.getCoOrdDice(new Board.CoOrd(0,0));
        Dice[] adjDice = board.getAdjacentDice(dice);

        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], adjDice[i].getLetter());
        }
    }

    @Test
    public void boardToStringArrayTest() throws Exception {
        Board board = new Board("abcdefghijklmnoqu");
        String[] expected = {"a", "b", "c", "d", "e", "f", "g", "h",
        "i", "j", "k", "l", "m", "n", "o", "qu"};
        String[] boardArr = board.toStringArray();
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], boardArr[i]);
        }
    }

}
