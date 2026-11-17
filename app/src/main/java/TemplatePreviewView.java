package com.mukhda.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

public final class TemplatePreviewView extends View {
    public final Handler h;
    public final Paint paint;
    public final RectF rect;
    public final int[] slides;
    public final long start;
    public final Template t;
    public final Runnable tick;
    public final Paint txt;

    public TemplatePreviewView(Context ctx, Template t) {
        super(ctx);
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(t, "t");
        this.t = t;
        this.h = new Handler(Looper.getMainLooper());
        this.start = System.currentTimeMillis();
        this.paint = new Paint(1);
        Paint paint = new Paint(1);
        paint.setColor(-1);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setShadowLayer(6.0f, 0.0f, 2.0f, Color.argb(160, 0, 0, 0));
        this.txt = paint;
        this.tick = new Runnable() {
            @Override
            public void run() {
                Handler handler;
                TemplatePreviewView.this.invalidate();
                handler = TemplatePreviewView.this.h;
                handler.postDelayed(this, 45L);
            }
        };
        this.slides = new int[]{t.getC1(), t.getC2(), mix(t.getC1(), t.getC2(), 0.5f), mix(t.getC2(), ViewCompat.MEASURED_STATE_MASK, 0.35f)};
        this.rect = new RectF();
    }

    public final int mix(int a, int b, float f) {
        float f2 = 1 - f;
        return Color.rgb((int) ((Color.red(a) * f2) + (Color.red(b) * f)), (int) ((Color.green(a) * f2) + (Color.green(b) * f)), (int) ((Color.blue(a) * f2) + (Color.blue(b) * f)));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.h.postDelayed(this.tick, 45L);
    }

    @Override
    protected void onDetachedFromWindow() {
        this.h.removeCallbacks(this.tick);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas c) {
        float f;
        Intrinsics.checkNotNullParameter(c, "c");
        float width = getWidth();
        float height = getHeight();
        if (width <= 0.0f) {
            return;
        }
        long coerceAtLeast = RangesKt.coerceAtLeast((long) ((60000.0d / this.t.getBpm()) * this.t.getBeatsPerCut()), 250L);
        long currentTimeMillis = System.currentTimeMillis() - this.start;
        int length = (int) ((currentTimeMillis / coerceAtLeast) % this.slides.length);
        float f2 = ((float) (currentTimeMillis % coerceAtLeast)) / ((float) coerceAtLeast);
        String fx = this.t.getFx();
        if (Intrinsics.areEqual(fx, "punch")) {
            if (length % 2 != 0) {
                f = 1.2f;
            }
            f = 1.0f;
        } else {
            if (Intrinsics.areEqual(fx, "pulse")) {
                f = 1.1f;
            }
            f = 1.0f;
        }
        float f3 = f * ((0.08f * f2) + 1.0f);
        c.save();
        c.clipRect(0.0f, 0.0f, width, height);
        float f4 = 2;
        float f5 = width / f4;
        c.scale(f3, f3, f5, height / f4);
        Paint paint = this.paint;
        int[] iArr = this.slides;
        paint.setShader(new LinearGradient(0.0f, 0.0f, width, height, iArr[length], iArr[(length + 1) % iArr.length], Shader.TileMode.CLAMP));
        c.drawRect(0.0f, 0.0f, width, height, this.paint);
        this.paint.setShader(null);
        this.paint.setColor(Color.argb(70, 255, 255, 255));
        c.drawCircle(width * ((length * 0.1f) + 0.3f), height * 0.42f, 0.16f * width, this.paint);
        this.rect.set(width * 0.2f, height * 0.58f, 0.8f * width, 0.9f * height);
        c.drawRoundRect(this.rect, 24.0f, 24.0f, this.paint);
        c.restore();
        if (f2 < 0.12f && Intrinsics.areEqual(this.t.getFx(), "punch")) {
            this.paint.setColor(Color.argb((int) (((0.12f - f2) / 0.12f) * 120), 255, 255, 255));
            c.drawRect(0.0f, 0.0f, width, height, this.paint);
        }
        this.txt.setTextSize(width * 0.085f);
        c.drawText(StringsKt.take(this.t.getTitleText(), 18), f5, height * 0.2f, this.txt);
    }
}
