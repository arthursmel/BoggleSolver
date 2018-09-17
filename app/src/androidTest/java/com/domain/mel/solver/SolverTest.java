package com.domain.mel.solver;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SolverTest {

    @Test
    public void getCoOrdPathTest() throws Exception {
        // Context of the app under test
        Context appContext = InstrumentationRegistry.getTargetContext();
        Solver solver = new Solver(appContext);

        Board.CoOrd[] expectedPath = {
                new Board.CoOrd(1, 1),
                new Board.CoOrd(2, 0),
                new Board.CoOrd(3, 1)
        };
        Board.CoOrd[] path = solver.getCoOrdPath("abcdefghijklmnop", "fin");

        for (int i = 0; i < path.length; i++) {
            assertEquals(expectedPath[i].col, path[i].col);
            assertEquals(expectedPath[i].row, path[i].row);
        }

        path = solver.getCoOrdPath("abcdefghijklmnop", "x");
        assertEquals(0, path.length);
    }

    @Test
    public void getAllWordsTest() throws Exception {
        // Context of the app under test
        Context appContext = InstrumentationRegistry.getTargetContext();
        Solver solver = new Solver(appContext);
        String[] words = solver.getAllWords("vvvvvvvvvvvvvzoo");

        assertEquals(1, words.length);
        assertEquals("zoo", words[0]);
    }

}
