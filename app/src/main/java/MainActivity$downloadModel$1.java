package com.mukhda.editor;

import android.app.ProgressDialog;
import android.os.Handler;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.mukhda.editor.MainActivity$downloadModel$1;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

public final class MainActivity$downloadModel$1 extends Lambda<Unit> implements Function0<Unit> {
    final ProgressDialog $d;
    final Lang $l;
    final MainActivity this$0;

    public static final class AnonymousClass1 extends Lambda<Unit> implements Function1<Float, Unit> {
        final ProgressDialog $d;
        final MainActivity this$0;

        AnonymousClass1(MainActivity mainActivity, ProgressDialog progressDialog) {
            super(1);
            this.this$0 = mainActivity;
            this.$d = progressDialog;
        }

        public static final void invoke$lambda$0(ProgressDialog d, float f) {
            Intrinsics.checkNotNullParameter(d, "$d");
            d.setProgress((int) (f * 100));
        }

        @Override
        public Unit invoke(Float f) {
            invoke(f.floatValue());
            return Unit.INSTANCE;
        }

        public final void invoke(final float f) {
            Handler handler;
            handler = this.this$0.main;
            final ProgressDialog progressDialog = this.$d;
            handler.post(new Runnable() {
                @Override
                public final void run() {
                    MainActivity$downloadModel$1.AnonymousClass1.invoke$lambda$0(progressDialog, f);
                }
            });
        }
    }

    public MainActivity$downloadModel$1(MainActivity mainActivity, Lang lang, ProgressDialog progressDialog) {
        super(0);
        this.this$0 = mainActivity;
        this.$l = lang;
        this.$d = progressDialog;
    }

    public static final void invoke$lambda$0(ProgressDialog d, MainActivity this$0) {
        Intrinsics.checkNotNullParameter(d, "$d");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        d.dismiss();
        this$0.show(4);
    }

    public static final void invoke$lambda$1(ProgressDialog d, MainActivity this$0, Throwable e) {
        Intrinsics.checkNotNullParameter(d, "$d");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(e, "$e");
        d.dismiss();
        Toast.makeText(this$0, "Download failed: " + e.getMessage(), 1).show();
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Handler handler;
        Handler handler2;
        try {
            ModelStore.INSTANCE.download(this.this$0, this.$l.getModel(), new AnonymousClass1(this.this$0, this.$d), new Function0<Boolean>() {
                @Override
                public final Boolean invoke() {
                    return false;
                }
            });
            handler2 = this.this$0.main;
            final ProgressDialog progressDialog = this.$d;
            final MainActivity mainActivity = this.this$0;
            handler2.post(new Runnable() {
                @Override
                public final void run() {
                    MainActivity$downloadModel$1.invoke$lambda$0(progressDialog, mainActivity);
                }
            });
        } catch (Throwable th) {
            handler = this.this$0.main;
            final ProgressDialog progressDialog2 = this.$d;
            final MainActivity mainActivity2 = this.this$0;
            handler.post(new Runnable() {
                @Override
                public final void run() {
                    MainActivity$downloadModel$1.invoke$lambda$1(progressDialog2, mainActivity2, th);
                }
            });
        }
    }
}
