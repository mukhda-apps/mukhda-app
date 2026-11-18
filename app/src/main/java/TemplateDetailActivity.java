package com.mukhda.editor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * mAst-style template detail: big auto-playing finished-look preview,
 * song name, photo count, one big "Use" button. Picking photos enforces the
 * template's fixed photo count, then opens TemplateMakeActivity.
 */
public final class TemplateDetailActivity extends AppCompatActivity {
    public static final String EXTRA_SPEC = "spec";

    private TemplateSpec spec;
    private TemplateView preview;
    private MediaPlayer player;
    private ActivityResultLauncher<String[]> picker;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        spec = TemplateSpec.byId(getIntent().getStringExtra(EXTRA_SPEC));
        if (spec == null) { finish(); return; }
        final TemplateDetailActivity self = this;

        picker = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(),
                result -> {
                    if (result == null || result.isEmpty()) return;
                    if (result.size() < spec.photoCount) {
                        Toast.makeText(self, "Is template ke liye " + spec.photoCount
                                + " photos chahiye (" + result.size() + "/" + spec.photoCount + ")", Toast.LENGTH_LONG).show();
                        launchPicker();
                        return;
                    }
                    ArrayList<String> uris = new ArrayList<>();
                    for (int i = 0; i < spec.photoCount; i++) {
                        Uri u = result.get(i);
                        try { getContentResolver().takePersistableUriPermission(u, Intent.FLAG_GRANT_READ_URI_PERMISSION); }
                        catch (Exception ignored) {}
                        uris.add(u.toString());
                    }
                    Intent in = new Intent(self, TemplateMakeActivity.class);
                    in.putExtra(TemplateMakeActivity.EXTRA_SPEC, spec.id);
                    in.putStringArrayListExtra(TemplateMakeActivity.EXTRA_PHOTOS, uris);
                    startActivity(in);
                    finish();
                });

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        preview = new TemplateView(this);
        preview.setTemplate(spec, SamplePhotos.get(Math.max(spec.photoCount, 1)));
        root.addView(preview, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // back
        TextView back = UiKt.label(this, "\u2039", 34.0f, -1, true, 17);
        back.setPadding(UiKt.dp(this, 16), UiKt.dp(this, 10), UiKt.dp(this, 16), UiKt.dp(this, 10));
        back.setOnClickListener(v -> finish());
        root.addView(back, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // bottom info card
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(UiKt.dp(this, 20), UiKt.dp(this, 16), UiKt.dp(this, 20), UiKt.dp(this, 22));
        card.setBackground(UiKt.solid(C.INSTANCE.getCard(), UiKt.dp(this, 22), C.INSTANCE.getStroke(), UiKt.dp(this, 1)));

        TextView title = UiKt.label$default(this, spec.title, 22.0f, -1, true, 0, 16, null);
        title.setMaxLines(1);
        title.setEllipsize(TextUtils.TruncateAt.END);
        card.addView(title);

        Track tr = Music.INSTANCE.byFile(spec.musicFile);
        String song = tr == null ? "" : tr.getTitle();
        TextView meta = UiKt.label$default(this,
                "\u266a " + song + "  \u00b7  " + spec.photoCount + " photos  \u00b7  " + spec.durationSec + "s",
                13.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        meta.setPadding(0, UiKt.dp(this, 6), 0, 0);
        card.addView(meta);

        TextView use = UiKt.label(this, "Use", 18.0f, -1, true, 17);
        use.setBackground(UiKt.gradient(C.INSTANCE.getSaffron(), C.INSTANCE.getPink(),
                UiKt.dp(this, 26), GradientDrawable.Orientation.LEFT_RIGHT));
        use.setPadding(0, UiKt.dp(this, 14), 0, UiKt.dp(this, 14));
        LinearLayout.LayoutParams ulp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ulp.topMargin = UiKt.dp(this, 16);
        use.setOnClickListener(v -> launchPicker());
        card.addView(use, ulp);

        FrameLayout.LayoutParams clp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.BOTTOM);
        clp.setMargins(UiKt.dp(this, 12), 0, UiKt.dp(this, 12), UiKt.dp(this, 14));
        root.addView(card, clp);

        setContentView(root);

        // template song preview
        try {
            player = new MediaPlayer();
            player.setDataSource(this, Music.INSTANCE.uri(this, spec.musicFile));
            player.setLooping(true);
            player.prepareAsync();
            player.setOnPreparedListener(MediaPlayer::start);
        } catch (Exception ignored) { player = null; }
    }

    private void launchPicker() {
        Toast.makeText(this, "Select " + spec.photoCount + " photos", Toast.LENGTH_SHORT).show();
        picker.launch(new String[]{"image/*"});
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preview != null) preview.play();
        try { if (player != null && player.isPlaying() == false) player.start(); } catch (Exception ignored) {}
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
