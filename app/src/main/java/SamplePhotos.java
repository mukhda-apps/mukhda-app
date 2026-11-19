package com.mukhda.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import java.util.ArrayList;
import java.util.List;

/**
 * Original, programmatically drawn demo photos used only for template previews
 * (gallery cards + detail screen) before the user picks their own photos.
 * Flat-illustration style people + scenery. No third-party assets: every
 * pixel is drawn here.
 */
public final class SamplePhotos {
    private static List<Bitmap> cache;

    private SamplePhotos() {}

    public static synchronized List<Bitmap> get(int count) {
        if (cache == null || cache.size() < count) {
            cache = new ArrayList<Bitmap>();
            for (int i = 0; i < Math.max(count, 8); i++) {
                cache.add(draw(i));
            }
        }
        return cache.subList(0, count);
    }

    private static final int[][] BGS = {
            {0xFF355C7D, 0xFFC06C84}, {0xFFFF5F6D, 0xFFFFC371}, {0xFF11998E, 0xFF38EF7D},
            {0xFF373B44, 0xFF4286F4}, {0xFF8E2DE2, 0xFF4A00E0}, {0xFFF857A6, 0xFFFF5858},
            {0xFF0F2027, 0xFF2C5364}, {0xFFFC4A1A, 0xFFF7B733},
    };
    private static final int[] SKIN = {0xFFF1C27D, 0xFFE0AC69, 0xFFC68642, 0xFFFFDBAC,
            0xFFF1C27D, 0xFFE0AC69, 0xFFC68642, 0xFFFFDBAC};
    private static final int[] HAIR = {0xFF1B1B1B, 0xFF2E1A0F, 0xFF3B2314, 0xFF141414,
            0xFF241613, 0xFF1B1B1B, 0xFF2E1A0F, 0xFF141414};
    private static final int[] CLOTH = {0xFFE91E63, 0xFF3F51B5, 0xFF009688, 0xFFFF5722,
            0xFF673AB7, 0xFFC2185B, 0xFF00796B, 0xFFAF4448};

