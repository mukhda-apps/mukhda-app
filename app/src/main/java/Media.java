package com.mukhda.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

public final class Media {
    public static final Media INSTANCE = new Media();
    public static final long PHOTO_MS = 3000;

    private Media() {
    }

    public static Bitmap thumb$default(Media media, Context context, Clip clip, long j, int i, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            j = 0;
        }
        long j2 = j;
        if ((i2 & 8) != 0) {
            i = 320;
        }
        return media.thumb(context, clip, j2, i);
    }

    public final boolean isImage(Context ctx, Uri uri) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(uri, "uri");
        String type = ctx.getContentResolver().getType(uri);
        if (type == null) {
            type = "";
        }
        return StringsKt.startsWith(type, "image/", false);
    }

    public final void persist(Context ctx, Uri uri) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(uri, "uri");
        try {
            ctx.getContentResolver().takePersistableUriPermission(uri, 1);
        } catch (Exception unused) {
        }
    }

    public final Bitmap thumb(Context ctx, Clip clip, long atMs, int maxPx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(clip, "clip");
        try {
            int i = 1;
            if (!clip.isImage()) {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(ctx, Uri.parse(clip.getUri()));
                Bitmap frameAtTime = mediaMetadataRetriever.getFrameAtTime((clip.getStartMs() + atMs) * 1000, 2);
                mediaMetadataRetriever.release();
                if (frameAtTime == null) {
                    return null;
                }
                float max = maxPx / Math.max(frameAtTime.getWidth(), frameAtTime.getHeight());
                if (max < 1.0f) {
                    frameAtTime = Bitmap.createScaledBitmap(frameAtTime, RangesKt.coerceAtLeast((int) (frameAtTime.getWidth() * max), 1), RangesKt.coerceAtLeast((int) (frameAtTime.getHeight() * max), 1), true);
                }
                return frameAtTime;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream openInputStream = ctx.getContentResolver().openInputStream(Uri.parse(clip.getUri()));
            Intrinsics.checkNotNull(openInputStream);
            InputStream inputStream = openInputStream;
            try {
                BitmapFactory.decodeStream(inputStream, null, options);
                CloseableKt.closeFinally(inputStream, null);
                while (options.outWidth / i > maxPx * 2) {
                    i *= 2;
                }
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                options2.inSampleSize = i;
                InputStream openInputStream2 = ctx.getContentResolver().openInputStream(Uri.parse(clip.getUri()));
                Intrinsics.checkNotNull(openInputStream2);
                inputStream = openInputStream2;
                try {
                    Bitmap decodeStream = BitmapFactory.decodeStream(inputStream, null, options2);
                    CloseableKt.closeFinally(inputStream, null);
                    return decodeStream;
                } finally {
                }
            } finally {
            }
        } catch (Exception unused) {
            return null;
        }
    }

    public final Pair<List<Clip>, Integer> toClips(Context ctx, List<? extends Uri> uris) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(uris, "uris");
        ArrayList arrayList = new ArrayList();
        int i = 0;
        for (Uri uri : uris) {
            persist(ctx, uri);
            if (isImage(ctx, uri)) {
                String uri2 = uri.toString();
                Intrinsics.checkNotNullExpressionValue(uri2, "toString(...)");
                arrayList.add(new Clip(null, uri2, true, 3000L, 0L, 3000L, 0.0f, 0.0f, 193, null));
            } else {
                long videoDuration = videoDuration(ctx, uri);
                if (videoDuration <= 0) {
                    i++;
                } else {
                    String uri3 = uri.toString();
                    Intrinsics.checkNotNullExpressionValue(uri3, "toString(...)");
                    arrayList.add(new Clip(null, uri3, false, videoDuration, 0L, videoDuration, 0.0f, 0.0f, 193, null));
                }
            }
        }
        return new Pair<>(arrayList, Integer.valueOf(i));
    }

    public final long videoDuration(Context ctx, Uri uri) {
        Long longOrNull;
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(uri, "uri");
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(ctx, uri);
            String extractMetadata = mediaMetadataRetriever.extractMetadata(9);
            long longValue = (extractMetadata == null || (longOrNull = StringsKt.toLongOrNull(extractMetadata)) == null) ? 0L : longOrNull.longValue();
            mediaMetadataRetriever.release();
            return longValue;
        } catch (Exception unused) {
            return 0L;
        }
    }
}
