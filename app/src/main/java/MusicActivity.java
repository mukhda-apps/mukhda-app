package com.mukhda.editor;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

public final class MusicActivity extends AppCompatActivity {
    public static final String EXTRA_LANG = "lang";
    public static final String EXTRA_SELECTED = "selected";
    public static final String EXTRA_TEMPLATE_SONG = "tsong";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TRACK = "track";
    public static final String EXTRA_URI = "uri";
    public LinearLayout chips;
    public String lang = "Hindi";
    public LinearLayout list;
    public final ActivityResultLauncher<String[]> pickPhone;
    public MediaPlayer player;
    public String playing;
    public String selected;

    public MusicActivity() {
        ActivityResultLauncher<String[]> registerForActivityResult = registerForActivityResult(new ActivityResultContracts.OpenDocument(), new ActivityResultCallback() {
            @Override
            public final void onActivityResult(Object obj) {
                MusicActivity.pickPhone$lambda$0(MusicActivity.this, (Uri) obj);
            }
        });
        Intrinsics.checkNotNullExpressionValue(registerForActivityResult, "registerForActivityResult(...)");
        this.pickPhone = registerForActivityResult;
    }

    public final void done(String track) {
        stop();
        setResult(-1, new Intent().putExtra(EXTRA_TRACK, track));
        finish();
    }

