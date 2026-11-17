package com.mukhda.editor;

import android.net.Uri;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlin.ranges.LongRange;

final class StatusActivity$exportPart$t$1$onCompleted$1 extends Lambda<Unit> implements Function0<Unit> {
    final ProgressBar $bar;
    final int $i;
    final File $out;
    final List<LongRange> $parts;
    final TextView $title;
    final StatusActivity this$0;

    public StatusActivity$exportPart$t$1$onCompleted$1(int i, StatusActivity statusActivity, File file, List<LongRange> list, TextView textView, ProgressBar progressBar) {
        super(0);
        this.$i = i;
        this.this$0 = statusActivity;
        this.$out = file;
        this.$parts = list;
        this.$title = textView;
        this.$bar = progressBar;
    }

    public static final void invoke$lambda$0(Uri uri, StatusActivity this$0, List parts, int i, TextView title, ProgressBar bar) {
        ArrayList arrayList;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(parts, "$parts");
        Intrinsics.checkNotNullParameter(title, "$title");
        Intrinsics.checkNotNullParameter(bar, "$bar");
        if (uri != null) {
            arrayList = this$0.outputs;
            arrayList.add(uri);
        }
        StatusActivity.exportPart$default(this$0, parts, i + 1, title, bar, false, 16, null);
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Uri uri;
        Handler handler;
        try {
            uri = Exporter.INSTANCE.saveToGallery(this.this$0, this.$out, "Mukhda_status_" + System.currentTimeMillis() + "_part" + (this.$i + 1) + ".mp4");
        } catch (Throwable unused) {
            uri = null;
        }
        final Uri uri2 = uri;
        this.$out.delete();
        handler = this.this$0.handler;
        final StatusActivity statusActivity = this.this$0;
        final List<LongRange> list = this.$parts;
        final int i = this.$i;
        final TextView textView = this.$title;
        final ProgressBar progressBar = this.$bar;
        handler.post(new Runnable() {
            @Override
            public final void run() {
                StatusActivity$exportPart$t$1$onCompleted$1.invoke$lambda$0(uri2, statusActivity, list, i, textView, progressBar);
            }
        });
    }
}
