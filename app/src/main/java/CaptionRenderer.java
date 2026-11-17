package com.mukhda.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

public final class CaptionRenderer {
    public static final CaptionRenderer INSTANCE = new CaptionRenderer();
    private static final HashMap<String, Typeface> fonts = new HashMap<>();

    private CaptionRenderer() {
    }

    public final Typeface fontFor(Context ctx, String text) {
        String str;
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(text, "text");
        String str2 = text;
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= str2.length()) {
                while (true) {
                    if (i >= str2.length()) {
                        str = null;
                        break;
                    }
                    char charAt = str2.charAt(i);
                    if (2304 <= charAt && charAt < 2432) {
                        str = "NotoSansDevanagari.ttf";
                        break;
                    }
                    i++;
                }
            } else {
                char charAt2 = str2.charAt(i2);
                if (2688 <= charAt2 && charAt2 < 2816) {
                    str = "NotoSansGujarati.ttf";
                    break;
                }
                i2++;
            }
        }
        if (str == null) {
            Typeface create = Typeface.create(Typeface.DEFAULT_BOLD, 1);
            Intrinsics.checkNotNullExpressionValue(create, "create(...)");
            return create;
        }
        HashMap<String, Typeface> hashMap = fonts;
        Typeface typeface = hashMap.get(str);
        if (typeface == null) {
            try {
                if (Build.VERSION.SDK_INT >= 26) {
                    typeface = new Typeface.Builder(ctx.getAssets(), "fonts/" + str).setFontVariationSettings("'wght' 800").build();
                    if (typeface == null) {
                        typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + str);
                    }
                } else {
                    typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + str);
                }
            } catch (Exception unused) {
                typeface = Typeface.DEFAULT_BOLD;
            }
            Intrinsics.checkNotNull(typeface);
            hashMap.put(str, typeface);
        }
        return typeface;
    }

    public final Bitmap render(Context ctx, String text, String style, int w, int h) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(style, "style");
        Bitmap createBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Intrinsics.checkNotNullExpressionValue(createBitmap, "createBitmap(...)");
        Canvas canvas = new Canvas(createBitmap);
        float min = Math.min(w, h) * 0.062f;
        TextPaint textPaint = new TextPaint(1);
        textPaint.setTextSize(min);
        textPaint.setTypeface(INSTANCE.fontFor(ctx, text));
        textPaint.setColor(-1);
        float f = w;
        int i = (int) (0.84f * f);
        if (Intrinsics.areEqual(style, "garba")) {
            textPaint.setColor(Color.parseColor("#FFC24A"));
        } else if (Intrinsics.areEqual(style, "diwali")) {
            textPaint.setColor(Color.parseColor("#1A1005"));
        } else {
            textPaint.setColor(-1);
        }
        String str = text;
        StaticLayout build = StaticLayout.Builder.obtain(str, 0, text.length(), textPaint, i).setAlignment(Layout.Alignment.ALIGN_CENTER).setLineSpacing(0.0f, 1.05f).build();
        Intrinsics.checkNotNullExpressionValue(build, "build(...)");
        Iterator<Integer> it = RangesKt.until(0, build.getLineCount()).iterator();
        if (!it.hasNext()) {
            throw new NoSuchElementException();
        }
        IntIterator intIterator = (IntIterator) it;
        float lineWidth = build.getLineWidth(intIterator.nextInt());
        while (it.hasNext()) {
            lineWidth = Math.max(lineWidth, build.getLineWidth(intIterator.nextInt()));
        }
        float f2 = (f - lineWidth) / 2.0f;
        float height = (h * 0.7f) - (build.getHeight() / 2.0f);
        float f3 = 0.35f * min;
        canvas.save();
        if (Intrinsics.areEqual(style, "diwali")) {
            Paint paint = new Paint(1);
            paint.setColor(Color.parseColor("#FFD84A"));
            float f4 = 0.6f * f3;
            canvas.drawRoundRect(f2 - f3, height - f4, f2 + lineWidth + f3, build.getHeight() + height + f4, f3, f3, paint);
            canvas.translate((w - i) / 2.0f, height);
            build.draw(canvas);
        } else if (Intrinsics.areEqual(style, "reels")) {
            Paint paint2 = new Paint(1);
            float f5 = lineWidth + f2;
            paint2.setShader(new LinearGradient(f2, 0.0f, f5, 0.0f, Color.parseColor("#FF9F2E"), Color.parseColor("#FF4D7E"), Shader.TileMode.CLAMP));
            float f6 = 0.6f * f3;
            canvas.drawRoundRect(f2 - f3, height - f6, f5 + f3, build.getHeight() + height + f6, f3, f3, paint2);
            canvas.translate((w - i) / 2.0f, height);
            build.draw(canvas);
        } else {
            TextPaint textPaint2 = new TextPaint(textPaint);
            textPaint2.setStyle(Paint.Style.STROKE);
            textPaint2.setStrokeWidth(min * 0.16f);
            textPaint2.setColor(Color.parseColor("#7A1030"));
            textPaint2.setStrokeJoin(Paint.Join.ROUND);
            StaticLayout build2 = StaticLayout.Builder.obtain(str, 0, text.length(), textPaint2, i).setAlignment(Layout.Alignment.ALIGN_CENTER).setLineSpacing(0.0f, 1.05f).build();
            Intrinsics.checkNotNullExpressionValue(build2, "build(...)");
            canvas.translate((w - i) / 2.0f, height);
            build2.draw(canvas);
            build.draw(canvas);
        }
        canvas.restore();
        return createBitmap;
    }
}
