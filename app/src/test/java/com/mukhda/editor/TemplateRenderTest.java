package com.mukhda.editor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders real frames of every template through TemplateEngine and writes PNGs
 * to app/build/outputs/renderedFrames for visual inspection. Also asserts the
 * frames are non-blank and actually animate (frames differ across time).
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class TemplateRenderTest {

    private static final int W = 540;
    private static final int H = 960;

    private static Bitmap synthPhoto(int idx) {
        int[] cols = {0xFFE91E63, 0xFF3F51B5, 0xFF009688, 0xFFFF5722, 0xFF673AB7,
                0xFF4CAF50, 0xFFFFC107, 0xFF2196F3, 0xFF795548, 0xFF607D8B,
                0xFF9C27B0, 0xFFCDDC39, 0xFF00BCD4, 0xFFFF9800, 0xFF8BC34A};
        Bitmap b = Bitmap.createBitmap(600, 900, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawColor(cols[idx % cols.length]);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0x33FFFFFF);
        c.drawCircle(300 + (idx % 3 - 1) * 150, 380, 180, p);
        p.setColor(Color.WHITE);
        p.setTextSize(280f);
        p.setTextAlign(Paint.Align.CENTER);
        p.setFakeBoldText(true);
        c.drawText(String.valueOf(idx + 1), 300, 520, p);
        return b;
    }

    private static long colorfulness(Bitmap b) {
        // sample a grid; count distinct-ish colors to prove real content
        java.util.HashSet<Integer> seen = new java.util.HashSet<>();
        for (int y = 0; y < b.getHeight(); y += 24) {
            for (int x = 0; x < b.getWidth(); x += 24) {
                seen.add(b.getPixel(x, y) & 0x00FFFFFF);
            }
        }
        return seen.size();
    }

    private static long diffCount(Bitmap a, Bitmap b) {
        long diff = 0;
        for (int y = 0; y < a.getHeight(); y += 16) {
            for (int x = 0; x < a.getWidth(); x += 16) {
                if (a.getPixel(x, y) != b.getPixel(x, y)) diff++;
            }
        }
        return diff;
    }

    @Test
    public void renderAllTemplatesAndDumpFrames() throws Exception {
        File outDir = new File("build/outputs/renderedFrames");
        outDir.mkdirs();
        StringBuilder report = new StringBuilder();
        for (TemplateSpec spec : TemplateSpec.ALL) {
            List<Bitmap> photos = new ArrayList<>();
            for (int i = 0; i < spec.photoCount; i++) photos.add(synthPhoto(i));
            long dur = spec.durationMs();
            long[] times = {dur / 10, dur / 2, (dur * 9) / 10};
            Bitmap first = null, mid = null;
            for (int ti = 0; ti < times.length; ti++) {
                Bitmap frame = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(frame);
                TemplateEngine.fxOverride = null;
                TemplateEngine.filterOverride = null;
                TemplateEngine.drawFrame(canvas, W, H, spec, photos, times[ti]);
                File f = new File(outDir, spec.id + "_t" + ti + ".png");
                FileOutputStream fos = new FileOutputStream(f);
                frame.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
                if (ti == 0) first = frame;
                if (ti == 1) mid = frame;
                long colors = colorfulness(frame);
                if (colors < 8) {
                    fail("template " + spec.id + " t" + ti + " looks blank (distinct colors=" + colors + ")");
                }
            }
            long diff = diffCount(first, mid);
            report.append(spec.id).append(" animDiff=").append(diff).append('\n');
            if (diff < 50) {
                fail("template " + spec.id + " frames do not animate (diff=" + diff + ")");
            }
        }
        File rf = new File(outDir, "_report.txt");
        FileOutputStream fos = new FileOutputStream(rf);
        fos.write(report.toString().getBytes("UTF-8"));
        fos.close();
        assertTrue(true);
    }
}
