package com.mukhda.editor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Real end-to-end QA, the way she uses the app:
 * open app -> pick photos through the REAL system picker (DocumentsUI,
 * the Media.toClips path that crashed on her phone) -> template -> export
 * -> verify the exported MP4 (duration + frames) on device.
 *
 * Evidence (screenshots, exported MP4 frames, qa_report.txt) is written to
 * the app's INTERNAL files dir so CI can pull it through /data/data
 * (FUSE external-storage views are per-app and invisible to adb root).
 */
@RunWith(AndroidJUnit4.class)
public class FullFlowQaTest {

    private static final String PKG = "com.mukhda.video";
    private static final String DOCSUI = "com.android.documentsui";
    private static final long EXPORT_WAIT_MS = 8 * 60 * 1000L;

    private static Context ctx;
    private static UiDevice device;
    private static File qaDir;
    private static StringBuilder report;

    @BeforeClass
    public static void setup() throws Exception {
        ctx = ApplicationProvider.getApplicationContext();
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        qaDir = new File(ctx.getFilesDir(), "qa");
        qaDir.mkdirs();
        report = new StringBuilder();
        log("QA start, sdk=" + android.os.Build.VERSION.SDK_INT + " release=" + android.os.Build.VERSION.RELEASE);
        seedPhotosIntoGallery(8);
    }

    // ---------------- fixtures ----------------

