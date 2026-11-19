package com.mukhda.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import java.util.List;
import java.util.Random;

public final class TemplateEngine {
    public static String fxOverride;
    public static String filterOverride;
    private TemplateEngine() {}

    public static void drawFrame(Canvas canvas, int w, int h, TemplateSpec spec, List<Bitmap> photos, long tMs) {
        if (canvas == null || spec == null) return;
        long dur = spec.durationMs();
        if (tMs < 0) tMs = 0;
        if (tMs > dur) tMs = dur;

        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setShader(new LinearGradient(0, 0, 0, h, spec.c1, spec.c2, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, bg);
        bg.setShader(null);

        int n = photos == null ? 0 : photos.size();
        if (n > 0) {
            if ("full".equals(spec.layout)) {
                int slots = spec.photoCount;
                float slotMs = (float) dur / slots;
                float xf = Math.min(500f, slotMs * 0.3f);
                int idx = (int) Math.min(slots - 1, (long) (tMs / slotMs));
                float slotStart = idx * slotMs;
                float p = (tMs - slotStart) / slotMs;
                Bitmap cur = photos.get(idx % n);
                drawPhoto(canvas, cur, w, h, p, tMs - slotStart, idx, spec, 1f);
                if (p > 1f - xf / slotMs && idx + 1 < slots) {
                    float xa = (p - (1f - xf / slotMs)) / (xf / slotMs);
                    Bitmap nxt = photos.get((idx + 1) % n);
                    drawPhoto(canvas, nxt, w, h, 0f, 0f, idx + 1, spec, xa);
                }
            } else {
                drawCollage(canvas, w, h, spec, photos, tMs);
            }
        }

        String fxName = fxOverride != null ? fxOverride : spec.beatFx;
        long beatMs = Math.max(1, 60000L / spec.bpm);
        long nearest = Math.round(tMs / (double) beatMs) * beatMs;
        long dist = Math.abs(tMs - nearest);
        float beatP = dist < 220 ? 1f - dist / 220f : 0f;
        if ("zoompulse".equals(fxName)) {
            float z = 1f + 0.04f * (float) Math.abs(Math.sin(tMs / 600.0));
            canvas.scale(z, z, w / 2f, h / 2f);
        } else {
            applyBeatFx(canvas, w, h, spec, beatP, fxName);
        }
        if ("shine".equals(fxName)) {
            float ph = (tMs % 4000L) / 4000f;
            float bx = (w + h) * ph - h;
            Paint sp = new Paint();
            sp.setColor(Color.WHITE);
            sp.setAlpha(36);
            canvas.save();
            canvas.rotate(30, w / 2f, h / 2f);
            canvas.drawRect(bx, -h, bx + w * 0.15f, h * 3, sp);
            canvas.restore();
        }

        drawOverlay(canvas, w, h, spec, tMs);
        drawTexts(canvas, w, h, spec, tMs);
        drawVignette(canvas, w, h);
        drawWatermark(canvas, w, h);
    }

    private static void drawPhoto(Canvas canvas, Bitmap bmp, int w, int h, float p, float slotElapsedMs, int slot,
                                  TemplateSpec spec, float alpha) {
        if (bmp == null || bmp.isRecycled()) return;
        float enter = Math.min(1f, slotElapsedMs / 400f);
        float a = alpha * enter;
        if (a <= 0f) return;

        float margin = "rounded".equals(spec.frame) ? w * 0.06f : 0f;
        float fw = w - margin * 2, fh = h - margin * 2;
        float scaleBase = Math.max(fw / bmp.getWidth(), fh / bmp.getHeight());
        float kb = 1.04f + 0.14f * p;
        float dirX = (slot % 2 == 0) ? 1f : -1f;
        float dirY = (slot % 3 == 0) ? -1f : 1f;
        float drift = 0.03f * p;

        Matrix m = new Matrix();
        float s = scaleBase * kb;
        m.setScale(s, s);
        m.postTranslate(margin + (fw - bmp.getWidth() * s) / 2f + dirX * drift * fw,
                margin + (fh - bmp.getHeight() * s) / 2f + dirY * drift * fh);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setAlpha((int) (255 * a));
        ColorMatrix cm = filterMatrix(filterOverride != null ? filterOverride : spec.filter);
        if (cm != null) paint.setColorFilter(new ColorMatrixColorFilter(cm));

        boolean polaroid = "polaroid".equals(spec.mask);
        boolean shaped = "circle".equals(spec.mask) || "heart".equals(spec.mask);
        RectF photoRect;
        if (polaroid) {
            float card = Math.min(w, h) * 0.82f;
            float cx = w / 2f;
            float cy = h / 2f;
            Paint cardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            cardPaint.setColor(Color.WHITE);
            cardPaint.setShadowLayer(14f, 0f, 6f, 0x66000000);
            canvas.save();
            canvas.rotate((slot % 2 == 0 ? -3f : 3f), cx, cy);
            canvas.drawRoundRect(new RectF(cx - card / 2f, cy - card / 2f, cx + card / 2f, cy + card / 2f + card * 0.14f), 10f, 10f, cardPaint);
            photoRect = new RectF(cx - card / 2f + card * 0.05f, cy - card / 2f + card * 0.05f, cx + card / 2f - card * 0.05f, cy + card / 2f - card * 0.05f);
        } else if (shaped) {
            float d = Math.min(w, h) * 0.78f;
            photoRect = new RectF(w / 2f - d / 2f, h / 2f - d / 2f, w / 2f + d / 2f, h / 2f + d / 2f);
        } else {
            photoRect = new RectF(margin, margin, w - margin, h - margin);
        }
        float scale2 = Math.max(photoRect.width() / bmp.getWidth(), photoRect.height() / bmp.getHeight()) * (1.04f + 0.14f * p);
        Matrix m2 = new Matrix();
        m2.setScale(scale2, scale2);
        float offY2 = photoRect.height() - bmp.getHeight() * scale2;
        m2.postTranslate(photoRect.left + (photoRect.width() - bmp.getWidth() * scale2) / 2f + dirX * drift * photoRect.width(),
                photoRect.top + (offY2 >= 0 ? offY2 / 2f : offY2 * 0.15f) + dirY * drift * photoRect.height());
        canvas.save();
        canvas.clipPath(maskPath(spec.mask, photoRect));
        canvas.drawBitmap(bmp, m2, paint);
        canvas.restore();
        if (polaroid) {
            canvas.restore();
        }
        if (!polaroid && margin > 0f || shaped) {
            Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
            border.setStyle(Paint.Style.STROKE);
            border.setStrokeWidth(6f);
            border.setColor(Color.WHITE);
            border.setAlpha(220);
            canvas.drawPath(maskPath(spec.mask, photoRect), border);
        }
    }

    private static void drawCollage(Canvas canvas, int w, int h, TemplateSpec spec, List<Bitmap> photos, long tMs) {
        int cols;
        int rows;
        if ("split2".equals(spec.layout)) { cols = 1; rows = 2; }
        else if ("split3".equals(spec.layout)) { cols = 1; rows = 3; }
        else if ("grid6".equals(spec.layout)) { cols = 2; rows = 3; }
        else { cols = 2; rows = 2; }
        int cells = cols * rows;
        float gap = w * 0.025f;
        float margin = w * 0.045f;
        float cw = (w - margin * 2 - gap * (cols - 1)) / cols;
        float ch = (h - margin * 2 - gap * (rows - 1)) / rows;
        long cellMs = 2500;
        int base = (int) (tMs / cellMs);
        float cellP = (tMs % cellMs) / (float) cellMs;
        for (int i = 0; i < cells; i++) {
            int col = i % cols;
            int row = i / cols;
            float x = margin + col * (cw + gap);
            float y = margin + row * (ch + gap);
            Bitmap bmp = photos.get((base + i) % photos.size());
            float kb = 1.05f + 0.1f * cellP;
            float s = Math.max(cw / bmp.getWidth(), ch / bmp.getHeight()) * kb;
            float cellOffY = ch - bmp.getHeight() * s;
            Matrix m = new Matrix();
            m.setScale(s, s);
            // Face-safe: when the photo overflows vertically, bias the crop toward the top
            // (heads sit near the top of portrait photos) instead of center-cropping.
            m.postTranslate(x + (cw - bmp.getWidth() * s) / 2f, y + (cellOffY >= 0 ? cellOffY / 2f : cellOffY * 0.15f));
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            ColorMatrix cm = filterMatrix(filterOverride != null ? filterOverride : spec.filter);
            if (cm != null) paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.save();
            Path clip = new Path();
            clip.addRoundRect(new RectF(x, y, x + cw, y + ch), 28f, 28f, Path.Direction.CW);
            canvas.clipPath(clip);
            canvas.drawBitmap(bmp, m, paint);
            canvas.restore();
            Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);
            border.setStyle(Paint.Style.STROKE);
            border.setStrokeWidth(5f);
            border.setColor(Color.WHITE);
            border.setAlpha(230);
            canvas.drawRoundRect(new RectF(x, y, x + cw, y + ch), 28f, 28f, border);
        }
    }

