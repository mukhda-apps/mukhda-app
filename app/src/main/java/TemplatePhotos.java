package com.mukhda.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class TemplatePhotos {
    private TemplatePhotos() {}

    public static List<Bitmap> load(Context ctx, List<Clip> clips, int targetW, int targetH) {
        List<Bitmap> out = new ArrayList<Bitmap>();
        if (clips == null) return out;
        // The template build step duplicates clips to fill the beat timeline; decoding one
        // bitmap per clip would hold the same photo many times (24+ full bitmaps => OOM).
        java.util.HashSet<String> seen = new java.util.HashSet<String>();
        for (Clip c : clips) {
            if (c == null || c.getUri() == null || !seen.add(c.getUri())) continue;
            Bitmap b = decode(ctx, c.getUri(), targetW, targetH);
            if (b != null) out.add(b);
        }
        return out;
    }

    private static Bitmap decode(Context ctx, String uriStr, int targetW, int targetH) {
        try {
            Uri uri = Uri.parse(uriStr);
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            InputStream in = ctx.getContentResolver().openInputStream(uri);
            if (in == null) return null;
            BitmapFactory.decodeStream(in, null, bounds);
            in.close();
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null;
            int sample = 1;
            while (bounds.outWidth / (sample * 2) >= targetW && bounds.outHeight / (sample * 2) >= targetH) {
                sample *= 2;
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = sample;
            InputStream in2 = ctx.getContentResolver().openInputStream(uri);
            if (in2 == null) return null;
            Bitmap b = BitmapFactory.decodeStream(in2, null, opts);
            in2.close();
            return b;
        } catch (Exception e) {
            return null;
        }
    }
}
