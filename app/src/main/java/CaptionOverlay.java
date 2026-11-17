package com.mukhda.editor;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.effect.BitmapOverlay;
import androidx.media3.effect.OverlaySettings;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

public final class CaptionOverlay extends BitmapOverlay {
    private final List<TimedCaption> caps;
    private final Context ctx;
    private final Bitmap empty;
    private final int h;
    private Bitmap lastBmp;
    private int lastIdx;
    private final String style;
    private final int w;

    public CaptionOverlay(Context ctx, List<TimedCaption> caps, String style, int i, int i2) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(caps, "caps");
        Intrinsics.checkNotNullParameter(style, "style");
        this.ctx = ctx;
        this.caps = caps;
        this.style = style;
        this.w = i;
        this.h = i2;
        Bitmap createBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Intrinsics.checkNotNullExpressionValue(createBitmap, "createBitmap(...)");
        this.empty = createBitmap;
        this.lastIdx = -2;
        this.lastBmp = createBitmap;
    }

    private final int idxAt(long ms) {
        int i = 0;
        for (TimedCaption timedCaption : this.caps) {
            if (ms >= timedCaption.getStartMs() && ms < timedCaption.getEndMs()) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public Bitmap getBitmap(long presentationTimeUs) {
        int idxAt = idxAt(presentationTimeUs / 1000);
        if (idxAt != this.lastIdx) {
            this.lastIdx = idxAt;
            this.lastBmp = idxAt < 0 ? this.empty : CaptionRenderer.INSTANCE.render(this.ctx, this.caps.get(idxAt).getText(), this.style, this.w, this.h);
        }
        return this.lastBmp;
    }

    @Override
    public OverlaySettings getOverlaySettings(long presentationTimeUs) {
        OverlaySettings build = new OverlaySettings.Builder().build();
        Intrinsics.checkNotNullExpressionValue(build, "build(...)");
        return build;
    }
}
