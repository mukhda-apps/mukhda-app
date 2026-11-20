package com.mukhda.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.media3.common.VideoFrameProcessingException;
import androidx.media3.common.util.Size;
import androidx.media3.effect.BitmapOverlay;
import androidx.media3.effect.OverlaySettings;
import java.util.List;

public final class TemplateOverlay extends BitmapOverlay {
    private final TemplateSpec spec;
    private final List<Bitmap> photos;
    private int w = -1;
    private int h = -1;
    private Bitmap frame;
    private Canvas frameCanvas;
    // Ring of bitmaps: returning the SAME mutated Bitmap every frame can make the GL
    // texture upload skip changes (blank/stale frames in 2 and 4 photo exports).
    private final Bitmap[] ring = new Bitmap[4];
    private int ringIdx = 0;
    private long lastT = -1;

    public TemplateOverlay(TemplateSpec spec, List<Bitmap> photos) {
        this.spec = spec;
        this.photos = photos;
    }

    @Override
    public void configure(Size videoSize) {
        this.w = videoSize.getWidth();
        this.h = videoSize.getHeight();
        if (frame == null || frame.getWidth() != w || frame.getHeight() != h) {
            for (int i = 0; i < ring.length; i++) { if (ring[i] != null) ring[i].recycle(); ring[i] = null; }
            frame = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            frameCanvas = new Canvas(frame);
            lastT = -1;
        }
    }

    @Override
    public Bitmap getBitmap(long presentationTimeUs) {
        long t = presentationTimeUs / 1000;
        if (frameCanvas != null && t != lastT) {
            ringIdx = (ringIdx + 1) % ring.length;
            if (ring[ringIdx] == null) ring[ringIdx] = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            frame = ring[ringIdx];
            frameCanvas = new Canvas(frame);
            frame.eraseColor(0);
            try {
                TemplateEngine.drawFrame(frameCanvas, w, h, spec, photos, t);
            } catch (Throwable e) {
                System.out.println("TemplateOverlay draw error: " + e);
            }
            lastT = t;
        }
        return frame;
    }

    @Override
    public OverlaySettings getOverlaySettings(long presentationTimeUs) {
        return new OverlaySettings.Builder().setScale(1f, 1f).build();
    }

    @Override
    public void release() {
        for (int i = 0; i < ring.length; i++) { if (ring[i] != null) ring[i].recycle(); ring[i] = null; }
        frame = null;
    }
}
