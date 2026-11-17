package com.mukhda.editor;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MimeTypes;
import androidx.media3.transformer.Composition;
import androidx.media3.transformer.ExportException;
import androidx.media3.transformer.ExportResult;
import androidx.media3.transformer.ProgressHolder;
import androidx.media3.transformer.Transformer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.concurrent.ThreadsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.LongRange;

public final class StatusActivity extends AppCompatActivity {
    public boolean cancelled;
    public long duration;
    public List<LongRange> pendingParts;
    public LinearLayout root;
    public Transformer transformer;
    public Uri uri;
    public final Handler handler = new Handler(Looper.getMainLooper());
    public List<Long> cuts = CollectionsKt.emptyList();
    public final ArrayList<Uri> outputs = new ArrayList<>();

    public final void done() {
        getWindow().clearFlags(128);
        page();
        LinearLayout linearLayout = this.root;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout = null;
        }
        linearLayout.setGravity(1);
        LinearLayout linearLayout2 = this.root;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout2 = null;
        }
        StatusActivity statusActivity = this;
        linearLayout2.addView(UiKt.spacer(statusActivity, 50));
        LinearLayout linearLayout3 = this.root;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout3 = null;
        }
        linearLayout3.addView(UiKt.label(statusActivity, "✓", 54.0f, C.INSTANCE.getGreen(), true, 17), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout4 = this.root;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout4 = null;
        }
        TextView label = UiKt.label(statusActivity, this.outputs.size() + " part" + (this.outputs.size() > 1 ? "s" : "") + " ready", 24.0f, C.INSTANCE.getText(), true, 17);
        label.setPadding(0, UiKt.dp(statusActivity, 8), 0, UiKt.dp(statusActivity, 4));
        linearLayout4.addView(label, UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout5 = this.root;
        if (linearLayout5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout5 = null;
        }
        linearLayout5.addView(UiKt.label(statusActivity, "Saved in Gallery > Movies > Mukhda", 14.0f, C.INSTANCE.getDim(), false, 17), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout6 = this.root;
        if (linearLayout6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout6 = null;
        }
        linearLayout6.addView(UiKt.spacer(statusActivity, 24));
        LinearLayout linearLayout7 = this.root;
        if (linearLayout7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout7 = null;
        }
        linearLayout7.addView(UiKt.pillButton$default(statusActivity, "Open WhatsApp to post", C.INSTANCE.getGreen(), C.INSTANCE.getTeal(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                StatusActivity.this.shareWhatsApp();
            }
        }, 8, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout8 = this.root;
        if (linearLayout8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout8 = null;
        }
        linearLayout8.addView(UiKt.spacer(statusActivity, 10));
        LinearLayout linearLayout9 = this.root;
        if (linearLayout9 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout9 = null;
        }
        linearLayout9.addView(UiKt.pillButton$default(statusActivity, "Done", C.INSTANCE.getCard2(), C.INSTANCE.getCard2(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                StatusActivity.this.finish();
            }
        }, 8, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        Ads.onBreak$default(Ads.INSTANCE, this, "status_done", null, 4, null);
    }

    public final void exportPart(final List<LongRange> parts, final int i, final TextView title, final ProgressBar bar, boolean lite) {
        if (this.cancelled) {
            return;
        }
        if (i >= parts.size()) {
            done();
            return;
        }
        title.setText("Making part " + (i + 1) + " of " + parts.size() + "…");
        bar.setProgress(0);
        LongRange longRange = parts.get(i);
        Uri uri = this.uri;
        if (uri == null) {
            Intrinsics.throwUninitializedPropertyAccessException(MusicActivity.EXTRA_URI);
            uri = null;
        }
        String uri2 = uri.toString();
        Intrinsics.checkNotNullExpressionValue(uri2, "toString(...)");
        Project project = new Project(null, null, CollectionsKt.listOf(new Clip(null, uri2, false, this.duration, longRange.getFirst(), longRange.getLast(), 0.0f, 0.0f, 193, null)), null, null, null, null, 0.0f, null, null, 0L, 2043, null);
        ExportOptions exportOptions = new ExportOptions(Presets.INSTANCE.get("whatsapp"), 720, "orig", false, true);
        File file = new File(getCacheDir(), "out");
        file.mkdirs();
        final File file2 = new File(file, "status_" + System.currentTimeMillis() + "_" + i + ".mp4");
        try {
            Composition buildComposition = Exporter.INSTANCE.buildComposition(this, project, exportOptions);
            final Transformer newTransformer = Exporter.INSTANCE.newTransformer(this, exportOptions, new Transformer.Listener() {
                @Override
                public void onCompleted(Composition composition, ExportResult result) {
                    Handler handler;
                    Intrinsics.checkNotNullParameter(composition, "composition");
                    Intrinsics.checkNotNullParameter(result, "result");
                    handler = StatusActivity.this.handler;
                    handler.removeCallbacksAndMessages(null);
                    new Thread(() -> new StatusActivity$exportPart$t$1$onCompleted$1(i, StatusActivity.this, file2, parts, title, bar).invoke()).start();
                }

                @Override
                public void onError(Composition composition, ExportResult result, ExportException e) {
                    Handler handler;
                    Intrinsics.checkNotNullParameter(composition, "composition");
                    Intrinsics.checkNotNullParameter(result, "result");
                    Intrinsics.checkNotNullParameter(e, "e");
                    handler = StatusActivity.this.handler;
                    handler.removeCallbacksAndMessages(null);
                    file2.delete();
                    StatusActivity.this.fail(e.getMessage());
                }
            });
            this.transformer = newTransformer;
            newTransformer.start(buildComposition, file2.getAbsolutePath());
            final ProgressHolder progressHolder = new ProgressHolder();
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    Handler handler;
                    if (newTransformer.getProgress(progressHolder) == 2) {
                        bar.setProgress(progressHolder.progress);
                    }
                    handler = StatusActivity.this.handler;
                    handler.postDelayed(this, 400L);
                }
            });
        } catch (Throwable th) {
            fail(th.getMessage());
        }
    }

    public static void exportPart$default(StatusActivity statusActivity, List list, int i, TextView textView, ProgressBar progressBar, boolean z, int i2, Object obj) {
        if ((i2 & 16) != 0) {
            z = false;
        }
        statusActivity.exportPart(list, i, textView, progressBar, z);
    }

    public final void fail(String msg) {
        getWindow().clearFlags(128);
        page();
        LinearLayout linearLayout = this.root;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout = null;
        }
        StatusActivity statusActivity = this;
        linearLayout.addView(UiKt.spacer(statusActivity, 60));
        LinearLayout linearLayout2 = this.root;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout2 = null;
        }
        linearLayout2.addView(UiKt.label$default(statusActivity, "Could not finish", 24.0f, C.INSTANCE.getText(), true, 0, 16, null));
        LinearLayout linearLayout3 = this.root;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout3 = null;
        }
        if (msg == null) {
            msg = "error";
        }
        TextView label$default = UiKt.label$default(statusActivity, "This video can't be processed on this phone (" + msg + ").", 14.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default.setPadding(0, UiKt.dp(statusActivity, 8), 0, UiKt.dp(statusActivity, 20));
        linearLayout3.addView(label$default);
        LinearLayout linearLayout4 = this.root;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout4 = null;
        }
        linearLayout4.addView(UiKt.pillButton$default(statusActivity, "Back", 0, 0, 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                StatusActivity.this.finish();
            }
        }, 14, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
    }

    public final void go(List<LongRange> parts) {
        LinearLayout linearLayout;
        if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 9);
            this.pendingParts = parts;
            return;
        }
        getWindow().addFlags(128);
        page();
        this.outputs.clear();
        this.cancelled = false;
        StatusActivity statusActivity = this;
        TextView label = UiKt.label(statusActivity, "Preparing part 1 of " + parts.size() + "…", 20.0f, C.INSTANCE.getText(), true, 17);
        LinearLayout linearLayout2 = this.root;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout2 = null;
        }
        linearLayout2.addView(UiKt.spacer(statusActivity, 60));
        LinearLayout linearLayout3 = this.root;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout3 = null;
        }
        linearLayout3.addView(label, UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout4 = this.root;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout = null;
        } else {
            linearLayout = linearLayout4;
        }
        TextView label2 = UiKt.label(statusActivity, "Keep Mukhda open", 14.0f, C.INSTANCE.getDim(), false, 17);
        label2.setPadding(0, UiKt.dp(statusActivity, 6), 0, UiKt.dp(statusActivity, 20));
        linearLayout.addView(label2, UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        ProgressBar progressBar = new ProgressBar(statusActivity, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        LinearLayout linearLayout5 = this.root;
        if (linearLayout5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout5 = null;
        }
        linearLayout5.addView(progressBar, UiKt.lp$default(UiKt.getMATCH(), UiKt.dp(statusActivity, 10), 0.0f, 4, null));
        LinearLayout linearLayout6 = this.root;
        if (linearLayout6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout6 = null;
        }
        linearLayout6.addView(UiKt.spacer(statusActivity, 20));
        LinearLayout linearLayout7 = this.root;
        if (linearLayout7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout7 = null;
        }
        linearLayout7.addView(UiKt.pillButton$default(statusActivity, "Cancel", C.INSTANCE.getCard2(), C.INSTANCE.getCard2(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                Transformer transformer;
                StatusActivity.this.cancelled = true;
                transformer = StatusActivity.this.transformer;
                if (transformer != null) {
                    transformer.cancel();
                }
                StatusActivity.this.showParts(true);
            }
        }, 8, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        exportPart$default(this, parts, 0, label, progressBar, false, 16, null);
    }

    public final void page() {
        StatusActivity statusActivity = this;
        ScrollView scrollView = new ScrollView(statusActivity);
        scrollView.setBackgroundColor(C.INSTANCE.getBg());
        LinearLayout linearLayout = new LinearLayout(statusActivity);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(UiKt.dp(statusActivity, 20), UiKt.dp(statusActivity, 20), UiKt.dp(statusActivity, 20), UiKt.dp(statusActivity, 24));
        this.root = linearLayout;
        scrollView.addView(linearLayout);
        setContentView(scrollView);
    }

    public final void shareWhatsApp() {
        ArrayList<? extends Parcelable> arrayList = new ArrayList<>(this.outputs);
        Intent addFlags = new Intent(arrayList.size() > 1 ? "android.intent.action.SEND_MULTIPLE" : "android.intent.action.SEND").setType(MimeTypes.VIDEO_MP4).addFlags(1);
        Intrinsics.checkNotNullExpressionValue(addFlags, "addFlags(...)");
        if (arrayList.size() > 1) {
            addFlags.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
        } else {
            addFlags.putExtra("android.intent.extra.STREAM", arrayList.get(0));
        }
        try {
            try {
                startActivity(new Intent(addFlags).setPackage("com.whatsapp"));
            } catch (Exception unused) {
                startActivity(Intent.createChooser(addFlags, "Share parts"));
            }
        } catch (Exception unused2) {
        }
    }

    public final void showParts(boolean hasAudio) {
        LinearLayout linearLayout;
        LinearLayout linearLayout2;
        LinearLayout linearLayout3;
        page();
        final List<LongRange> parts = StatusSplitter.INSTANCE.parts(this.duration, this.cuts);
        LinearLayout linearLayout4 = this.root;
        LinearLayout linearLayout5 = null;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout4 = null;
        }
        StatusActivity statusActivity = this;
        TextView label$default = UiKt.label$default(statusActivity, "‹  Back", 15.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default.setPadding(0, 0, 0, UiKt.dp(statusActivity, 12));
        label$default.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StatusActivity.showParts$lambda$4$lambda$3(StatusActivity.this, view);
            }
        });
        linearLayout4.addView(label$default);
        LinearLayout linearLayout6 = this.root;
        if (linearLayout6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout6 = null;
        }
        linearLayout6.addView(UiKt.label$default(statusActivity, "Status Ready", 24.0f, C.INSTANCE.getText(), true, 0, 16, null));
        LinearLayout linearLayout7 = this.root;
        if (linearLayout7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout7 = null;
        }
        TextView label$default2 = UiKt.label$default(statusActivity, UiKt.fmtTime(this.duration) + " long  →  " + parts.size() + " part" + (parts.size() > 1 ? "s" : ""), 18.0f, C.INSTANCE.getGreen(), true, 0, 16, null);
        label$default2.setPadding(0, UiKt.dp(statusActivity, 8), 0, UiKt.dp(statusActivity, 12));
        linearLayout7.addView(label$default2);
        LinearLayout linearLayout8 = this.root;
        if (linearLayout8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout8 = null;
        }
        PartsBar partsBar = new PartsBar(statusActivity);
        partsBar.setDurationMs(this.duration);
        partsBar.setCuts(this.cuts);
        linearLayout8.addView(partsBar, UiKt.lp$default(UiKt.getMATCH(), UiKt.dp(statusActivity, 56), 0.0f, 4, null));
        if (!hasAudio) {
            LinearLayout linearLayout9 = this.root;
            if (linearLayout9 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("root");
                linearLayout9 = null;
            }
            TextView label$default3 = UiKt.label$default(statusActivity, "No speech found, so parts are split every 30 seconds.", 12.0f, C.INSTANCE.getDim(), false, 0, 24, null);
            label$default3.setPadding(0, UiKt.dp(statusActivity, 6), 0, 0);
            linearLayout9.addView(label$default3);
        }
        LinearLayout linearLayout10 = this.root;
        if (linearLayout10 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout10 = null;
        }
        linearLayout10.addView(UiKt.spacer(statusActivity, 12));
        int i = 0;
        for (LongRange longRange : parts) {
            int i2 = i + 1;
            LinearLayout linearLayout11 = this.root;
            if (linearLayout11 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("root");
                linearLayout3 = linearLayout5;
            } else {
                linearLayout3 = linearLayout11;
            }
            TextView label$default4 = UiKt.label$default(statusActivity, "Part " + i2 + "     " + UiKt.fmtTime(longRange.getFirst()) + " – " + UiKt.fmtTime(longRange.getLast()) + "     (" + ((longRange.getLast() - longRange.getFirst()) / 1000) + "s)", 14.0f, C.INSTANCE.getText(), false, 0, 24, null);
            label$default4.setPadding(UiKt.dp(statusActivity, 14), UiKt.dp(statusActivity, 12), UiKt.dp(statusActivity, 14), UiKt.dp(statusActivity, 12));
            label$default4.setBackground(UiKt.solid$default(C.INSTANCE.getCard(), UiKt.dp(statusActivity, 12), 0, 0, 12, null));
            LinearLayout.LayoutParams lp$default = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
            lp$default.bottomMargin = UiKt.dp(statusActivity, 8);
            Unit unit = Unit.INSTANCE;
            linearLayout3.addView(label$default4, lp$default);
            i = i2;
            linearLayout5 = null;
        }
        LinearLayout linearLayout12 = this.root;
        if (linearLayout12 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout12 = null;
        }
        linearLayout12.addView(UiKt.spacer(statusActivity, 10));
        LinearLayout linearLayout13 = this.root;
        if (linearLayout13 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout = null;
        } else {
            linearLayout = linearLayout13;
        }
        linearLayout.addView(UiKt.pillButton$default(statusActivity, "Share " + parts.size() + " part" + (parts.size() > 1 ? "s" : "") + " to WhatsApp", C.INSTANCE.getGreen(), C.INSTANCE.getTeal(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                StatusActivity.this.go(parts);
            }
        }, 8, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout14 = this.root;
        if (linearLayout14 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout2 = null;
        } else {
            linearLayout2 = linearLayout14;
        }
        TextView label$default5 = UiKt.label$default(statusActivity, "Parts are saved to Gallery > Movies > Mukhda. In WhatsApp, pick My Status in the share list, or add them from Status > gallery.", 12.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default5.setPadding(0, UiKt.dp(statusActivity, 10), 0, 0);
        linearLayout2.addView(label$default5);
    }

    public static final void showParts$lambda$4$lambda$3(StatusActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.finish();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        Uri data = getIntent().getData();
        if (data == null) {
            finish();
            return;
        }
        this.uri = data;
        page();
        LinearLayout linearLayout = this.root;
        LinearLayout linearLayout2 = null;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout = null;
        }
        StatusActivity statusActivity = this;
        linearLayout.addView(UiKt.label$default(statusActivity, "Looking for pauses…", 18.0f, C.INSTANCE.getText(), true, 0, 16, null));
        LinearLayout linearLayout3 = this.root;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
        } else {
            linearLayout2 = linearLayout3;
        }
        linearLayout2.addView(new ProgressBar(statusActivity));
        new Thread(() -> new StatusActivity$onCreate$2(this).invoke()).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.cancelled = true;
        this.handler.removeCallbacksAndMessages(null);
        Transformer transformer = this.transformer;
        if (transformer != null) {
            transformer.cancel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] res) {
        Intrinsics.checkNotNullParameter(perms, "perms");
        Intrinsics.checkNotNullParameter(res, "res");
        super.onRequestPermissionsResult(code, perms, res);
        if (code == 9) {
            List<LongRange> list = this.pendingParts;
            this.pendingParts = null;
            if ((!(res.length == 0)) && res[0] == 0 && list != null) {
                go(list);
            } else {
                Toast.makeText(this, "Mukhda needs storage access to save the parts", 1).show();
            }
        }
    }
}
