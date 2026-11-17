package com.mukhda.editor;

import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

final class StatusActivity$onCreate$2 extends Lambda<Unit> implements Function0<Unit> {
    final StatusActivity this$0;

    public StatusActivity$onCreate$2(StatusActivity statusActivity) {
        super(0);
        this.this$0 = statusActivity;
    }

    public static final void invoke$lambda$0(StatusActivity this$0, double[] loud) {
        long j;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(loud, "$loud");
        j = this$0.duration;
        if (j > 0) {
            this$0.showParts(!(loud.length == 0));
        } else {
            Toast.makeText(this$0, "This video could not be opened", 1).show();
            this$0.finish();
        }
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Uri uri;
        double[] dArrTmp;
        long j;
        List emptyList;
        Handler handler;
        long j2;
        Uri uri2;
        Uri uri3;
        short[] decodeAll;
        StatusActivity statusActivity = this.this$0;
        Media media = Media.INSTANCE;
        StatusActivity statusActivity2 = this.this$0;
        StatusActivity statusActivity3 = statusActivity2;
        uri = statusActivity2.uri;
        if (uri == null) {
            Intrinsics.throwUninitializedPropertyAccessException(MusicActivity.EXTRA_URI);
            uri = null;
        }
        statusActivity.duration = media.videoDuration(statusActivity3, uri);
        try {
            AudioDecoder audioDecoder = AudioDecoder.INSTANCE;
            StatusActivity statusActivity4 = this.this$0;
            StatusActivity statusActivity5 = statusActivity4;
            uri2 = statusActivity4.uri;
            if (uri2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException(MusicActivity.EXTRA_URI);
                uri3 = null;
            } else {
                uri3 = uri2;
            }
            decodeAll = audioDecoder.decodeAll(statusActivity5, uri3, 0L, 0L);
            dArrTmp = decodeAll == null ? new double[0] : StatusSplitter.INSTANCE.loudness(decodeAll);
        } catch (Throwable unused) {
            dArrTmp = new double[0];
        }
        final double[] dArr = dArrTmp;
        StatusActivity statusActivity6 = this.this$0;
        j = statusActivity6.duration;
        if (j > 0) {
            StatusSplitter statusSplitter = StatusSplitter.INSTANCE;
            j2 = this.this$0.duration;
            emptyList = statusSplitter.cuts(j2, dArr, 30000L, 6000L, 3000L);
        } else {
            emptyList = CollectionsKt.emptyList();
        }
        statusActivity6.cuts = emptyList;
        handler = this.this$0.handler;
        final StatusActivity statusActivity7 = this.this$0;
        handler.post(new Runnable() {
            @Override
            public final void run() {
                StatusActivity$onCreate$2.invoke$lambda$0(this$0, dArr);
            }
        });
    }
}
