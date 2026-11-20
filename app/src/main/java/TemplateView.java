package com.mukhda.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.util.List;

public final class TemplateView extends View {
    private TemplateSpec spec;
    private List<Bitmap> photos;
    private boolean playing;
    private long startNs;
    private long baseMs;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            invalidate();
            if (playing) handler.postDelayed(this, 33);
        }
    };

    public TemplateView(Context ctx) {
        super(ctx);
    }

    public boolean showText = true;

    public void setTemplate(TemplateSpec spec, List<Bitmap> photos) {
        this.spec = spec;
        this.photos = photos;
        invalidate();
    }

    public void play() {
        if (playing) return;
        playing = true;
        startNs = System.nanoTime();
        handler.post(tick);
    }

    public void pause() {
        if (!playing) return;
        playing = false;
        baseMs = currentT();
        handler.removeCallbacks(tick);
    }

    public void stop() {
        playing = false;
        baseMs = 0;
        handler.removeCallbacks(tick);
        invalidate();
    }

    public boolean isPlaying() { return playing; }

    private long currentT() {
        long dur = spec == null ? 1 : Math.max(1, spec.durationMs());
        long t = baseMs + (playing ? (System.nanoTime() - startNs) / 1000000 : 0);
        return t % dur;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (spec == null) return;
        TemplateEngine.drawFrame(canvas, getWidth(), getHeight(), spec, photos, currentT(), !showText);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        playing = false;
        handler.removeCallbacks(tick);
    }
}