    private static Path maskPath(String mask, RectF r) {
        Path path = new Path();
        if ("circle".equals(mask)) {
            float d = Math.min(r.width(), r.height());
            path.addCircle(r.centerX(), r.centerY(), d / 2f, Path.Direction.CW);
        } else if ("heart".equals(mask)) {
            float x = r.centerX();
            float y = r.top + r.height() * 0.32f;
            float s2 = r.width() * 0.5f;
            path.moveTo(x, y + s2 * 0.3f);
            path.cubicTo(x, y, x - s2, y, x - s2, y + s2 * 0.3f);
            path.cubicTo(x - s2, y + s2 * 0.7f, x, y + s2, x, y + s2 * 1.3f);
            path.cubicTo(x, y + s2, x + s2, y + s2 * 0.7f, x + s2, y + s2 * 0.3f);
            path.cubicTo(x + s2, y, x, y, x, y + s2 * 0.3f);
        } else {
            path.addRoundRect(r, 40f, 40f, Path.Direction.CW);
        }
        return path;
    }

    private static void applyBeatFx(Canvas canvas, int w, int h, TemplateSpec spec, float beatP, String fxName) {
        if (beatP <= 0f || "none".equals(fxName)) return;
        if ("flash".equals(fxName)) {
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setAlpha((int) (140 * beatP));
            canvas.drawRect(0, 0, w, h, p);
        } else if ("punch".equals(fxName)) {
            float z = 1f + 0.03f * beatP;
            canvas.scale(z, z, w / 2f, h / 2f);
        } else if ("shake".equals(fxName)) {
            float d = 10f * beatP;
            canvas.translate(((t2(w) % 2) == 0 ? d : -d), 0);
        } else if ("glitch".equals(fxName)) {
            Paint p = new Paint();
            p.setColor(0xFFFF0000);
            p.setAlpha((int) (40 * beatP));
            canvas.drawRect(0, h * 0.3f, w, h * 0.3f + 8, p);
            p.setColor(0xFF00FFFF);
            canvas.drawRect(0, h * 0.6f, w, h * 0.6f + 8, p);
        }
    }

