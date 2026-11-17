package com.mukhda.editor;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.concurrent.ThreadsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

public final class ExportActivity extends AppCompatActivity {
    public boolean lite;
    public File outFile;
    public boolean pendingSave;
    public ProgressBar progressBar;
    public TextView progressText;
    public Project project;
    public boolean retriedLite;
    public LinearLayout root;
    public Uri savedUri;
    public Transformer transformer;
    public Preset preset = Presets.INSTANCE.getAll().get(0);
    public int heightCap = 1080;
    public String aspect = "orig";
    public boolean fill = true;
    public final Handler handler = new Handler(Looper.getMainLooper());

    public final void failed(Throwable e, ExportOptions o) {
        File file = this.outFile;
        if (file != null) {
            file.delete();
        }
        if (!this.retriedLite && (o.getHeightCap() > 720 || !o.getLite())) {
            this.retriedLite = true;
            Toast.makeText(this, "Retrying at 720p for this phone…", 1).show();
            runExport(new ExportOptions(o.getPreset(), 720, o.getAspect(), o.getFill(), true));
            return;
        }
        page();
        LinearLayout linearLayout = this.root;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout = null;
        }
        ExportActivity exportActivity = this;
        linearLayout.addView(UiKt.spacer(exportActivity, 60));
        LinearLayout linearLayout2 = this.root;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout2 = null;
        }
        linearLayout2.addView(UiKt.label$default(exportActivity, "Export did not finish", 24.0f, C.INSTANCE.getText(), true, 0, 16, null));
        LinearLayout linearLayout3 = this.root;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout3 = null;
        }
        String message = e.getMessage();
        if (message == null) {
            message = "error";
        }
        TextView label$default = UiKt.label$default(exportActivity, "Something in this video can't be processed on this phone (" + message + "). Try removing the last clip you added, or turn on Lite Mode.", 14.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default.setPadding(0, UiKt.dp(exportActivity, 8), 0, UiKt.dp(exportActivity, 20));
        linearLayout3.addView(label$default);
        LinearLayout linearLayout4 = this.root;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout4 = null;
        }
        linearLayout4.addView(UiKt.pillButton$default(exportActivity, "Try again", 0, 0, 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                ExportActivity.this.retriedLite = false;
                ExportActivity.this.showOptions();
            }
        }, 14, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
    }

    public final void finishExport(File f) {
        getWindow().clearFlags(128);
        TextView textView = this.progressText;
        if (textView != null) {
            textView.setText("Saving…");
        }
        new Thread(() -> new ExportActivity$finishExport$1(this, f).invoke()).start();
    }

    public final View flow(List<? extends View> chips) {
        ExportActivity exportActivity = this;
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(exportActivity);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout = new LinearLayout(exportActivity);
        for (View view : chips) {
            LinearLayout.LayoutParams lp$default = UiKt.lp$default(UiKt.getWRAP(), UiKt.getWRAP(), 0.0f, 4, null);
            lp$default.rightMargin = UiKt.dp(exportActivity, 8);
            lp$default.topMargin = UiKt.dp(exportActivity, 6);
            Unit unit = Unit.INSTANCE;
            linearLayout.addView(view, lp$default);
        }
        horizontalScrollView.addView(linearLayout);
        return horizontalScrollView;
    }

    public final ExportOptions options() {
        return new ExportOptions(this.preset, this.lite ? Math.min(this.heightCap, 720) : this.heightCap, this.aspect, this.fill, this.lite);
    }

    public final LinearLayout page() {
        ExportActivity exportActivity = this;
        ScrollView scrollView = new ScrollView(exportActivity);
        scrollView.setBackgroundColor(C.INSTANCE.getBg());
        LinearLayout linearLayout = new LinearLayout(exportActivity);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(UiKt.dp(exportActivity, 20), UiKt.dp(exportActivity, 20), UiKt.dp(exportActivity, 20), UiKt.dp(exportActivity, 24));
        this.root = linearLayout;
        scrollView.addView(linearLayout);
        setContentView(scrollView);
        LinearLayout linearLayout2 = this.root;
        if (linearLayout2 != null) {
            return linearLayout2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("root");
        return null;
    }

    public final void poll() {
        int progress;
        Transformer transformer = this.transformer;
        if (transformer == null) {
            return;
        }
        ProgressHolder progressHolder = new ProgressHolder();
        int progress2 = transformer.getProgress(progressHolder);
        if (progress2 != 0) {
            if (progress2 == 2) {
                progress = progressHolder.progress;
            } else {
                ProgressBar progressBar = this.progressBar;
                progress = progressBar != null ? progressBar.getProgress() : 0;
            }
            ProgressBar progressBar2 = this.progressBar;
            if (progressBar2 != null) {
                progressBar2.setProgress(progress);
            }
            TextView textView = this.progressText;
            if (textView != null) {
                textView.setText(progress + "%");
            }
        }
        this.handler.postDelayed(new Runnable() {
            @Override
            public final void run() {
                ExportActivity.poll$lambda$27(ExportActivity.this);
            }
        }, 400L);
    }

    public static final void poll$lambda$27(ExportActivity this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.poll();
    }

    public final void runExport(final ExportOptions o) {
        getWindow().addFlags(128);
        page();
        LinearLayout linearLayout = this.root;
        Project project = null;
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
        ExportActivity exportActivity = this;
        linearLayout2.addView(UiKt.spacer(exportActivity, 60));
        LinearLayout linearLayout3 = this.root;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout3 = null;
        }
        linearLayout3.addView(UiKt.label(exportActivity, "Exporting…", 26.0f, C.INSTANCE.getText(), true, 17), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout4 = this.root;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout4 = null;
        }
        TextView label = UiKt.label(exportActivity, "Keep Mukhda open until it finishes", 14.0f, C.INSTANCE.getDim(), false, 17);
        label.setPadding(0, UiKt.dp(exportActivity, 6), 0, UiKt.dp(exportActivity, 24));
        linearLayout4.addView(label, UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        ProgressBar progressBar = new ProgressBar(exportActivity, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgressTintList(ColorStateList.valueOf(C.INSTANCE.getSaffron()));
        this.progressBar = progressBar;
        LinearLayout linearLayout5 = this.root;
        if (linearLayout5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout5 = null;
        }
        linearLayout5.addView(this.progressBar, UiKt.lp$default(UiKt.getMATCH(), UiKt.dp(exportActivity, 10), 0.0f, 4, null));
        TextView label2 = UiKt.label(exportActivity, "0%", 16.0f, C.INSTANCE.getText(), true, 17);
        label2.setPadding(0, UiKt.dp(exportActivity, 12), 0, 0);
        this.progressText = label2;
        LinearLayout linearLayout6 = this.root;
        if (linearLayout6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout6 = null;
        }
        linearLayout6.addView(this.progressText, UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout7 = this.root;
        if (linearLayout7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout7 = null;
        }
        linearLayout7.addView(UiKt.spacer(exportActivity, 24));
        LinearLayout linearLayout8 = this.root;
        if (linearLayout8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout8 = null;
        }
        linearLayout8.addView(UiKt.pillButton$default(exportActivity, "Cancel", C.INSTANCE.getCard2(), C.INSTANCE.getCard2(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                Transformer transformer;
                Handler handler;
                transformer = ExportActivity.this.transformer;
                if (transformer != null) {
                    transformer.cancel();
                }
                handler = ExportActivity.this.handler;
                handler.removeCallbacksAndMessages(null);
                ExportActivity.this.showOptions();
            }
        }, 8, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        File file = new File(getCacheDir(), "out");
        file.mkdirs();
        final File file2 = new File(file, "mukhda_" + System.currentTimeMillis() + ".mp4");
        this.outFile = file2;
        try {
            Exporter exporter = Exporter.INSTANCE;
            ExportActivity exportActivity2 = this;
            Project project2 = this.project;
            if (project2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
            } else {
                project = project2;
            }
            Composition buildComposition = exporter.buildComposition(exportActivity2, project, o);
            Transformer newTransformer = Exporter.INSTANCE.newTransformer(this, o, new Transformer.Listener() {
                @Override
                public void onCompleted(Composition composition, ExportResult result) {
                    Handler handler;
                    Intrinsics.checkNotNullParameter(composition, "composition");
                    Intrinsics.checkNotNullParameter(result, "result");
                    handler = ExportActivity.this.handler;
                    handler.removeCallbacksAndMessages(null);
                    ExportActivity.this.finishExport(file2);
                }

                @Override
                public void onError(Composition composition, ExportResult result, ExportException e) {
                    Handler handler;
                    Intrinsics.checkNotNullParameter(composition, "composition");
                    Intrinsics.checkNotNullParameter(result, "result");
                    Intrinsics.checkNotNullParameter(e, "e");
                    handler = ExportActivity.this.handler;
                    handler.removeCallbacksAndMessages(null);
                    ExportActivity.this.failed(e, o);
                }
            });
            this.transformer = newTransformer;
            newTransformer.start(buildComposition, file2.getAbsolutePath());
            poll();
        } catch (Throwable th) {
            failed(th, o);
        }
    }

    public final void selectPreset(Preset p) {
        this.preset = p;
        this.aspect = p.getAspect();
        this.heightCap = p.getHeight();
        this.fill = !Intrinsics.areEqual(p.getAspect(), "orig");
    }

    public final void share(Uri uri, String pkg) {
        Intent addFlags = new Intent("android.intent.action.SEND").setType(MimeTypes.VIDEO_MP4).putExtra("android.intent.extra.STREAM", uri).addFlags(1);
        Intrinsics.checkNotNullExpressionValue(addFlags, "addFlags(...)");
        try {
            startActivity(pkg != null ? addFlags.setPackage(pkg) : Intent.createChooser(addFlags, "Share video"));
        } catch (Exception unused) {
            if (pkg != null) {
                Toast.makeText(this, "WhatsApp is not installed", 0).show();
            }
        }
    }

    public final void showDone(File f, Uri uri) {
        String str;
        final Uri uri2;
        LinearLayout linearLayout;
        LinearLayout linearLayout2;
        LinearLayout linearLayout3;
        page();
        LinearLayout linearLayout4 = this.root;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout4 = null;
        }
        linearLayout4.setGravity(1);
        LinearLayout linearLayout5 = this.root;
        if (linearLayout5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout5 = null;
        }
        ExportActivity exportActivity = this;
        linearLayout5.addView(UiKt.spacer(exportActivity, 50));
        LinearLayout linearLayout6 = this.root;
        if (linearLayout6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout6 = null;
        }
        linearLayout6.addView(UiKt.label(exportActivity, "✓", 54.0f, C.INSTANCE.getGreen(), true, 17), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout7 = this.root;
        if (linearLayout7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout7 = null;
        }
        TextView label = UiKt.label(exportActivity, "Your video is ready", 24.0f, C.INSTANCE.getText(), true, 17);
        label.setPadding(0, UiKt.dp(exportActivity, 10), 0, UiKt.dp(exportActivity, 4));
        linearLayout7.addView(label, UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout8 = this.root;
        if (linearLayout8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout8 = null;
        }
        if (uri != null) {
            str = "Saved in Gallery > Movies > Mukhda  ·  " + UiKt.fmtSize(f.length());
        } else {
            str = "Could not save to Gallery. You can still share it.";
        }
        linearLayout8.addView(UiKt.label(exportActivity, str, 14.0f, C.INSTANCE.getDim(), false, 17), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout9 = this.root;
        if (linearLayout9 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout9 = null;
        }
        linearLayout9.addView(UiKt.spacer(exportActivity, 26));
        if (uri == null) {
            uri2 = FileProvider.getUriForFile(exportActivity, getPackageName() + ".files", f);
        } else {
            uri2 = uri;
        }
        LinearLayout linearLayout10 = this.root;
        if (linearLayout10 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout = null;
        } else {
            linearLayout = linearLayout10;
        }
        linearLayout.addView(UiKt.pillButton$default(exportActivity, "Share", 0, 0, 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                ExportActivity exportActivity2 = ExportActivity.this;
                Uri shareUri = uri2;
                Intrinsics.checkNotNullExpressionValue(shareUri, "$shareUri");
                exportActivity2.share(shareUri, null);
            }
        }, 14, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout11 = this.root;
        if (linearLayout11 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout11 = null;
        }
        linearLayout11.addView(UiKt.spacer(exportActivity, 10));
        LinearLayout linearLayout12 = this.root;
        if (linearLayout12 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout2 = null;
        } else {
            linearLayout2 = linearLayout12;
        }
        linearLayout2.addView(UiKt.pillButton(exportActivity, "WhatsApp", C.INSTANCE.getGreen(), C.INSTANCE.getGreen(), -1, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                ExportActivity exportActivity2 = ExportActivity.this;
                Uri shareUri = uri2;
                Intrinsics.checkNotNullExpressionValue(shareUri, "$shareUri");
                exportActivity2.share(shareUri, "com.whatsapp");
            }
        }), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout13 = this.root;
        if (linearLayout13 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout13 = null;
        }
        linearLayout13.addView(UiKt.spacer(exportActivity, 10));
        LinearLayout linearLayout14 = this.root;
        if (linearLayout14 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout3 = null;
        } else {
            linearLayout3 = linearLayout14;
        }
        linearLayout3.addView(UiKt.pillButton$default(exportActivity, "Play video", C.INSTANCE.getCard2(), C.INSTANCE.getCard2(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                ExportActivity.this.startActivity(new Intent("android.intent.action.VIEW").setDataAndType(uri2, MimeTypes.VIDEO_MP4).addFlags(1));
            }
        }, 8, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        LinearLayout linearLayout15 = this.root;
        if (linearLayout15 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout15 = null;
        }
        linearLayout15.addView(UiKt.spacer(exportActivity, 10));
        LinearLayout linearLayout16 = this.root;
        if (linearLayout16 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout16 = null;
        }
        linearLayout16.addView(UiKt.pillButton$default(exportActivity, "Done", C.INSTANCE.getCard2(), C.INSTANCE.getCard2(), 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                ExportActivity.this.finish();
            }
        }, 8, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
        Ads.onBreak$default(Ads.INSTANCE, this, "export_done", null, 4, null);
    }

    public final void showOptions() {
        LinearLayout linearLayout;
        LinearLayout linearLayout2;
        LinearLayout linearLayout3;
        LinearLayout linearLayout4;
        LinearLayout linearLayout5;
        LinearLayout linearLayout6;
        page();
        LinearLayout linearLayout7 = this.root;
        if (linearLayout7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout7 = null;
        }
        ExportActivity exportActivity = this;
        TextView label$default = UiKt.label$default(exportActivity, "‹  Back", 15.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default.setPadding(0, 0, 0, UiKt.dp(exportActivity, 12));
        label$default.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ExportActivity.showOptions$lambda$4$lambda$3(ExportActivity.this, view);
            }
        });
        linearLayout7.addView(label$default);
        LinearLayout linearLayout8 = this.root;
        if (linearLayout8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout8 = null;
        }
        linearLayout8.addView(UiKt.label$default(exportActivity, "Export", 28.0f, C.INSTANCE.getText(), true, 0, 16, null));
        LinearLayout linearLayout9 = this.root;
        if (linearLayout9 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout9 = null;
        }
        Project project = this.project;
        if (project == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project = null;
        }
        String name = project.getName();
        Project project2 = this.project;
        if (project2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project2 = null;
        }
        TextView label$default2 = UiKt.label$default(exportActivity, name + "  ·  " + UiKt.fmtTime(project2.getTotalMs()), 14.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default2.setPadding(0, UiKt.dp(exportActivity, 4), 0, UiKt.dp(exportActivity, 14));
        linearLayout9.addView(label$default2);
        LinearLayout linearLayout10 = this.root;
        if (linearLayout10 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout10 = null;
        }
        linearLayout10.addView(UiKt.label$default(exportActivity, "Share-ready presets", 13.0f, C.INSTANCE.getDim(), true, 0, 16, null));
        LinearLayout linearLayout11 = this.root;
        if (linearLayout11 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout11 = null;
        }
        List<Preset> all = Presets.INSTANCE.getAll();
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(all, 10));
        for (final Preset preset : all) {
            arrayList.add(UiKt.chip(exportActivity, preset.getLabel(), Intrinsics.areEqual(this.preset.getId(), preset.getId()), new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    ExportActivity.this.selectPreset(preset);
                    ExportActivity.this.showOptions();
                }
            }));
        }
        linearLayout11.addView(flow(arrayList));
        LinearLayout linearLayout12 = this.root;
        if (linearLayout12 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout12 = null;
        }
        TextView label$default3 = UiKt.label$default(exportActivity, this.preset.getNote(), 12.0f, C.INSTANCE.getDim(), false, 0, 24, null);
        label$default3.setPadding(0, UiKt.dp(exportActivity, 4), 0, UiKt.dp(exportActivity, 10));
        linearLayout12.addView(label$default3);
        int min = Math.min(Exporter.INSTANCE.maxEncoderHeight(), this.lite ? 720 : 2160);
        LinearLayout linearLayout13 = this.root;
        if (linearLayout13 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout = null;
        } else {
            linearLayout = linearLayout13;
        }
        int i = 720;
        linearLayout.addView(UiKt.label$default(exportActivity, "Quality", 13.0f, C.INSTANCE.getDim(), true, 0, 16, null));
        String str = "720p";
        List listOf = CollectionsKt.listOf(new Pair[]{TuplesKt.to(720, "720p"), TuplesKt.to(1080, "1080p"), TuplesKt.to(1440, "2K"), TuplesKt.to(2160, "4K")});
        LinearLayout linearLayout14 = this.root;
        if (linearLayout14 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout14 = null;
        }
        ArrayList arrayList2 = new ArrayList();
        for (Object obj : listOf) {
            int i2 = i;
            if (((Number) ((Pair) obj).getFirst()).intValue() <= Math.max(min, i2)) {
                arrayList2.add(obj);
            }
            i = i2;
        }
        ArrayList<Pair> arrayList3 = arrayList2;
        ArrayList arrayList4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(arrayList3, 10));
        for (Pair pair : arrayList3) {
            final int intValue = ((Number) pair.component1()).intValue();
            arrayList4.add(UiKt.chip(exportActivity, (String) pair.component2(), Math.min(this.heightCap, min) == intValue, new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    ExportActivity.this.heightCap = intValue;
                    ExportActivity.this.showOptions();
                }
            }));
        }
        linearLayout14.addView(flow(arrayList4));
        if (min < 2160 && !this.lite) {
            LinearLayout linearLayout15 = this.root;
            if (linearLayout15 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("root");
                linearLayout15 = null;
            }
            if (min >= 1440) {
                str = "2K";
            } else if (min >= 1080) {
                str = "1080p";
            }
            TextView label$default4 = UiKt.label$default(exportActivity, "This phone can export up to " + str + ".", 12.0f, C.INSTANCE.getDim(), false, 0, 24, null);
            label$default4.setPadding(0, UiKt.dp(exportActivity, 4), 0, 0);
            linearLayout15.addView(label$default4);
        }
        LinearLayout linearLayout16 = this.root;
        if (linearLayout16 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout16 = null;
        }
        TextView label$default5 = UiKt.label$default(exportActivity, "Shape", 13.0f, C.INSTANCE.getDim(), true, 0, 16, null);
        label$default5.setPadding(0, UiKt.dp(exportActivity, 12), 0, 0);
        linearLayout16.addView(label$default5);
        List listOf2 = CollectionsKt.listOf(new Pair[]{TuplesKt.to("orig", "Original"), TuplesKt.to("9:16", "9:16"), TuplesKt.to("1:1", "Square"), TuplesKt.to("16:9", "16:9")});
        LinearLayout linearLayout17 = this.root;
        if (linearLayout17 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout17 = null;
        }
        List<Pair> list = listOf2;
        ArrayList arrayList5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        for (Pair pair2 : list) {
            final String str2 = (String) pair2.component1();
            arrayList5.add(UiKt.chip(exportActivity, (String) pair2.component2(), Intrinsics.areEqual(this.aspect, str2), new Function0<Unit>() {

                @Override
                public Unit invoke() {
                    invoke2();
                    return Unit.INSTANCE;
                }

                public final void invoke2() {
                    ExportActivity.this.aspect = str2;
                    ExportActivity.this.showOptions();
                }
            }));
        }
        linearLayout17.addView(flow(arrayList5));
        if (!Intrinsics.areEqual(this.aspect, "orig")) {
            Switch r1 = new Switch(exportActivity);
            r1.setText("Fill the frame (crops edges)");
            r1.setTextColor(C.INSTANCE.getText());
            r1.setChecked(this.fill);
            r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    ExportActivity.showOptions$lambda$14$lambda$13(ExportActivity.this, compoundButton, z);
                }
            });
            LinearLayout linearLayout18 = this.root;
            if (linearLayout18 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("root");
                linearLayout18 = null;
            }
            LinearLayout.LayoutParams lp$default = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
            lp$default.topMargin = UiKt.dp(exportActivity, 6);
            Unit unit = Unit.INSTANCE;
            linearLayout18.addView(r1, lp$default);
        }
        Switch r12 = new Switch(exportActivity);
        r12.setText("Lite Mode (720p, smooth on 4 GB phones)");
        r12.setTextColor(C.INSTANCE.getText());
        r12.setChecked(this.lite);
        r12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                ExportActivity.showOptions$lambda$17$lambda$16(ExportActivity.this, compoundButton, z);
            }
        });
        LinearLayout linearLayout19 = this.root;
        if (linearLayout19 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout2 = null;
        } else {
            linearLayout2 = linearLayout19;
        }
        LinearLayout.LayoutParams lp$default2 = UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null);
        lp$default2.topMargin = UiKt.dp(exportActivity, 10);
        Unit unit2 = Unit.INSTANCE;
        linearLayout2.addView(r12, lp$default2);
        ExportOptions options = options();
        Exporter exporter = Exporter.INSTANCE;
        Project project3 = this.project;
        if (project3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project3 = null;
        }
        Pair<Integer, Integer> outputSize = exporter.outputSize(exportActivity, project3, options);
        int intValue2 = outputSize.component1().intValue();
        int intValue3 = outputSize.component2().intValue();
        Exporter exporter2 = Exporter.INSTANCE;
        Project project4 = this.project;
        if (project4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("project");
            project4 = null;
        }
        long estimateBytes = exporter2.estimateBytes(project4, options);
        LinearLayout linearLayout20 = this.root;
        if (linearLayout20 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout3 = null;
        } else {
            linearLayout3 = linearLayout20;
        }
        TextView label$default6 = UiKt.label$default(exportActivity, intValue2 + "×" + intValue3 + "  ·  about " + UiKt.fmtSize(estimateBytes), 15.0f, C.INSTANCE.getText(), true, 0, 16, null);
        label$default6.setPadding(0, UiKt.dp(exportActivity, 16), 0, UiKt.dp(exportActivity, 4));
        linearLayout3.addView(label$default6);
        LinearLayout linearLayout21 = this.root;
        if (linearLayout21 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout4 = null;
        } else {
            linearLayout4 = linearLayout21;
        }
        linearLayout4.addView(UiKt.label$default(exportActivity, "Exports on your phone, free. Includes a small \"Made with Mukhda\" mark in the corner.", 12.0f, C.INSTANCE.getDim(), false, 0, 24, null));
        if (Intrinsics.areEqual(this.preset.getId(), "shorts")) {
            Project project5 = this.project;
            if (project5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("project");
                project5 = null;
            }
            if (project5.getTotalMs() > 180000) {
                LinearLayout linearLayout22 = this.root;
                if (linearLayout22 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("root");
                    linearLayout6 = null;
                } else {
                    linearLayout6 = linearLayout22;
                }
                linearLayout6.addView(UiKt.label$default(exportActivity, "Shorts must be under 3 minutes.", 12.0f, C.INSTANCE.getPink(), false, 0, 24, null));
            }
        }
        LinearLayout linearLayout23 = this.root;
        if (linearLayout23 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout23 = null;
        }
        linearLayout23.addView(UiKt.spacer(exportActivity, 18));
        LinearLayout linearLayout24 = this.root;
        if (linearLayout24 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("root");
            linearLayout5 = null;
        } else {
            linearLayout5 = linearLayout24;
        }
        linearLayout5.addView(UiKt.pillButton$default(exportActivity, "Export  ·  ~" + UiKt.fmtSize(estimateBytes), 0, 0, 0, new Function0<Unit>() {

            @Override
            public Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            public final void invoke2() {
                ExportActivity.this.startExport();
            }
        }, 14, null), UiKt.lp$default(UiKt.getMATCH(), UiKt.getWRAP(), 0.0f, 4, null));
    }

    public static final void showOptions$lambda$14$lambda$13(ExportActivity this$0, CompoundButton compoundButton, boolean z) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.fill = z;
    }

    public static final void showOptions$lambda$17$lambda$16(ExportActivity this$0, CompoundButton compoundButton, boolean z) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.lite = z;
        Store.INSTANCE.setLite(this$0, z);
        this$0.showOptions();
    }

    public static final void showOptions$lambda$4$lambda$3(ExportActivity this$0, View view) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        this$0.finish();
    }

    public final void startExport() {
        if (Build.VERSION.SDK_INT >= 29 || ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            runExport(options());
        } else {
            this.pendingSave = true;
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 7);
        }
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        String stringExtra = getIntent().getStringExtra("id");
        Project load = stringExtra != null ? Store.INSTANCE.load(this, stringExtra) : null;
        if (load == null) {
            finish();
            return;
        }
        this.project = load;
        this.lite = Store.INSTANCE.lite(this);
        selectPreset(Presets.INSTANCE.getAll().get(0));
        showOptions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        if (code == 7 && this.pendingSave) {
            this.pendingSave = false;
            if ((!(res.length == 0)) && res[0] == 0) {
                runExport(options());
            } else {
                Toast.makeText(this, "Mukhda needs storage access to save your video to the gallery", 1).show();
            }
        }
    }
}
