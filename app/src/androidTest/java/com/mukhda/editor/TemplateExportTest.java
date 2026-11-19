package com.mukhda.editor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.media3.transformer.Composition;
import androidx.media3.transformer.ExportException;
import androidx.media3.transformer.ExportResult;
import androidx.media3.transformer.ProgressHolder;
import androidx.media3.transformer.Transformer;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class TemplateExportTest {

    private static File writePhoto(Context ctx, int idx) throws Exception {
        int[] cols = {0xFFE91E63, 0xFF3F51B5, 0xFF009688, 0xFFFF5722, 0xFF673AB7, 0xFF4CAF50};
        // Taller than 9:16 (3:8) so cover-cropping is real, with a red "head" marker
        // near the top of the frame: if the crop cuts heads, the marker disappears.
        Bitmap b = Bitmap.createBitmap(720, 1920, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawColor(cols[idx % cols.length]);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.WHITE);
        p.setTextSize(360f);
        p.setTextAlign(Paint.Align.CENTER);
        p.setFakeBoldText(true);
        c.drawText(String.valueOf(idx + 1), 360, 1050, p);
        Paint head = new Paint(Paint.ANTI_ALIAS_FLAG);
        head.setColor(0xFFFF0000);
        c.drawCircle(360, 230, 60, head);
        File f = new File(ctx.getFilesDir(), "testphotos/p" + idx + ".jpg");
        f.getParentFile().mkdirs();
        FileOutputStream o = new FileOutputStream(f);
        b.compress(Bitmap.CompressFormat.JPEG, 92, o);
        o.close();
        b.recycle();
        return f;
    }

    private static int redPixelsInTopHalf(Bitmap fr) {
        int w = fr.getWidth();
        int h = fr.getHeight();
        int red = 0;
        for (int y = 0; y < h / 2; y += 2) {
            for (int x = 0; x < w; x += 2) {
                int px = fr.getPixel(x, y);
                if (Color.red(px) > 150 && Color.green(px) < 90 && Color.blue(px) < 90) red++;
            }
        }
        return red;
    }

    @Test
    public void exportsRealVideoWithTemplateDesign() throws Exception {
        final Context ctx = ApplicationProvider.getApplicationContext();
        final TemplateSpec spec = TemplateSpec.byId("birthday");
        assertNotNull(spec);
        List<Clip> clips = new ArrayList<>();
        for (int i = 0; i < spec.photoCount; i++) {
            File f = writePhoto(ctx, i);
            clips.add(new Clip("p" + i, Uri.fromFile(f).toString(), true,
                    spec.durationMs(), 0, spec.durationMs(), 1f, 1f));
        }
        String musicUri = Music.INSTANCE.uri(ctx, spec.musicFile).toString();
        Project project = new Project("testexport", "Test Export", clips, new ArrayList<Caption>(),
                "reels", "hinglish", musicUri, 0.35f, spec.filter, spec.id, System.currentTimeMillis());
        // CI/speed: verify the real export path at a small size and short duration.
        // Production exports still use spec.durationMs() at 30fps, 1080x1920.
        final long testDurationMs = 2000;
        final int testWidth = 360;
        final int testHeight = 640;
        final int testFrameRate = 15;
        ExportOptions options = new ExportOptions(Presets.INSTANCE.getAll().get(0), testWidth, "9:16", true, false);
        System.out.println("EXPORT-TEST stage: writing photos done, building composition");
        final Composition composition = Exporter.INSTANCE.buildTemplateComposition(ctx, project, spec, testWidth, testHeight, testDurationMs, testFrameRate);
        System.out.println("EXPORT-TEST stage: composition built");
        final File out = new File(ctx.getExternalFilesDir(null), "test_export_birthday.mp4");
        if (out.exists()) out.delete();

        final CountDownLatch done = new CountDownLatch(1);
        final Throwable[] err = new Throwable[1];
        final Transformer[] holder = new Transformer[1];
        HandlerThread ht = new HandlerThread("export");
        ht.start();
        final Handler handler = new Handler(ht.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("EXPORT-TEST stage: runnable entered");
                    holder[0] = Exporter.INSTANCE.newTransformer(ctx, options, new Transformer.Listener() {
                        @Override
                        public void onCompleted(Composition c, ExportResult r) {
                            System.out.println("EXPORT-TEST stage: onCompleted");
                            done.countDown();
                        }

                        @Override
                        public void onError(Composition c, ExportResult r, ExportException e) {
                            System.out.println("EXPORT-TEST stage: onError " + e);
                            err[0] = e;
                            done.countDown();
                        }
                    });
                    System.out.println("EXPORT-TEST stage: transformer built");
                    holder[0].start(composition, out.getAbsolutePath());
                    System.out.println("EXPORT-TEST stage: transformer started");
                } catch (Throwable t) {
                    System.out.println("EXPORT-TEST stage: runnable exception " + t);
                    err[0] = t;
                    done.countDown();
                }
            }
        });
        final ProgressHolder ph = new ProgressHolder();
        final boolean[] stopPolling = {false};
        final Runnable poller = new Runnable() {
            @Override
            public void run() {
                if (stopPolling[0]) return;
                try {
                    if (holder[0] != null) {
                        int state = holder[0].getProgress(ph);
                        System.out.println("EXPORT-TEST progress=" + ph.progress + " state=" + state);
                    } else {
                        System.out.println("EXPORT-TEST poll alive, holder still null");
                    }
                } catch (Throwable t) {
                    System.out.println("EXPORT-TEST progress poll error " + t);
                }
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(poller, 5000);
        boolean finished = done.await(4, TimeUnit.MINUTES);
        stopPolling[0] = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (holder[0] != null) holder[0].cancel();
                } catch (Throwable ignore) {
                }
            }
        });
        ht.quitSafely();
        if (!finished) fail("export timed out after 4 minutes");
        if (err[0] != null) fail("export error: " + err[0]);
        assertTrue("output file missing", out.exists());
        assertTrue("output too small: " + out.length(), out.length() > 30000);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(out.getAbsolutePath());
        String durStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long durMs = Long.parseLong(durStr);
        assertTrue("duration " + durMs + " not near " + testDurationMs,
                Math.abs(durMs - testDurationMs) < 1500);
        File framesDir = new File(ctx.getExternalFilesDir(null), "frames");
        framesDir.mkdirs();
        long[] pts = {1000, durMs / 2, Math.max(1000, durMs - 1000)};
        int maxRed = 0;
        for (int i = 0; i < pts.length; i++) {
            Bitmap fr = mmr.getFrameAtTime(pts[i] * 1000);
            assertNotNull("no frame at " + pts[i] + "ms", fr);
            maxRed = Math.max(maxRed, redPixelsInTopHalf(fr));
            FileOutputStream fo = new FileOutputStream(new File(framesDir, "export_f" + i + ".png"));
            fr.compress(Bitmap.CompressFormat.PNG, 90, fo);
            fo.close();
            fr.recycle();
        }
        assertTrue("head marker cropped out of export (red pixels top half=" + maxRed + ")",
                maxRed > 40);
        mmr.release();
        System.out.println("EXPORT-TEST pkg=" + ctx.getPackageName());
        System.out.println("EXPORT-TEST out=" + out.getAbsolutePath() + " exists=" + out.exists());
        System.out.println("EXPORT-TEST filesDir=" + ctx.getFilesDir().getAbsolutePath());
        System.out.println("EXPORT-TEST extDir=" + ctx.getExternalFilesDir(null));

        // Copy evidence into internal storage so CI can pull it via /data/data (FUSE
        // external-storage views are per-app mount namespaces, invisible to adb root).
        File ciDir = new File(ctx.getFilesDir(), "ci_export");
        ciDir.mkdirs();
        copy(out, new File(ciDir, "test_export_birthday.mp4"));
        for (int i = 0; i < 3; i++) {
            File f = new File(framesDir, "export_f" + i + ".png");
            if (f.exists()) copy(f, new File(ciDir, "export_f" + i + ".png"));
        }
        System.out.println("EXPORT-TEST ciDir=" + ciDir.getAbsolutePath() + " mp4there=" + new File(ciDir, "test_export_birthday.mp4").exists());
    }

    private static void copy(File src, File dst) throws Exception {
        java.io.FileInputStream in = new java.io.FileInputStream(src);
        java.io.FileOutputStream o = new java.io.FileOutputStream(dst);
        byte[] buf = new byte[65536];
        int n;
        while ((n = in.read(buf)) > 0) o.write(buf, 0, n);
        o.close();
        in.close();
    }

    @Test
    public void mainActivityLaunchesWithoutCrash() throws Exception {
        try (ActivityScenario<MainActivity> s = ActivityScenario.launch(MainActivity.class)) {
            s.onActivity(a -> assertNotNull(a));
            Thread.sleep(2500);
        }
    }
}