    public static final void pickPhone$lambda$0(MusicActivity this$0, Uri uri) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (uri == null) {
            return;
        }
        try {
            Media.INSTANCE.persist(this$0, uri);
        } catch (Throwable unused) {
        }
        this$0.setResult(-1, new Intent().putExtra(EXTRA_URI, uri.toString()));
        this$0.finish();
    }

    public final void render() {
        int i;
        int i2;
        LinearLayout linearLayout = this.chips;
        Object obj = null;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("chips");
            linearLayout = null;
        }
        linearLayout.removeAllViews();
        Iterator<String> it = Music.INSTANCE.getLanguages().iterator();
        while (true) {
            i = 8;
            i2 = 4;
            if (!it.hasNext()) {
                break;
            }
            final String next = it.next();
            LinearLayout linearLayout2 = this.chips;
            if (linearLayout2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("chips");
                linearLayout2 = null;
            }
            MusicActivity musicActivity = this;
            TextView chip = UiKt.chip(musicActivity, next, Intrinsics.areEqual(next, this.lang), new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    MusicActivity.this.lang = next;
                    MusicActivity.this.render();
                }
            });
            LinearLayout.LayoutParams lp$default = UiKt.lp$default(UiKt.getWRAP(), UiKt.getWRAP(), 0.0f, 4, null);
            lp$default.rightMargin = UiKt.dp(musicActivity, 8);
            Unit unit = Unit.INSTANCE;
            linearLayout2.addView(chip, lp$default);
        }
        LinearLayout linearLayout3 = this.list;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("list");
            linearLayout3 = null;
        }
        linearLayout3.removeAllViews();
        for (final Track track : Music.INSTANCE.inLanguage(this.lang)) {
            MusicActivity musicActivity2 = this;
            LinearLayout linearLayout4 = new LinearLayout(musicActivity2);
            linearLayout4.setGravity(16);
            linearLayout4.setPadding(UiKt.dp(musicActivity2, 12), UiKt.dp(musicActivity2, 10), UiKt.dp(musicActivity2, 12), UiKt.dp(musicActivity2, 10));
            linearLayout4.setBackground(UiKt.solid(C.INSTANCE.getCard(), UiKt.dp(musicActivity2, 14), Intrinsics.areEqual(track.getFile(), this.selected) ? C.INSTANCE.getSaffron() : C.INSTANCE.getStroke(), UiKt.dp(musicActivity2, 1)));
            TextView textView = new TextView(musicActivity2);
            textView.setText(Intrinsics.areEqual(this.playing, track.getFile()) ? "❚❚" : "▶");
            textView.setTextSize(16.0f);
            textView.setGravity(17);
            textView.setTextColor(C.INSTANCE.getSaffron());
            textView.setBackground(UiKt.solid$default(C.INSTANCE.getCard2(), UiKt.dp(musicActivity2, 20), 0, 0, 12, null));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    MusicActivity.render$lambda$15$lambda$14(MusicActivity.this, track, view);
                }
            });
            linearLayout4.addView(textView, UiKt.lp$default(UiKt.dp(musicActivity2, 40), UiKt.dp(musicActivity2, 40), 0.0f, i2, obj));
            LinearLayout linearLayout5 = new LinearLayout(musicActivity2);
            linearLayout5.setOrientation(1);
            linearLayout5.setPadding(UiKt.dp(musicActivity2, 12), 0, UiKt.dp(musicActivity2, i), 0);
            linearLayout5.addView(UiKt.label$default(musicActivity2, track.getTitle(), 15.0f, C.INSTANCE.getText(), true, 0, 16, null));
            TextView label$default = UiKt.label$default(musicActivity2, CollectionsKt.joinToString(track.getLangs(), " · ", "", "", -1, "...", null) + "  ·  " + track.getMood() + "  ·  " + track.getBpm() + " BPM", 11.0f, C.INSTANCE.getDim(), false, 0, 24, null);
            label$default.setPadding(0, UiKt.dp(musicActivity2, 2), 0, 0);
            linearLayout5.addView(label$default);
            linearLayout4.addView(linearLayout5, UiKt.lp(0, UiKt.getWRAP(), 1.0f));
            linearLayout4.addView(UiKt.chip(musicActivity2, "Use", true, new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    MusicActivity.this.done(track.getFile());
                }
            }));
            LinearLayout linearLayout6 = this.list;
            if (linearLayout6 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("list");
                linearLayout6 = null;
            }
            i2 = 4;
            LinearLayout.LayoutParams lp$default2 = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
            i = 8;
            lp$default2.bottomMargin = UiKt.dp(musicActivity2, 8);
            Unit unit2 = Unit.INSTANCE;
            linearLayout6.addView(linearLayout4, lp$default2);
            obj = null;
        }
    }

    public static final void render$lambda$15$lambda$14(MusicActivity this$0, Track t, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(t, "$t");
        this$0.toggle(t);
    }

    public final void stop() {
        try {
            MediaPlayer mediaPlayer = this.player;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            MediaPlayer mediaPlayer2 = this.player;
            if (mediaPlayer2 != null) {
                mediaPlayer2.release();
            }
        } catch (Throwable unused) {
        }
        this.player = null;
        this.playing = null;
    }

    public final void toggle(Track t) {
        String str = this.playing;
        stop();
        if (Intrinsics.areEqual(str, t.getFile())) {
            render();
            return;
        }
        try {
            AssetFileDescriptor openFd = getAssets().openFd("music/" + t.getFile() + ".m4a");
            Intrinsics.checkNotNullExpressionValue(openFd, "openFd(...)");
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(openFd.getFileDescriptor(), openFd.getStartOffset(), openFd.getLength());
            openFd.close();
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            this.player = mediaPlayer;
            this.playing = t.getFile();
        } catch (Throwable unused) {
            Toast.makeText(this, "Could not play this song", 0).show();
        }
        render();
    }

    @Override
    public void onCreate(Bundle b) {
        LinearLayout linearLayout;
        float f;
        super.onCreate(b);
        this.selected = getIntent().getStringExtra(EXTRA_SELECTED);
        Track byFile = Music.INSTANCE.byFile(this.selected);
        if (byFile != null) {
            this.lang = (String) CollectionsKt.first((List) byFile.getLangs());
        }
        String stringExtra = getIntent().getStringExtra(EXTRA_LANG);
        if (stringExtra != null) {
            this.lang = stringExtra;
        }
        MusicActivity musicActivity = this;
        LinearLayout linearLayout2 = new LinearLayout(musicActivity);
        linearLayout2.setOrientation(1);
        linearLayout2.setBackgroundColor(C.INSTANCE.getBg());
        linearLayout2.setPadding(UiKt.dp(musicActivity, 16), UiKt.dp(musicActivity, 24), UiKt.dp(musicActivity, 16), UiKt.dp(musicActivity, 8));
        String stringExtra2 = getIntent().getStringExtra(EXTRA_TITLE);
        if (stringExtra2 == null) {
            stringExtra2 = "Choose a song";
        }
        linearLayout2.addView(UiKt.label$default(musicActivity, stringExtra2, 24.0f, C.INSTANCE.getText(), true, 0, 16, null));
        TextView label$default = UiKt.label$default(musicActivity, "Free songs made for Mukhda. Tap ▶ to listen.", 12.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default.setPadding(0, UiKt.dp(musicActivity, 4), 0, UiKt.dp(musicActivity, 10));
        linearLayout2.addView(label$default);
        if (!getIntent().getBooleanExtra(EXTRA_TEMPLATE_SONG, false) || this.selected == null) {
            linearLayout = null;
            f = 0.0f;
        } else {
            linearLayout = null;
            f = 0.0f;
            linearLayout2.addView(UiKt.pillButton$default(musicActivity, "Use template song  ➜", 0, 0, 0, new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    String str;
                    MusicActivity musicActivity2 = MusicActivity.this;
                    str = musicActivity2.selected;
                    Intrinsics.checkNotNull(str);
                    musicActivity2.done(str);
                }
            }, 14, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        }
        TextView pillButton$default = UiKt.pillButton$default(musicActivity, "📁  Pick a song from my phone", C.INSTANCE.getCard2(), C.INSTANCE.getCard2(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                ActivityResultLauncher activityResultLauncher;
                activityResultLauncher = MusicActivity.this.pickPhone;
                activityResultLauncher.launch(new String[]{"audio/*"});
            }
        }, 8, null);
        LinearLayout.LayoutParams lp$default = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), f, 4, linearLayout);
        lp$default.topMargin = UiKt.dp(musicActivity, 10);
        Unit unit = Unit.INSTANCE;
        linearLayout2.addView(pillButton$default, lp$default);
        this.chips = new LinearLayout(musicActivity);
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(musicActivity);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout3 = this.chips;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("chips");
            linearLayout3 = linearLayout;
        }
        horizontalScrollView.addView(linearLayout3);
        LinearLayout.LayoutParams lp$default2 = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), f, 4, linearLayout);
        lp$default2.topMargin = UiKt.dp(musicActivity, 14);
        Unit unit2 = Unit.INSTANCE;
        linearLayout2.addView(horizontalScrollView, lp$default2);
        LinearLayout linearLayout4 = new LinearLayout(musicActivity);
        linearLayout4.setOrientation(1);
        this.list = linearLayout4;
        ScrollView scrollView = new ScrollView(musicActivity);
        LinearLayout linearLayout5 = this.list;
        if (linearLayout5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("list");
            linearLayout5 = linearLayout;
        }
        scrollView.addView(linearLayout5);
        LinearLayout.LayoutParams lp = UiKt.lp(UiKt.getMATCH(), 0, 1.0f);
        lp.topMargin = UiKt.dp(musicActivity, 8);
        Unit unit3 = Unit.INSTANCE;
        linearLayout2.addView(scrollView, lp);
        TextView pillButton$default2 = UiKt.pillButton$default(musicActivity, "Cancel", C.INSTANCE.getCard(), C.INSTANCE.getCard(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                MusicActivity.this.setResult(0);
                MusicActivity.this.finish();
            }
        }, 8, null);
        LinearLayout.LayoutParams lp$default3 = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), f, 4, linearLayout);
        lp$default3.topMargin = UiKt.dp(musicActivity, 6);
        Unit unit4 = Unit.INSTANCE;
        linearLayout2.addView(pillButton$default2, lp$default3);
        setContentView(linearLayout2);
        render();
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.playing != null) {
            stop();
            if (this.list != null) {
                render();
            }
        }
    }
}
