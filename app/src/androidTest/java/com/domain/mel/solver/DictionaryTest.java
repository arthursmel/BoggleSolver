package com.domain.mel.solver;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DictionaryTest {

    @Test
    public void isPartialWordTest() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Dictionary dictionary = new Dictionary(appContext);

        assertEquals(true, dictionary.isPartialWord("dicti"));
        assertEquals(false, dictionary.isPartialWord("dictionn"));
        assertEquals(false, dictionary.isPartialWord("dictionaries"));
    }

    @Test
    public void isWordTest() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Dictionary dictionary = new Dictionary(appContext);

        assertEquals(false, dictionary.isWord("dicti"));
        assertEquals(false, dictionary.isWord("dictionn"));
        assertEquals(true, dictionary.isWord("dictionaries"));
    }
}