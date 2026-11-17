package com.mukhda.editor;

import android.app.Application;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.lang.Thread;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

public final class App extends Application {
    public static final void onCreate$lambda$0(App this$0, Thread.UncaughtExceptionHandler uncaughtExceptionHandler, Thread thread, Throwable th) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        try {
            Intrinsics.checkNotNull(th);
            CrashLog.INSTANCE.write(this$0, th);
        } catch (Throwable unused) {
        }
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public final void uncaughtException(Thread thread, Throwable th) {
                App.onCreate$lambda$0(App.this, defaultUncaughtExceptionHandler, thread, th);
            }
        });
    }
}
