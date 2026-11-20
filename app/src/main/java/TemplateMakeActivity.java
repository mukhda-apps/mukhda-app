package com.mukhda.editor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.media3.transformer.Composition;
import androidx.media3.transformer.ExportException;
import androidx.media3.transformer.ExportResult;
import androidx.media3.transformer.Transformer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * mAst-style make screen: big live preview of the finished video with the
 * user's own photos, photo-slot strip, gradient Export button, then
 * save-to-gallery + share sheet. Song, effects and filters are baked into
 * the template itself - the user only adds photos.
 */
public final class TemplateMakeActivity extends AppCompatActivity {
    public static final String EXTRA_SPEC = "spec";
    public static final String EXTRA_PHOTOS = "photos";

    private TemplateSpec spec;
    private List<String> photoUris;
    private List<Bitmap> photos;
    private List<Clip> clips;
    private String projectId;
    private TemplateView preview;
    private MediaPlayer player;
    private Track track;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        final TemplateMakeActivity self = this;
        spec = TemplateSpec.byId(getIntent().getStringExtra(EXTRA_SPEC));
        photoUris = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);
        if (photoUris == null) {
            String[] arr = getIntent().getStringArrayExtra(EXTRA_PHOTOS);
            if (arr != null) photoUris = new ArrayList<>(java.util.Arrays.asList(arr));
        }
        if (spec == null || photoUris == null || photoUris.isEmpty()) { finish(); return; }
        projectId = "make_" + spec.id + "_" + System.currentTimeMillis();
        track = Music.INSTANCE.byFile(spec.musicFile);

        clips = new ArrayList<>();
        int i = 0;
        for (String u : photoUris) {
            clips.add(new Clip("m" + (i++), u, true, spec.durationMs(), 0, spec.durationMs(), 1f, 1f));
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);

        // top bar
        LinearLayout bar = new LinearLayout(this);
        bar.setGravity(android.view.Gravity.CENTER_VERTICAL);
        TextView back = UiKt.label(this, "\u2039", 30.0f, -1, true, 17);
        back.setPadding(UiKt.dp(this, 14), UiKt.dp(this, 8), UiKt.dp(this, 14), UiKt.dp(this, 8));
        back.setOnClickListener(v -> finish());
        bar.addView(back);
        TextView ttl = UiKt.label(this, spec.title, 17.0f, -1, true, 17);
        bar.addView(ttl, UiKt.lp(0, UiKt.getWRAP(), 1.0f));
        TextView export = UiKt.label(this, "Export", 15.0f, -1, true, 17);
        export.setBackground(UiKt.gradient(C.INSTANCE.getSaffron(), C.INSTANCE.getPink(),
                UiKt.dp(this, 20), GradientDrawable.Orientation.LEFT_RIGHT));
        export.setPadding(UiKt.dp(this, 18), UiKt.dp(this, 9), UiKt.dp(this, 18), UiKt.dp(this, 9));
        LinearLayout.LayoutParams elp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        elp.rightMargin = UiKt.dp(this, 12);
        export.setOnClickListener(v -> startExport());
        bar.addView(export, elp);
        root.addView(bar);

        // preview, 9:16 centered
        FrameLayout pframe = new FrameLayout(this);
        pframe.setBackgroundColor(Color.BLACK);
        preview = new TemplateView(this);
        final FrameLayout.LayoutParams plp = new FrameLayout.LayoutParams(0, 0, android.view.Gravity.CENTER);
        pframe.addView(preview, plp);
        pframe.post(() -> {
            int fw = pframe.getWidth();
            int fh = pframe.getHeight();
            int vw = Math.min(fw, fh * 9 / 16);
            int vh = vw * 16 / 9;
            if (vh > fh) { vh = fh; vw = vh * 9 / 16; }
            plp.width = vw;
            plp.height = vh;
            preview.setLayoutParams(plp);
        });
        root.addView(pframe, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

        // photo strip
        LinearLayout photoRow = new LinearLayout(this);
        photoRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        photoRow.setPadding(UiKt.dp(this, 12), UiKt.dp(this, 6), UiKt.dp(this, 12), UiKt.dp(this, 2));
        TextView pc = UiKt.label$default(this, photoUris.size() + "/" + spec.photoCount + " photos",
                12.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        photoRow.addView(pc);
        HorizontalScrollView photoScroll = new HorizontalScrollView(this);
        photoScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout thumbs = new LinearLayout(this);
        photoScroll.addView(thumbs);
        photoRow.addView(photoScroll, UiKt.lp(0, UiKt.getWRAP(), 1.0f));
        root.addView(photoRow);

        // baked-in template info (song + duration are part of the template)
        TextView baked = UiKt.label$default(this,
                "\u266a " + (track == null ? "" : track.getTitle()) + "  \u00b7  " + spec.durationSec
                        + "s  \u00b7  effects + filter included",
                12.0f, C.INSTANCE.getDim(), false, 17, 24, null);
        baked.setPadding(0, UiKt.dp(this, 2), 0, UiKt.dp(this, 10));
        root.addView(baked);

        setContentView(root);

        // decode photos off the UI thread, then start preview + thumbnails
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Preparing preview\u2026");
        pd.setCancelable(false);
        pd.show();
        new Thread(() -> {
            final List<Bitmap> loaded = TemplatePhotos.load(self, clips, 1080, 1920);
            final List<Bitmap> small = TemplatePhotos.load(self, clips, 120, 214);
            runOnUiThread(() -> {
                UiKt.safeDismiss(self, pd);
                if (self.isFinishing() || self.isDestroyed()) return;
                photos = loaded.isEmpty() ? SamplePhotos.get(spec.photoCount) : loaded;
                preview.setTemplate(spec, photos);
                preview.play();
                for (Bitmap tb : small) {
                    ImageView iv = new ImageView(self);
                    iv.setImageBitmap(tb);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(UiKt.dp(self, 52), UiKt.dp(self, 92));
                    tlp.leftMargin = UiKt.dp(self, 8);
                    iv.setBackground(UiKt.solid(C.INSTANCE.getCard(), UiKt.dp(self, 8), C.INSTANCE.getStroke(), UiKt.dp(self, 1)));
                    iv.setClipToOutline(true);
                    thumbs.addView(iv, tlp);
                }
            });
        }).start();

        playMusic();
    }

    private void playMusic() {
        try {
            if (player != null) { player.stop(); player.release(); player = null; }
            if (track == null) return;
            Uri u = Music.INSTANCE.uri(this, track.getFile());
            if (u == null) return;
            player = new MediaPlayer();
            player.setDataSource(this, u);
            player.setLooping(true);
            player.prepareAsync();
            player.setOnPreparedListener(MediaPlayer::start);
        } catch (Exception e) {
            player = null;
        }
    }

    private static boolean isSoftwareEmulator() {
        String h = (android.os.Build.HARDWARE + "").toLowerCase();
        String f = (android.os.Build.FINGERPRINT + "").toLowerCase();
        String m = (android.os.Build.MODEL + "").toLowerCase();
        return h.contains("ranchu") || h.contains("goldfish") || f.contains("generic")
                || f.contains("emulator") || m.contains("sdk") || m.contains("emulator");
    }

    private void writeExportError(Throwable t) {
        try {
            File f = new File(getFilesDir(), "export_error.txt");
            java.io.FileOutputStream o = new java.io.FileOutputStream(f, true);
            o.write((System.currentTimeMillis() + " " + t + "\n" + android.util.Log.getStackTraceString(t) + "\n").getBytes("UTF-8"));
            o.close();
        } catch (Throwable ignored) {}
    }

    private void startExport() {
        final TemplateMakeActivity self = this;
        try { if (player != null && player.isPlaying()) player.pause(); } catch (Exception ignored) {}
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Exporting video\u2026");
        pd.setCancelable(false);
        pd.show();

        final String musicUri = track == null ? null : Music.INSTANCE.uri(this, track.getFile()).toString();
        // captionLang must be non-null: Project.<init> has an Intrinsics null check
        // (decompiled Kotlin) and export crashed with NPE on every template export.
        final Project project = new Project(projectId, spec.title, clips, new ArrayList<Caption>(),
                "reels", "hinglish", musicUri, 0.35f, spec.filter, spec.id, System.currentTimeMillis());
        // Software-GL emulators (swiftshader) cannot encode 1080p30 in any sane time;
        // drop to 360p15 there only. Real devices always get the full 1080p30 export.
        final boolean softRenderer = isSoftwareEmulator();
        android.util.Log.i("MukhdaExport", "export starting, softRenderer=" + softRenderer);
        final Composition composition;
        try {
            composition = softRenderer
                    ? Exporter.INSTANCE.buildTemplateComposition(this, project, spec, 360, 640, spec.durationMs(), 15)
                    : Exporter.INSTANCE.buildTemplateComposition(this, project, spec);
        } catch (Throwable t) {
            android.util.Log.e("MukhdaExport", "composition setup failed", t);
            writeExportError(t);
            UiKt.safeDismiss(this, pd);
            Toast.makeText(this, "Export setup failed", Toast.LENGTH_LONG).show();
            return;
        }
        final File out = new File(getFilesDir(), "exports/mukhda_" + spec.id + "_" + System.currentTimeMillis() + ".mp4");
        out.getParentFile().mkdirs();
        final ExportOptions options = new ExportOptions(Presets.INSTANCE.getAll().get(0), softRenderer ? 360 : 1080, "9:16", true, false);

        final CountDownLatch done = new CountDownLatch(1);
        final Throwable[] err = new Throwable[1];
        final HandlerThread ht = new HandlerThread("export");
        ht.start();
        final Handler handler = new Handler(ht.getLooper());
        handler.post(() -> {
            try {
                Transformer transformer = Exporter.INSTANCE.newTransformer(self, options, new Transformer.Listener() {
                    @Override
                    public void onCompleted(Composition c, ExportResult r) { done.countDown(); }

                    @Override
                    public void onError(Composition c, ExportResult r, ExportException e) {
                        android.util.Log.e("MukhdaExport", "transformer error", e);
                        writeExportError(e);
                        err[0] = e;
                        done.countDown();
                    }
                });
                transformer.start(composition, out.getAbsolutePath());
            } catch (Throwable t) {
                android.util.Log.e("MukhdaExport", "export thread error", t);
                writeExportError(t);
                err[0] = t;
                done.countDown();
            }
        });
        new Thread(() -> {
            boolean ok = false;
            try { ok = done.await(30, TimeUnit.MINUTES); } catch (InterruptedException ignored) {}
            final boolean finished = ok;
            if (!finished) {
                android.util.Log.e("MukhdaExport", "export timed out after 10 min");
                writeExportError(new RuntimeException("export timed out after 10 min"));
            }
            runOnUiThread(() -> {
                if (self.isFinishing() || self.isDestroyed()) { ht.quitSafely(); return; }
                UiKt.safeDismiss(self, pd);
                ht.quitSafely();
                if (!finished || err[0] != null || !out.exists() || out.length() == 0) {
                    Toast.makeText(self, "Export nahi ho paya. Dobara try karo.", Toast.LENGTH_LONG).show();
                    return;
                }
                Uri galleryUri = null;
                try { galleryUri = Exporter.INSTANCE.saveToGallery(self, out, out.getName()); }
                catch (Exception ignored) {}
                showDone(out, galleryUri);
            });
        }).start();
    }

    private void showDone(final File out, final Uri galleryUri) {
        final TemplateMakeActivity self = this;
        LinearLayout sheet = new LinearLayout(this);
        sheet.setOrientation(LinearLayout.VERTICAL);
        sheet.setPadding(UiKt.dp(this, 24), UiKt.dp(this, 24), UiKt.dp(this, 24), UiKt.dp(this, 28));
        sheet.setBackground(UiKt.solid(C.INSTANCE.getCard(), UiKt.dp(this, 24), 0, 0));

        TextView t = UiKt.label(this, "Video ready \u2728", 20.0f, -1, true, 17);
        sheet.addView(t);
        TextView s = UiKt.label(this, galleryUri != null
                ? "Saved in Gallery > Movies > Mukhda"
                : "Saved in app. Share karo.", 13.0f, C.INSTANCE.getDim(), false, 17);
        s.setPadding(0, UiKt.dp(this, 8), 0, 0);
        sheet.addView(s);

        TextView share = UiKt.label(this, "Share", 17.0f, -1, true, 17);
        share.setBackground(UiKt.gradient(C.INSTANCE.getSaffron(), C.INSTANCE.getPink(),
                UiKt.dp(this, 26), GradientDrawable.Orientation.LEFT_RIGHT));
        share.setPadding(0, UiKt.dp(this, 13), 0, UiKt.dp(this, 13));
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slp.topMargin = UiKt.dp(this, 18);
        share.setOnClickListener(v -> {
            try {
                Uri shareUri = galleryUri != null ? galleryUri
                        : FileProvider.getUriForFile(self, getPackageName() + ".files", out);
                Intent send = new Intent(Intent.ACTION_SEND)
                        .setType("video/mp4")
                        .putExtra(Intent.EXTRA_STREAM, shareUri)
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(send, "Share video"));
            } catch (Exception e) {
                Toast.makeText(self, "Share nahi ho paya", Toast.LENGTH_SHORT).show();
            }
        });
        sheet.addView(share, slp);

        TextView home = UiKt.label(this, "Back to Home", 14.0f, C.INSTANCE.getDim(), true, 17);
        home.setPadding(0, UiKt.dp(this, 14), 0, 0);
        home.setOnClickListener(v -> {
            Intent in = new Intent(self, MainActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });
        sheet.addView(home);

        android.app.AlertDialog dlg = new android.app.AlertDialog.Builder(this).create();
        dlg.setView(sheet);
        dlg.setCancelable(false);
        dlg.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preview != null && photos != null) preview.play();
        try { if (player != null && !player.isPlaying()) player.start(); } catch (Exception ignored) {}
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (preview != null) preview.pause();
        try { if (player != null && player.isPlaying()) player.pause(); } catch (Exception ignored) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preview != null) preview.stop();
        try { if (player != null) { player.stop(); player.release(); } } catch (Exception ignored) {}
        player = null;
    }
}
