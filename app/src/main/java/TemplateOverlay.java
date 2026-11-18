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
    private long lastT = -1;

    public TemplateOverlay(TemplateSpec spec, List<Bitmap> photos) {
        this.spec = spec;
        this.photos = photos;
    }

    @Override
    public void configure(Size videoSize) {
        this.w = videoSize.getWidth();
        this.h = videoSize.getHeight();
        System.out.println("EXPORT-TEST overlay configure: " + w + "x" + h);
        if (frame == null || frame.getWidth() != w || frame.getHeight() != h) {
            if (frame != null) frame.recycle();
            frame = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            frameCanvas = new Canvas(frame);
            lastT = -1;
        }
    }

    @Override
    public Bitmap getBitmap(long presentationTimeUs) {
        long t = presentationTimeUs / 1000;
        if (frameCanvas != null && t != lastT) {
            frame.eraseColor(0);
            try {
                TemplateEngine.drawFrame(frameCanvas, w, h, spec, photos, t);
            } catch (Throwable e) {
                System.out.println("EXPORT-TEST overlay draw error: " + e);
            }
            lastT = t;
            if (t < 3000) {
                int cx = frame.getPixel(w / 2, h / 2);
                System.out.println("EXPORT-TEST overlay frame t=" + t + " centerPixel=" + Integer.toHexString(cx) + " photos=" + photos.size());
            }
        }
        return frame;
    }

    @Override
    public OverlaySettings getOverlaySettings(long presentationTimeUs) {
        return new OverlaySettings.Builder().setScale(1f, 1f).build();
    }

    @Override
    public void release() {
        if (frame != null) {
            frame.recycle();
            frame = null;
        }
    }
}
