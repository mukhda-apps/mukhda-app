package com.mukhda.editor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

/** Launch MainActivity with the real manifest/resources: catches theme and resource crashes. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class MainActivityLaunchTest {

    @Test
    public void mainActivityCreatesWithoutCrash() {
        try (ActivityController<MainActivity> c = Robolectric.buildActivity(MainActivity.class)) {
            MainActivity a = c.create().get();
            assertNotNull(a);
            c.resume();
            assertTrue(a.getWindow() != null);
        }
    }
}