    private static Bitmap draw(int idx) {
        int w = 720, h = 1280;
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        int[] bg = BGS[idx % BGS.length];
        p.setShader(new LinearGradient(0, 0, 0, h, bg[0], bg[1], Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, p);
        p.setShader(null);

        // glow
        float gx = w * (0.3f + 0.12f * (idx % 3));
        float gy = h * 0.2f;
        p.setShader(new RadialGradient(gx, gy, w * 0.5f,
                Color.argb(110, 255, 255, 255), Color.argb(0, 255, 255, 255), Shader.TileMode.CLAMP));
        c.drawCircle(gx, gy, w * 0.5f, p);
        p.setShader(null);

        // bokeh
        for (int i = 0; i < 7; i++) {
            p.setColor(Color.argb(30, 255, 255, 255));
            c.drawCircle((i * 173 + idx * 61) % w, 120 + (i * 211 + idx * 47) % (h / 2), 22 + (i * 13) % 34, p);
        }

        // stars / sparkle dots on darker skies
        if (idx % 2 == 0) {
            for (int i = 0; i < 22; i++) {
                p.setColor(Color.argb(90 + (i * 37) % 140, 255, 255, 255));
                c.drawCircle((i * 197 + idx * 53) % w, (i * 131 + idx * 71) % (h / 2), 2 + (i % 3), p);
            }
        }

        // person bust, centered lower half
        int skin = SKIN[idx % SKIN.length];
        int hair = HAIR[idx % HAIR.length];
        int cloth = CLOTH[idx % CLOTH.length];
        float cx = w / 2f;
        float faceR = w * 0.21f;
        float faceY = h * 0.52f;

        // hair back (long styles)
        int style = idx % 3;
        p.setColor(hair);
        if (style == 0) { // long hair behind shoulders
            c.drawRoundRect(new RectF(cx - faceR * 1.45f, faceY - faceR * 1.5f,
                    cx + faceR * 1.45f, faceY + faceR * 2.6f), faceR, faceR, p);
        } else if (style == 1) { // bun
            c.drawCircle(cx, faceY - faceR * 1.5f, faceR * 0.5f, p);
        }

        // shoulders / kurta
        p.setColor(cloth);
        Path body = new Path();
        body.moveTo(cx - faceR * 2.1f, h);
        body.lineTo(cx - faceR * 1.75f, faceY + faceR * 2.0f);
        body.quadTo(cx, faceY + faceR * 1.35f, cx + faceR * 1.75f, faceY + faceR * 2.0f);
        body.lineTo(cx + faceR * 2.1f, h);
        body.close();
        c.drawPath(body, p);

        // neckline accent
        p.setColor(Color.argb(200, 255, 215, 130));
        Path neck = new Path();
        neck.moveTo(cx - faceR * 0.55f, faceY + faceR * 1.72f);
        neck.quadTo(cx, faceY + faceR * 2.15f, cx + faceR * 0.55f, faceY + faceR * 1.72f);
        neck.lineTo(cx + faceR * 0.45f, faceY + faceR * 1.55f);
        neck.quadTo(cx, faceY + faceR * 1.95f, cx - faceR * 0.45f, faceY + faceR * 1.55f);
        neck.close();
        c.drawPath(neck, p);

        // neck
        p.setColor(skin);
        c.drawRect(cx - faceR * 0.32f, faceY + faceR * 0.9f, cx + faceR * 0.32f, faceY + faceR * 1.75f, p);

        // face
        c.drawOval(new RectF(cx - faceR, faceY - faceR * 1.15f, cx + faceR, faceY + faceR * 1.15f), p);

        // hair front
        p.setColor(hair);
        if (style == 2) { // short cap
            Path cap = new Path();
            cap.moveTo(cx - faceR, faceY - faceR * 0.2f);
            cap.quadTo(cx - faceR * 1.05f, faceY - faceR * 1.5f, cx, faceY - faceR * 1.35f);
            cap.quadTo(cx + faceR * 1.05f, faceY - faceR * 1.5f, cx + faceR, faceY - faceR * 0.2f);
            cap.quadTo(cx + faceR * 0.6f, faceY - faceR * 0.75f, cx, faceY - faceR * 0.7f);
            cap.quadTo(cx - faceR * 0.6f, faceY - faceR * 0.75f, cx - faceR, faceY - faceR * 0.2f);
            cap.close();
            c.drawPath(cap, p);
        } else { // side-swept top
            Path top = new Path();
            top.moveTo(cx - faceR * 1.05f, faceY + faceR * 0.1f);
            top.quadTo(cx - faceR * 1.2f, faceY - faceR * 1.5f, cx, faceY - faceR * 1.45f);
            top.quadTo(cx + faceR * 1.2f, faceY - faceR * 1.5f, cx + faceR * 1.05f, faceY + faceR * 0.1f);
            top.quadTo(cx + faceR * 0.55f, faceY - faceR * 0.6f, cx - faceR * 0.2f, faceY - faceR * 0.55f);
            top.quadTo(cx - faceR * 0.8f, faceY - faceR * 0.5f, cx - faceR * 1.05f, faceY + faceR * 0.1f);
            top.close();
            c.drawPath(top, p);
        }

        // eyes
        p.setColor(0xFF26211E);
        float eyeY = faceY - faceR * 0.05f;
        float eyeDX = faceR * 0.38f;
        c.drawCircle(cx - eyeDX, eyeY, faceR * 0.075f, p);
        c.drawCircle(cx + eyeDX, eyeY, faceR * 0.075f, p);
        // brows
        p.setStrokeWidth(faceR * 0.045f);
        p.setStyle(Paint.Style.STROKE);
        c.drawArc(new RectF(cx - eyeDX - faceR * 0.14f, eyeY - faceR * 0.3f, cx - eyeDX + faceR * 0.14f, eyeY - faceR * 0.1f), 200, 140, false, p);
        c.drawArc(new RectF(cx + eyeDX - faceR * 0.14f, eyeY - faceR * 0.3f, cx + eyeDX + faceR * 0.14f, eyeY - faceR * 0.1f), 200, 140, false, p);
        p.setStyle(Paint.Style.FILL);
        // smile
        p.setColor(0xFFB0483A);
        p.setStrokeWidth(faceR * 0.05f);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeCap(Paint.Cap.ROUND);
        c.drawArc(new RectF(cx - faceR * 0.3f, faceY + faceR * 0.3f, cx + faceR * 0.3f, faceY + faceR * 0.62f), 15, 150, false, p);
        p.setStyle(Paint.Style.FILL);
        // blush
        p.setColor(Color.argb(70, 255, 105, 120));
        c.drawCircle(cx - faceR * 0.55f, faceY + faceR * 0.28f, faceR * 0.13f, p);
        c.drawCircle(cx + faceR * 0.55f, faceY + faceR * 0.28f, faceR * 0.13f, p);
        // bindi / earring accents on some
        if (idx % 2 == 1) {
            p.setColor(0xFFD32F2F);
            c.drawCircle(cx, faceY - faceR * 0.42f, faceR * 0.06f, p);
        }
        p.setColor(0xFFFFD700);
        c.drawCircle(cx - faceR * 1.02f, faceY + faceR * 0.35f, faceR * 0.07f, p);
        c.drawCircle(cx + faceR * 1.02f, faceY + faceR * 0.35f, faceR * 0.07f, p);

        // bottom vignette for text legibility
        p.setShader(new LinearGradient(0, h * 0.72f, 0, h,
                Color.argb(0, 0, 0, 0), Color.argb(90, 0, 0, 0), Shader.TileMode.CLAMP));
        c.drawRect(0, h * 0.72f, w, h, p);
        p.setShader(null);

        return b;
    }
}
