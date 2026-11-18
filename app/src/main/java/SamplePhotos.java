package com.mukhda.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import java.util.ArrayList;
import java.util.List;

/**
 * Original, programmatically drawn demo photos used only for template previews
 * (gallery cards + detail screen) before the user picks their own photos.
 * No third-party assets: every pixel is drawn here.
 */
public final class SamplePhotos {
    private static List<Bitmap> cache;

    private SamplePhotos() {}

    public static synchronized List<Bitmap> get(int count) {
        if (cache == null || cache.size() < count) {
            cache = new ArrayList<Bitmap>();
            for (int i = 0; i < Math.max(count, 6); i++) {
                cache.add(draw(i));
            }
        }
        return cache.subList(0, count);
    }

    private static Bitmap draw(int idx) {
        int w = 720, h = 1280;
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        int[][] skies = {
                {0xFF2B1055, 0xFF7597DE},
                {0xFFFF5F6D, 0xFFFFC371},
                {0xFF11998E, 0xFF38EF7D},
                {0xFF373B44, 0xFF4286F4},
                {0xFF8E2DE2, 0xFF4A00E0},
                {0xFFF857A6, 0xFFFF5858},
        };
        int[] sky = skies[idx % skies.length];
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setShader(new LinearGradient(0, 0, 0, h, sky[0], sky[1], Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, p);
        p.setShader(null);

        // sun / moon
        float sunX = w * (0.25f + 0.1f * (idx % 4));
        float sunY = h * (0.18f + 0.05f * (idx % 3));
        Paint sun = new Paint(Paint.ANTI_ALIAS_FLAG);
        sun.setShader(new RadialGradient(sunX, sunY, w * 0.22f,
                Color.argb(230, 255, 244, 214), Color.argb(0, 255, 244, 214), Shader.TileMode.CLAMP));
        c.drawCircle(sunX, sunY, w * 0.22f, sun);
        sun.setShader(null);
        sun.setColor(Color.argb(255, 255, 250, 230));
        c.drawCircle(sunX, sunY, w * 0.07f, sun);

        // bokeh
        Paint bk = new Paint(Paint.ANTI_ALIAS_FLAG);
        int[][] bokeh = {{110, 420, 46}, {590, 300, 34}, {620, 560, 52}, {140, 700, 30}, {540, 820, 26}};
        for (int i = 0; i < bokeh.length; i++) {
            bk.setColor(Color.argb(36, 255, 255, 255));
            c.drawCircle(bokeh[i][0] + (idx * 37) % 80, bokeh[i][1], bokeh[i][2], bk);
        }

        // hills
        Paint hill = new Paint(Paint.ANTI_ALIAS_FLAG);
        hill.setColor(Color.argb(140, 10, 10, 26));
        Path p1 = new Path();
        p1.moveTo(0, h * 0.72f);
        p1.quadTo(w * 0.3f, h * 0.60f, w * 0.62f, h * 0.72f);
        p1.quadTo(w * 0.85f, h * 0.80f, w, h * 0.74f);
        p1.lineTo(w, h);
        p1.lineTo(0, h);
        p1.close();
        c.drawPath(p1, hill);
        hill.setColor(Color.argb(190, 6, 6, 18));
        Path p2 = new Path();
        p2.moveTo(0, h * 0.84f);
        p2.quadTo(w * 0.4f, h * 0.74f, w, h * 0.88f);
        p2.lineTo(w, h);
        p2.lineTo(0, h);
        p2.close();
        c.drawPath(p2, hill);

        // stars on darker skies
        if (idx % 2 == 0) {
            Paint st = new Paint(Paint.ANTI_ALIAS_FLAG);
            for (int i = 0; i < 26; i++) {
                float sx = (i * 197 + idx * 53) % w;
                float sy = (i * 131 + idx * 71) % (h / 2);
                st.setColor(Color.argb(90 + (i * 37) % 140, 255, 255, 255));
                c.drawCircle(sx, sy, 2 + (i % 3), st);
            }
        }

        // birds
        Paint bird = new Paint(Paint.ANTI_ALIAS_FLAG);
        bird.setColor(Color.argb(200, 20, 20, 35));
        bird.setStyle(Paint.Style.STROKE);
        bird.setStrokeWidth(4f);
        bird.setStrokeCap(Paint.Cap.ROUND);
        for (int i = 0; i < 3; i++) {
            float bx = w * (0.2f + 0.22f * i) + (idx * 29) % 60;
            float by = h * (0.30f + 0.06f * ((idx + i) % 3));
            float bs = 14 + 4 * i;
            Path bp = new Path();
            bp.moveTo(bx - bs, by);
            bp.quadTo(bx - bs / 2, by - bs * 0.8f, bx, by);
            bp.quadTo(bx + bs / 2, by - bs * 0.8f, bx + bs, by);
            c.drawPath(bp, bird);
        }

        // soft vignette bottom for text legibility in previews
        Paint vg = new Paint(Paint.ANTI_ALIAS_FLAG);
        vg.setShader(new LinearGradient(0, h * 0.7f, 0, h,
                Color.argb(0, 0, 0, 0), Color.argb(90, 0, 0, 0), Shader.TileMode.CLAMP));
        c.drawRect(0, h * 0.7f, w, h, vg);
        vg.setShader(null);

        return b;
    }
}
