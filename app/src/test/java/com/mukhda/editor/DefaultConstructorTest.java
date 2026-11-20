package com.mukhda.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

/**
 * Regression: the decompiled Kotlin default-args synthetic constructors used to
 * throw UnsupportedOperationException("Method not decompiled") on real devices
 * (clip creation from the photo picker, project creation). They must work.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class DefaultConstructorTest {

    @Test
    public void clipSyntheticConstructorWorks() {
        // exact call shape used by Media.toClips (mask 193: id, speed, zoom defaulted)
        Clip c = new Clip(null, "file:///tmp/p0.jpg", true, 3000L, 0L, 3000L, 0f, 0f, 193, null);
        assertNotNull(c.id);
        assertTrue(c.id.length() > 0);
        assertEquals(1f, c.speed, 0.001f);
        assertEquals(1f, c.zoom, 0.001f);
        assertEquals(3000L, c.durationMs);
        assertTrue(c.isImage);
    }

    @Test
    public void projectSyntheticConstructorAllDefaults() {
        // exact call shape used by Preview/TimelineView (mask 2047: everything defaulted)
        Project p = new Project(null, null, null, null, null, null, null, 0f, null, null, 0L, 2047, null);
        assertNotNull(p.id);
        assertNotNull(p.name);
        assertNotNull(p.clips);
        assertNotNull(p.captions);
        assertNotNull(p.captionStyle);
        assertNotNull(p.captionLang);
        assertNotNull(p.filter);
        assertTrue(p.updatedAt > 0);
    }

    @Test
    public void projectSyntheticConstructorCreateFromShape() {
        // exact call shape used by MainActivity$createFrom$1 (mask 2041)
        ArrayList<Clip> clips = new ArrayList<Clip>();
        clips.add(new Clip(null, "file:///tmp/p0.jpg", true, 3000L, 0L, 3000L, 0f, 0f, 193, null));
        Project p = new Project(null, "Project 20 Nov", clips, null, null, null, null, 0f, null, null, 0L, 2041, null);
        assertNotNull(p.id);
        assertEquals("Project 20 Nov", p.name);
        assertEquals(1, p.clips.size());
        assertNotNull(p.captions);
        assertEquals(1f, p.originalVolume, 0.001f);
    }
}
