package com.mukhda.editor;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.File;
import kotlin.Metadata;
import kotlin.io.FilesKt;
import kotlin.text.Charsets;
import kotlin.jvm.internal.Intrinsics;

public final class CrashLog {
    public static final CrashLog INSTANCE = new CrashLog();

    private CrashLog() {
    }

    private final File file(Context c) {
        return new File(c.getFilesDir(), "last_crash.txt");
    }

    public final void clear(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        try {
            file(c).delete();
        } catch (Throwable unused) {
        }
    }

    public final String read(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        try {
            File file = file(c);
            if (!file.exists()) {
                file = null;
            }
            if (file != null) {
                return FilesKt.readText(file, Charsets.UTF_8);
            }
            return null;
        } catch (Throwable unused) {
            return null;
        }
    }

    public final void write(Context c, Throwable e) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(e, "e");
        FilesKt.writeText(file(c), "Mukhda " + versionName(c) + " | Android " + Build.VERSION.RELEASE + " | " + Build.MANUFACTURER + " " + Build.MODEL + "\n" + Log.getStackTraceString(e), Charsets.UTF_8);
    }
    private static String versionName(android.content.Context c) {
        try {
            return String.valueOf(c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName);
        } catch (Exception e) {
            return "?";
        }
    }
}
