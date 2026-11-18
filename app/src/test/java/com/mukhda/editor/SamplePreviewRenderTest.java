package com.mukhda.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/** Renders gallery-card style previews (SamplePhotos + template overlay) for visual inspection. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class SamplePreviewRenderTest {

    @Test
    public void renderCards() throws Exception {
        File outDir = new File("app/build/outputs/renderedFrames/cards");
        outDir.mkdirs();
        String[] ids = {"birthday", "garba_night", "dil_se", "attitude"};
        for (String id : ids) {
            TemplateSpec spec = TemplateSpec.byId(id);
            List<Bitmap> photos = SamplePhotos.get(spec.photoCount);
            Bitmap frame = Bitmap.createBitmap(540, 960, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(frame);
            TemplateEngine.drawFrame(canvas, 540, 960, spec, photos, spec.durationMs() / 3);
            File f = new File(outDir, "card_" + id + ".png");
            FileOutputStream fos = new FileOutputStream(f);
            frame.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            if (!f.exists() || f.length() < 10000) throw new AssertionError("card render too small: " + f);
        }
    }
}
