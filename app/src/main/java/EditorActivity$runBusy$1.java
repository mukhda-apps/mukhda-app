package com.mukhda.editor;

import android.app.ProgressDialog;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

public final class EditorActivity$runBusy$1 extends Lambda<Unit> implements Function0<Unit> {
    final ProgressDialog $d;
    final Function0<Unit> $work;
    final EditorActivity this$0;

    public EditorActivity$runBusy$1(Function0<Unit> function0, EditorActivity editorActivity, ProgressDialog progressDialog) {
        super(0);
        this.$work = function0;
        this.this$0 = editorActivity;
        this.$d = progressDialog;
    }

    public static final void invoke$lambda$0(EditorActivity a, ProgressDialog d) {
        Intrinsics.checkNotNullParameter(d, "$d");
        UiKt.safeDismiss(a, d);
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        Handler handler;
        try {
            this.$work.invoke();
        } finally {
            handler = this.this$0.main;
            final ProgressDialog progressDialog = this.$d;
            handler.post(new Runnable() {
                @Override
                public final void run() {
                    EditorActivity$runBusy$1.invoke$lambda$0(this$0, progressDialog);
                }
            });
        }
    }
}