    /** Numbered portrait photos with a red "head" marker near the top,
     *  inserted into the real gallery (MediaStore) so the system picker shows them. */
    private static void seedPhotosIntoGallery(int count) throws Exception {
        ContentResolver cr = ctx.getContentResolver();
        int[] cols = {0xFFE91E63, 0xFF3F51B5, 0xFF009688, 0xFFFF5722, 0xFF673AB7, 0xFF4CAF50, 0xFF2196F3, 0xFFFF9800};
        for (int i = 0; i < count; i++) {
            Bitmap b = Bitmap.createBitmap(720, 1920, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            c.drawColor(cols[i % cols.length]);
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.WHITE);
            p.setTextSize(360f);
            p.setTextAlign(Paint.Align.CENTER);
            p.setFakeBoldText(true);
            c.drawText(String.valueOf(i + 1), 360, 1050, p);
            Paint head = new Paint(Paint.ANTI_ALIAS_FLAG);
            head.setColor(0xFFFF0000);
            head.setStyle(Paint.Style.FILL);
            c.drawCircle(360, 230, 60, head);

            ContentValues v = new ContentValues();
            v.put(MediaStore.Images.Media.DISPLAY_NAME, "mukhda_qa_" + i + ".jpg");
            v.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            v.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MukhdaQA");
            v.put(MediaStore.Images.Media.IS_PENDING, 1);
            Uri u = cr.insert(MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), v);
            if (u == null) { b.recycle(); fail("MediaStore insert failed for photo " + i); }
            OutputStream os = cr.openOutputStream(u);
            b.compress(Bitmap.CompressFormat.JPEG, 92, os);
            os.close();
            b.recycle();
            v.clear();
            v.put(MediaStore.Images.Media.IS_PENDING, 0);
            cr.update(u, v, null, null);
        }
        log("seeded " + count + " photos into MediaStore Pictures/MukhdaQA");
        Thread.sleep(3000); // let DocumentsUI notice them
    }

    // ---------------- helpers ----------------

    private static void log(String s) {
        report.append(s).append('\n');
        System.out.println("QA: " + s);
        try {
            FileOutputStream o = new FileOutputStream(new File(qaDir, "qa_report.txt"));
            o.write(report.toString().getBytes("UTF-8"));
            o.close();
        } catch (Exception ignored) {}
    }

    private static void shot(String name) {
        try {
            File f = new File(qaDir, name + ".png");
            boolean ok = device.takeScreenshot(f);
            log("screenshot " + name + " -> " + ok);
        } catch (Throwable t) {
            log("screenshot " + name + " failed: " + t);
        }
    }

    private static void dump(String name) {
        try {
            File f = new File(qaDir, name + ".xml");
            device.dumpWindowHierarchy(f);
            log("hierarchy dumped " + name);
        } catch (Throwable t) {
            log("dump " + name + " failed: " + t);
        }
    }

    private static boolean clickIfExists(BySelector sel, long waitMs) {
        UiObject2 o = device.wait(Until.findObject(sel), waitMs);
        if (o == null) return false;
        o.click();
        return true;
    }

    private static String crashLogContent() {
        try {
            File f = new File(ctx.getFilesDir(), "last_crash.txt");
            if (!f.exists()) return "";
            byte[] buf = new byte[(int) f.length()];
            java.io.FileInputStream in = new java.io.FileInputStream(f);
            int n = in.read(buf);
            in.close();
            return n > 0 ? new String(buf, 0, n, "UTF-8") : "";
        } catch (Exception e) {
            return "";
        }
    }

    private static void assertNoCrash(String stage, String crashBefore) {
        String now = crashLogContent();
        if (!now.equals(crashBefore)) {
            shot(stage + "_CRASH");
            fail("app crashed during " + stage + ":\n" + now);
        }
        UiObject2 dlg = device.findObject(By.pkg("android").textContains("stopping"));
        if (dlg == null) dlg = device.findObject(By.pkg("android").textContains("has stopped"));
        if (dlg != null) {
            shot(stage + "_CRASH_DIALOG");
            fail("system crash dialog during " + stage);
        }
    }

    /** Drives the real system document picker: selects the first n photos and confirms. */
    private static void pickPhotos(int n, String stage) throws Exception {
        boolean opened = device.wait(Until.hasObject(By.pkg(DOCSUI).depth(0)), 15000) != null;
        if (!opened) {
            opened = device.wait(Until.hasObject(By.pkg("com.google.android.documentsui").depth(0)), 4000) != null;
        }
        if (!opened) {
            shot(stage + "_picker_missing");
            dump(stage + "_picker_missing");
            fail("system picker did not open at " + stage);
        }
        Thread.sleep(2500);
        shot(stage + "_picker");

        // DocumentsUI's Recent view can take a while to index freshly-seeded media:
        // poll for the items, and if they never show, switch to the Images root.
        boolean triedImagesRoot = false;
        long deadline = System.currentTimeMillis() + 60000;
        while (System.currentTimeMillis() < deadline) {
            List<UiObject2> icons = findPhotoIcons();
            if (icons.size() >= n) break;
            if (!triedImagesRoot && System.currentTimeMillis() > deadline - 45000) {
                triedImagesRoot = true;
                clickIfExists(By.desc("Show roots"), 1500);
                Thread.sleep(1200);
                clickIfExists(By.text("Images"), 2500);
                Thread.sleep(1500);
            }
            Thread.sleep(2500);
        }
        List<UiObject2> icons = findPhotoIcons();
        if (icons.size() < n) {
            shot(stage + "_picker_items");
            dump(stage + "_picker_items");
            fail("picker shows only " + icons.size() + " selectable items, need " + n + " at " + stage);
        }
        // Selection strategy depends on the picker layout:
        // - card/list layouts expose a CLICKABLE "icon" node -> accessibility click toggles selection
        // - the compact image grid (icon_thumb) is not clickable; a plain tap opens preview,
        //   so enter selection with a LONG PRESS on the first item, then tap the rest.
        List<UiObject2> items = findPhotoIcons();
        boolean clickableMode = items.get(0).isClickable();
        log(stage + ": picker items=" + items.size() + " clickableMode=" + clickableMode);
        if (clickableMode) {
            for (int i = 0; i < n; i++) {
                List<UiObject2> fresh = findPhotoIcons();
                if (fresh.size() <= i) { Thread.sleep(2000); fresh = findPhotoIcons(); }
                if (fresh.size() <= i) { shot(stage + "_select_reattach"); dump(stage + "_select_reattach"); fail("lost picker items at " + stage); }
                fresh.get(i).click();
                Thread.sleep(450);
            }
        } else {
            // long-press item 0 to enter selection mode
            android.graphics.Rect b0 = items.get(0).getVisibleBounds();
            device.swipe(b0.centerX(), b0.centerY(), b0.centerX(), b0.centerY(), 120);
            Thread.sleep(800);
            if (device.wait(Until.hasObject(By.textContains("selected")), 3000) == null) {
                shot(stage + "_longpress_failed");
                dump(stage + "_longpress_failed");
                fail("long-press did not start selection at " + stage);
            }
            for (int i = 1; i < n; i++) {
                List<UiObject2> fresh = findPhotoIcons();
                if (fresh.size() <= i) { Thread.sleep(2000); fresh = findPhotoIcons(); }
                if (fresh.size() <= i) { shot(stage + "_select_reattach"); dump(stage + "_select_reattach"); fail("lost picker items at " + stage); }
                android.graphics.Rect b = fresh.get(i).getVisibleBounds();
                device.click(b.centerX(), b.centerY());
                Thread.sleep(450);
            }
        }
        shot(stage + "_picked");
        if (device.findObject(By.textContains("selected")) == null) {
            shot(stage + "_not_selected");
            dump(stage + "_not_selected");
            fail("no selection indicator after picking at " + stage);
        }
        boolean confirmed = clickIfExists(By.text("Select"), 3000);
        if (!confirmed) confirmed = clickIfExists(By.desc("Select"), 2000);
        if (!confirmed) {
            shot(stage + "_confirm_missing");
            dump(stage + "_confirm_missing");
            fail("could not confirm photo selection at " + stage);
        }
        log("picked " + n + " photos at " + stage);
    }

    private static List<UiObject2> findPhotoIcons() {
        // DocumentsUI grid item thumbnails
        List<UiObject2> l = device.findObjects(By.res(DOCSUI, "icon_thumb"));
        if (l == null || l.isEmpty()) l = device.findObjects(By.res(DOCSUI, "icon"));
        if (l == null || l.isEmpty()) {
            // fallback: content-area image views only (never toolbar buttons)
            List<UiObject2> all = device.findObjects(By.pkg(DOCSUI).clazz("android.widget.ImageView"));
            l = new java.util.ArrayList<>();
            int screenH = device.getDisplayHeight();
            for (UiObject2 o : all) {
                android.graphics.Rect b = o.getVisibleBounds();
                if (b != null && b.top > screenH / 6 && b.width() > 100) l.add(o);
            }
        }
        return l;
    }

    private static void freshStart(Intent in) throws Exception {
        device.pressHome();
        Thread.sleep(1200);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ctx.startActivity(in);
        Thread.sleep(2500);
    }

    /** Full template flow: detail -> Use -> real picker -> make -> Export -> verify MP4 + frames. */
    private static void runTemplateFlow(String specId, int photos, long expectedDurMs) throws Exception {
        String crashBefore = crashLogContent();
        Intent in = new Intent(ctx, TemplateDetailActivity.class);
        in.putExtra(TemplateDetailActivity.EXTRA_SPEC, specId);
        freshStart(in);
        assertNoCrash(specId + "_detail", crashBefore);
        shot(specId + "_detail");

        if (!clickIfExists(By.text("Use"), 8000)) {
            shot(specId + "_use_missing");
            dump(specId + "_use_missing");
            fail("Use button not found for " + specId);
        }
        pickPhotos(photos, specId);

        // make screen
        boolean makeShown = device.wait(Until.hasObject(By.text("Export")), 25000) != null;
        assertNoCrash(specId + "_make", crashBefore);
        if (!makeShown) {
            shot(specId + "_make_missing");
            dump(specId + "_make_missing");
            fail("TemplateMakeActivity did not open for " + specId);
        }
        Thread.sleep(4000); // let preview settle
        shot(specId + "_make");
        assertNoCrash(specId + "_make2", crashBefore);

        // export
        clickIfExists(By.text("Export"), 3000);
        log(specId + ": export started, waiting");
        boolean done = device.wait(Until.hasObject(By.text("Share")), EXPORT_WAIT_MS) != null;
        assertNoCrash(specId + "_export", crashBefore);
        if (!done) {
            shot(specId + "_export_timeout");
            fail("export did not finish for " + specId);
        }
        Thread.sleep(1500);
        shot(specId + "_done");

        // verify MP4 on disk
        File exports = new File(ctx.getFilesDir(), "exports");
        File mp4 = null;
        File[] list = exports.listFiles();
        if (list != null) {
            for (File f : list) {
                if (f.getName().startsWith("mukhda_" + specId + "_") && f.getName().endsWith(".mp4")) {
                    if (mp4 == null || f.lastModified() > mp4.lastModified()) mp4 = f;
                }
            }
        }
        if (mp4 == null || mp4.length() < 30000) {
            fail("exported MP4 missing/too small for " + specId + ": " + (mp4 == null ? "null" : mp4.length()));
        }
        log(specId + ": exported " + mp4.getName() + " (" + (mp4.length() / 1024) + " KB)");

        // play it back: duration + frames (head marker must survive => photos fit, not cropped)
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mp4.getAbsolutePath());
        long durMs = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        if (Math.abs(durMs - expectedDurMs) > 2500) {
            mmr.release();
            fail(specId + " duration " + durMs + "ms, expected ~" + expectedDurMs + "ms");
        }
        long[] pts = {1000, durMs / 2, Math.max(1000, durMs - 1000)};
        int maxRed = 0;
        for (int i = 0; i < pts.length; i++) {
            Bitmap fr = mmr.getFrameAtTime(pts[i] * 1000);
            if (fr == null) { mmr.release(); fail(specId + ": no frame at " + pts[i] + "ms"); }
            maxRed = Math.max(maxRed, redPixelsInTopHalf(fr));
            FileOutputStream fo = new FileOutputStream(new File(qaDir, specId + "_f" + i + ".png"));
            fr.compress(Bitmap.CompressFormat.PNG, 90, fo);
            fo.close();
            fr.recycle();
        }
        mmr.release();
        log(specId + ": duration=" + durMs + "ms frames extracted, maxRedTopHalf=" + maxRed);
        if (maxRed < 20) {
            fail(specId + ": head marker missing from exported frames (photos cropped?) maxRed=" + maxRed);
        }
        // verify it also landed in the gallery (Movies/Mukhda)
        assertTrue(specId + ": not found in MediaStore", isInGallery(mp4.getName()));
        log(specId + ": gallery copy verified");
    }

    private static boolean isInGallery(String fileName) {
        try {
            ContentResolver cr = ctx.getContentResolver();
            android.database.Cursor c = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Video.Media.DISPLAY_NAME},
                    MediaStore.Video.Media.DISPLAY_NAME + "=?", new String[]{fileName}, null);
            boolean found = c != null && c.moveToFirst();
            if (c != null) c.close();
            return found;
        } catch (Exception e) {
            return false;
        }
    }

    private static int redPixelsInTopHalf(Bitmap fr) {
        int w = fr.getWidth();
        int h = fr.getHeight();
        int red = 0;
        for (int y = 0; y < h / 2; y += 4) {
            for (int x = 0; x < w; x += 4) {
                int px = fr.getPixel(x, y);
                if (Color.red(px) > 150 && Color.green(px) < 100 && Color.blue(px) < 100) red++;
            }
        }
        return red;
    }

    // ---------------- tests ----------------

    /** Her crash path: Home "Photo to Video" -> real picker -> Media.toClips. */
    @Test
    public void mainPhotoPickerPathDoesNotCrash() throws Exception {
        String crashBefore = crashLogContent();
        freshStart(new Intent(ctx, MainActivity.class));
        Thread.sleep(3000);
        shot("main_home");
        assertNoCrash("main_home", crashBefore);

        boolean clicked = clickIfExists(By.text("Photo to Video"), 6000);
        if (!clicked) {
            shot("main_hero_missing");
            dump("main_hero_missing");
            fail("Photo to Video card not found on Home");
        }
        pickPhotos(2, "main");

        // createFrom -> Media.toClips runs here; then the editor should open
        Thread.sleep(15000);
        shot("main_after_pick");
        assertNoCrash("main_after_pick", crashBefore);
        String pkg = device.getCurrentPackageName();
        log("after main pick, foreground pkg=" + pkg);
        if (pkg == null || !pkg.equals(PKG)) {
            dump("main_after_pick_pkg");
            fail("app not in foreground after picking photos (pkg=" + pkg + ")");
        }
        log("main photo-picker path OK, no crash");
    }

    @Test
    public void template1PhotoFacePop() throws Exception {
        runTemplateFlow("new_facepop", 1, 12000);
    }

    @Test
    public void template2PhotoLoveNames() throws Exception {
        runTemplateFlow("love_names", 2, 15000);
    }

    @Test
    public void template4PhotoBirthday() throws Exception {
        runTemplateFlow("birthday", 4, 15000);
    }

    @Test
    public void template6PhotoGangStory() throws Exception {
        runTemplateFlow("fr_gang", 6, 15000);
    }
}
