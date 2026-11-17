package com.mukhda.editor;

import android.net.Uri;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.File;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

public final class ExportActivity$finishExport$1 extends Lambda<Unit> implements Function0<Unit> {
    final File $f;
    final ExportActivity this$0;

    public ExportActivity$finishExport$1(ExportActivity exportActivity, File file) {
        super(0);
        this.this$0 = exportActivity;
        this.$f = file;
    }

    public static final void invoke$lambda$0(ExportActivity this$0, Uri uri, File f) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(f, "$f");
        this$0.savedUri = uri;
        this$0.showDone(f, uri);
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Uri uriTmp;
        Handler handler;
        try {
            uriTmp = Exporter.INSTANCE.saveToGallery(this.this$0, this.$f, "Mukhda_" + System.currentTimeMillis() + ".mp4");
        } catch (Throwable unused) {
            uriTmp = null;
        }
        final Uri uri = uriTmp;
        handler = this.this$0.handler;
        final ExportActivity exportActivity = this.this$0;
        final File file = this.$f;
        handler.post(new Runnable() {
            @Override
            public final void run() {
                ExportActivity$finishExport$1.invoke$lambda$0(ExportActivity$finishExport$1.this.this$0, uri, file);
            }
        });
    }
}
