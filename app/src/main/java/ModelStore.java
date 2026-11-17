package com.mukhda.editor;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.google.common.net.HttpHeaders;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.text.Charsets;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

public final class ModelStore {
    public static final ModelStore INSTANCE = new ModelStore();

    private ModelStore() {
    }

    public final void delete(Context ctx, String model) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(model, "model");
        FilesKt.deleteRecursively(dir(ctx, model));
    }

    public final File dir(Context ctx, String model) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(model, "model");
        return new File(ctx.getFilesDir(), "models/" + model);
    }

    public final void download(Context ctx, String model, Function1<? super Float, Unit> onProgress, Function0<Boolean> isCancelled) {
        try {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(model, "model");
        Intrinsics.checkNotNullParameter(onProgress, "onProgress");
        Intrinsics.checkNotNullParameter(isCancelled, "isCancelled");
        if (isReady(ctx, model)) {
            return;
        }
        new File(ctx.getFilesDir(), "models").mkdirs();
        File file = new File(ctx.getCacheDir(), model + ".zip");
        URLConnection openConnection = new URL("https://alphacephei.com/vosk/models/" + model + ".zip").openConnection();
        Intrinsics.checkNotNull(openConnection, "null cannot be cast to non-null type java.net.HttpURLConnection");
        HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
        httpURLConnection.setConnectTimeout(AccessibilityNodeInfoCompat.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_MAX_LENGTH);
        httpURLConnection.setReadTimeout(30000);
        int i = 0;
        int i2 = 0;
        while (true) {
            int responseCode = httpURLConnection.getResponseCode();
            if (300 > responseCode || responseCode >= 400) {
                break;
            }
            int i3 = i2 + 1;
            if (i2 >= 5) {
                break;
            }
            String headerField = httpURLConnection.getHeaderField(HttpHeaders.LOCATION);
            httpURLConnection.disconnect();
            URLConnection openConnection2 = new URL(headerField).openConnection();
            Intrinsics.checkNotNull(openConnection2, "null cannot be cast to non-null type java.net.HttpURLConnection");
            httpURLConnection = (HttpURLConnection) openConnection2;
            httpURLConnection.setConnectTimeout(AccessibilityNodeInfoCompat.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_MAX_LENGTH);
            httpURLConnection.setReadTimeout(30000);
            i2 = i3;
        }
        if (httpURLConnection.getResponseCode() != 200) {
            throw new IOException("Download failed (" + httpURLConnection.getResponseCode() + ")");
        }
        long contentLengthLong = httpURLConnection.getContentLengthLong();
        java.io.InputStream dl = httpURLConnection.getInputStream();
        FileOutputStream fout = new FileOutputStream(file);
        try {
            byte[] buf = new byte[65536];
            long total = 0;
            while (!isCancelled.invoke().booleanValue()) {
                int read = dl.read(buf);
                if (read < 0) {
                    break;
                }
                fout.write(buf, 0, read);
                total += read;
                if (contentLengthLong > 0) {
                    onProgress.invoke(Float.valueOf(((total * 0.9f)) / contentLengthLong));
                }
            }
            if (isCancelled.invoke().booleanValue()) {
                file.delete();
                throw new IOException("Cancelled");
            }
        } finally {
            try { dl.close(); } catch (Exception ignored) {}
            try { fout.close(); } catch (Exception ignored) {}
        }
        File dir = dir(ctx, model);
        FilesKt.deleteRecursively(dir);
        ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(file), 8192));
        try {
            while (true) {
                ZipEntry e = zin.getNextEntry();
                if (e == null) {
                    break;
                }
                String name = StringsKt.removePrefix(e.getName(), (CharSequence) (model + "/"));
                if (name.length() == 0 || StringsKt.contains(name, "..", false)) {
                    continue;
                }
                File out2 = new File(dir, name);
                if (e.isDirectory()) {
                    out2.mkdirs();
                    continue;
                }
                File p = out2.getParentFile();
                if (p != null) {
                    p.mkdirs();
                }
                FileOutputStream fo = new FileOutputStream(out2);
                try {
                    ByteStreamsKt.copyTo(zin, fo, 8192);
                } finally {
                    try { fo.close(); } catch (Exception ignored) {}
                }
            }
        } finally {
            try { zin.close(); } catch (Exception ignored) {}
        }
        file.delete();
        if (!new File(dir, "am").exists()) {
            FilesKt.deleteRecursively(dir);
            throw new IOException("Model files incomplete");
        }
        FilesKt.writeText(new File(dir, "ready"), "ok", Charsets.UTF_8);
        onProgress.invoke(Float.valueOf(1.0f));

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public final boolean isReady(Context ctx, String model) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(model, "model");
        return new File(dir(ctx, model), "ready").exists();
    }
}