    private static int t2(int w) { return w % 2; }

    private static void drawOverlay(Canvas canvas, int w, int h, TemplateSpec spec, long tMs) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        if ("hearts".equals(spec.overlay)) {
            for (int i = 0; i < 6; i++) {
                float seed = (i * 0.37f) % 1f;
                float x = w * (0.1f + 0.8f * seed);
                float span = 6000f;
                float ph = ((tMs + i * 977) % (long) span) / span;
                float y = h * (1.1f - 1.3f * ph);
                float size = w * (0.04f + 0.03f * ((i * 0.7f) % 1f));
                p.setColor(0xFFFF4081);
                p.setAlpha(160);
                drawHeart(canvas, p, x, y, size);
            }
        } else if ("sparkles".equals(spec.overlay)) {
            p.setColor(Color.WHITE);
            for (int i = 0; i < 14; i++) {
                float sx = ((i * 0.618f) % 1f) * w;
                float sy = ((i * 0.382f + 0.13f) % 1f) * h;
                float tw = (float) Math.abs(Math.sin((tMs / 300.0) + i * 1.7));
                p.setAlpha((int) (60 + 190 * tw));
                float r = 3f + 6f * tw;
                canvas.drawCircle(sx, sy, r, p);
            }
        } else if ("confetti".equals(spec.overlay)) {
            int[] cols = {0xFFFF4081, 0xFFFFD740, 0xFF00E676, 0xFF40C4FF, 0xFFE040FB};
            float span = 4000f;
            for (int i = 0; i < 18; i++) {
                float ph = ((tMs * 1f + i * 311) % (long) span) / span;
                float x = (((i * 0.47f) % 1f) + 0.1f * (float) Math.sin(ph * 6.28 + i)) * w;
                float y = h * ph;
                p.setColor(cols[i % cols.length]);
                p.setAlpha(220);
                canvas.drawRect(x, y, x + 12, y + 20, p);
            }
        } else if ("leak".equals(spec.overlay)) {
            float ph = (tMs % 5000L) / 5000f;
            float cx = w * ph;
            RadialGradient g = new RadialGradient(cx, h * 0.2f, w * 0.8f,
                    new int[]{0x66FF9800, 0x00FFFFFF}, null, Shader.TileMode.CLAMP);
            p.setShader(g);
            canvas.drawRect(0, 0, w, h, p);
            p.setShader(null);
        }
    }

    private static void drawHeart(Canvas canvas, Paint p, float x, float y, float s) {
        Path path = new Path();
        path.moveTo(x, y + s * 0.3f);
        path.cubicTo(x, y, x - s, y, x - s, y + s * 0.3f);
        path.cubicTo(x - s, y + s * 0.7f, x, y + s, x, y + s * 1.3f);
        path.cubicTo(x, y + s, x + s, y + s * 0.7f, x + s, y + s * 0.3f);
        path.cubicTo(x + s, y, x, y, x, y + s * 0.3f);
        canvas.drawPath(path, p);
    }

    private static void drawTexts(Canvas canvas, int w, int h, TemplateSpec spec, long tMs) {
        List<String> lines = spec.textLines;
        if (lines.isEmpty()) return;
        long dur = spec.durationMs();
        float per = (float) dur / lines.size();
        int idx = (int) Math.min(lines.size() - 1, tMs / per);
        String line = lines.get(idx);
        float lt = tMs - idx * per;
        float pop = Math.min(1f, lt / 250f);
        float scale = 0.6f + 0.4f * pop;
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        p.setTextSize(w * 0.09f * scale);
        p.setColor(Color.WHITE);
        p.setAlpha((int) (255 * pop));
        p.setTextAlign(Paint.Align.CENTER);
        p.setShadowLayer(8f, 0f, 3f, 0x80000000);
        // Text stays in the lower half, below the face zone (top-biased crops put faces high).
        float y = h * (idx % 2 == 0 ? 0.84f : 0.66f);
        float tw = p.measureText(line);
        Paint pill = new Paint(Paint.ANTI_ALIAS_FLAG);
        pill.setColor(Color.BLACK);
        pill.setAlpha((int) (70 * pop));
        canvas.drawRoundRect(new RectF(w / 2f - tw / 2f - 28f, y - p.getTextSize() * 0.85f, w / 2f + tw / 2f + 28f, y + p.getTextSize() * 0.45f), 30f, 30f, pill);
        canvas.drawText(line, w / 2f, y, p);
    }

    private static void drawVignette(Canvas canvas, int w, int h) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setShader(new RadialGradient(w / 2f, h / 2f, Math.max(w, h) * 0.75f,
                new int[]{0x00000000, 0x55000000}, null, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, p);
    }

    private static void drawWatermark(Canvas canvas, int w, int h) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setTextSize(w * 0.035f);
        p.setColor(Color.WHITE);
        p.setAlpha(140);
        p.setTextAlign(Paint.Align.RIGHT);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Mukhda", w - 24, h - 24, p);
    }

    public static void applyOverrides(android.content.Context ctx, String projectId) {
        if (ctx == null || projectId == null) {
            fxOverride = null;
            filterOverride = null;
            return;
        }
        android.content.SharedPreferences sp = ctx.getSharedPreferences("mukhda", 0);
        String fx = sp.getString("fx_" + projectId, null);
        String fl = sp.getString("filter_" + projectId, null);
        fxOverride = (fx == null || "none".equals(fx)) ? null : fx;
        filterOverride = (fl == null || "none".equals(fl)) ? null : fl;
    }

    public static ColorMatrix filterMatrix(String name) {
        if (name == null || "none".equals(name)) return null;
        ColorMatrix cm = new ColorMatrix();
        if ("bw".equals(name) || "noir".equals(name)) {
            cm.setSaturation(0f);
            if ("noir".equals(name)) {
                ColorMatrix c2 = new ColorMatrix(new float[]{
                        1.3f,0,0,0,-40, 0,1.3f,0,0,-40, 0,0,1.3f,0,-40, 0,0,0,1,0});
                cm.postConcat(c2);
            }
        } else if ("warm".equals(name)) {
            cm.set(new float[]{1.15f,0,0,0,10, 0,1.0f,0,0,0, 0,0,0.85f,0,-10, 0,0,0,1,0});
        } else if ("vivid".equals(name)) {
            cm.setSaturation(1.5f);
        } else if ("diya".equals(name)) {
            cm.set(new float[]{1.25f,0,0,0,25, 0,1.05f,0,0,10, 0,0,0.8f,0,-15, 0,0,0,1,0});
        } else if ("rose".equals(name)) {
            cm.set(new float[]{1.15f,0,0,0,15, 0,0.95f,0,0,0, 0,0,1.05f,0,5, 0,0,0,1,0});
        } else if ("cool".equals(name)) {
            cm.set(new float[]{0.9f,0,0,0,-5, 0,1.0f,0,0,0, 0,0,1.2f,0,15, 0,0,0,1,0});
        } else if ("sepia".equals(name)) {
            cm.set(new float[]{0.393f,0.769f,0.189f,0,0, 0.349f,0.686f,0.168f,0,0, 0.272f,0.534f,0.131f,0,0, 0,0,0,1,0});
        } else if ("fade".equals(name)) {
            cm.set(new float[]{0.9f,0,0,0,20, 0,0.9f,0,0,20, 0,0,0.9f,0,20, 0,0,0,1,0});
        } else if ("aqua".equals(name)) {
            cm.set(new float[]{0.85f,0,0,0,-10, 0,1.1f,0,0,10, 0,0,1.15f,0,15, 0,0,0,1,0});
        } else if ("sunset".equals(name)) {
            cm.set(new float[]{1.2f,0,0,0,20, 0,0.95f,0,0,0, 0,0,0.9f,0,-5, 0,0,0,1,0});
        } else if ("cinema".equals(name)) {
            cm.setSaturation(0.8f);
            ColorMatrix c2 = new ColorMatrix(new float[]{
                    0.95f,0,0.1f,0,0, 0,1f,0.05f,0,0, 0.1f,0,1f,0,0, 0,0,0,1,0});
            cm.postConcat(c2);
        } else {
            return null;
        }
        return cm;
    }

    public static final String[] FILTERS = {"none", "warm", "vivid", "diya", "rose", "bw", "noir", "cool", "sepia", "fade", "aqua", "sunset", "cinema"};
    public static final String[] EFFECTS = {"none", "punch", "flash", "shake", "glitch", "zoompulse", "shine"};

    public static ColorFilterHolder unused() { return null; }
    public static final class ColorFilterHolder { public PorterDuffColorFilter f; }
}
