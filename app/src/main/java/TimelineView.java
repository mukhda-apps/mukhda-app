package com.mukhda.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

public final class TimelineView extends View {
    public List<Long> beats;
    public float downX;
    public int minWidth;
    public Function1<? super Long, Unit> onSeek;
    public Function1<? super String, Unit> onSelect;
    public final Paint p;
    public long playheadMs;
    public Project project;
    public String selectedId;
    public final int slop;
    public final HashMap<String, Bitmap> thumbs;

    public TimelineView(Context ctx) {
        super(ctx);
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        this.project = new Project(null, null, null, null, null, null, null, 0.0f, null, null, 0L, 2047, null);
        this.beats = CollectionsKt.emptyList();
        this.thumbs = new HashMap<>();
        this.onSeek = new Function1<Long, Unit>() {
            @Override
            public Unit invoke(Long l) {
                invoke(l.longValue());
                return Unit.INSTANCE;
            }

            public final void invoke(long j) {
            }
        };
        this.onSelect = new Function1<String, Unit>() {
            @Override
            public Unit invoke(String str) {
                invoke2(str);
                return Unit.INSTANCE;
            }

            public final void invoke2(String str) {
            }
        };
        this.p = new Paint(1);
        Context context = getContext();
        Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
        this.slop = UiKt.dp(context, 8);
    }

    public final int getPad() {
        Context context = getContext();
        Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
        return UiKt.dp(context, 12);
    }

    public final int getRowH() {
        Context context = getContext();
        Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
        return UiKt.dp(context, 58);
    }

    public final int getRowTop() {
        Context context = getContext();
        Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
        return UiKt.dp(context, 26);
    }

    public final float pxPerMs() {
        long max = Math.max(this.project.getTotalMs(), 1000L);
        int i = this.minWidth;
        Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
        float dp = (i - UiKt.dp(getContext(), 24)) / ((float) max);
        Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
        float dp2 = UiKt.dp(getContext(), 14) / 1000.0f;
        Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
        return RangesKt.coerceIn(dp, dp2, UiKt.dp(getContext(), 60) / 1000.0f);
    }

    public final List<Long> getBeats() {
        return this.beats;
    }

    public final int getMinWidth() {
        return this.minWidth;
    }

    public final Function1<? super Long, Unit> getOnSeek() {
        return this.onSeek;
    }

    public final Function1<? super String, Unit> getOnSelect() {
        return this.onSelect;
    }

    public final long getPlayheadMs() {
        return this.playheadMs;
    }

    public final Project getProject() {
        return this.project;
    }

    public final String getSelectedId() {
        return this.selectedId;
    }

    public final HashMap<String, Bitmap> getThumbs() {
        return this.thumbs;
    }

    @Override
    protected void onDraw(Canvas c) {
        int i;
        Intrinsics.checkNotNullParameter(c, "c");
        float pxPerMs = pxPerMs();
        List<Long> offsets = Timeline.INSTANCE.offsets(this.project.getClips());
        this.p.setStyle(Paint.Style.FILL);
        this.p.setColor(C.INSTANCE.getDim());
        Paint paint = this.p;
        Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
        int i2 = 10;
        paint.setTextSize(UiKt.dp(getContext(), 10));
        int i3 = 1;
        int totalMs = ((int) (this.project.getTotalMs() / 1000)) + 1;
        float f = 1000 * pxPerMs;
        Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
        if (f <= UiKt.dp(getContext(), 40)) {
            Context context = getContext();
            Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
            i3 = f > ((float) UiKt.dp(context, 18)) ? 5 : 10;
        }
        for (int i4 = 0; i4 <= totalMs; i4 += i3) {
            Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
            c.drawText(i4 + "s", getPad() + (i4 * 1000 * pxPerMs), UiKt.dp(getContext(), 11), this.p);
        }
        Iterator<Clip> it = this.project.getClips().iterator();
        int i5 = 0;
        while (true) {
            i = 2;
            if (!it.hasNext()) {
                break;
            }
            int i6 = i5 + 1;
            Clip next = it.next();
            float pad = getPad() + (offsets.get(i5).floatValue() * pxPerMs);
            float lengthMs = (((float) next.getLengthMs()) * pxPerMs) + pad;
            Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
            float dp = lengthMs - UiKt.dp(getContext(), 2);
            float rowTop = getRowTop();
            Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
            RectF rectF = new RectF(pad, rowTop, Math.max(dp, UiKt.dp(getContext(), i2) + pad), getRowTop() + getRowH());
            Bitmap bitmap = this.thumbs.get(next.getId());
            int save = c.save();
            Path path = new Path();
            Context context2 = getContext();
            Intrinsics.checkNotNullExpressionValue(context2, "getContext(...)");
            float dp2 = UiKt.dp(context2, 8);
            Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
            path.addRoundRect(rectF, dp2, UiKt.dp(getContext(), 8), Path.Direction.CW);
            c.clipPath(path);
            this.p.setColor(C.INSTANCE.getCard2());
            c.drawRect(rectF, this.p);
            if (bitmap != null) {
                float rowH = (getRowH() * bitmap.getWidth()) / bitmap.getHeight();
                float f2 = rectF.left;
                while (f2 < rectF.right) {
                    float f3 = f2 + rowH;
                    c.drawBitmap(bitmap, (Rect) null, new RectF(f2, rectF.top, f3, rectF.bottom), (Paint) null);
                    f2 = f3;
                }
            } else {
                this.p.setShader(new LinearGradient(rectF.left, 0.0f, rectF.right, 0.0f, C.INSTANCE.getOrange(), C.INSTANCE.getPink(), Shader.TileMode.CLAMP));
                c.drawRect(rectF, this.p);
                this.p.setShader(null);
            }
            c.restoreToCount(save);
            if (Intrinsics.areEqual(next.getId(), this.selectedId)) {
                this.p.setStyle(Paint.Style.STROKE);
                Paint paint2 = this.p;
                Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
                paint2.setStrokeWidth(UiKt.dp(getContext(), 2));
                this.p.setColor(C.INSTANCE.getSaffron());
                Context context3 = getContext();
                Intrinsics.checkNotNullExpressionValue(context3, "getContext(...)");
                float dp3 = UiKt.dp(context3, 8);
                Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
                c.drawRoundRect(rectF, dp3, UiKt.dp(getContext(), 8), this.p);
                this.p.setStyle(Paint.Style.FILL);
            }
            if (next.getSpeed() != 1.0f) {
                float width = rectF.width();
                Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
                if (width > UiKt.dp(getContext(), 30)) {
                    this.p.setColor(Color.argb(180, 0, 0, 0));
                    float f4 = 6 + rectF.left;
                    float f5 = rectF.bottom;
                    Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
                    float dp4 = f5 - UiKt.dp(getContext(), 18);
                    float f6 = rectF.left;
                    Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
                    c.drawRoundRect(f4, dp4, UiKt.dp(getContext(), 36) + f6, rectF.bottom - 4, 8.0f, 8.0f, this.p);
                    this.p.setColor(-1);
                    Paint paint3 = this.p;
                    Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
                    paint3.setTextSize(UiKt.dp(getContext(), 10));
                    String str = next.getSpeed() + "x";
                    float f7 = rectF.left + 12;
                    float f8 = rectF.bottom;
                    Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
                    c.drawText(str, f7, f8 - UiKt.dp(getContext(), 7), this.p);
                    i5 = i6;
                    i2 = 10;
                }
            }
            i5 = i6;
            i2 = 10;
        }
        for (TimedCaption timedCaption : Timeline.INSTANCE.timedCaptions(this.project)) {
            this.p.setColor(C.INSTANCE.getSaffron());
            int rowTop2 = getRowTop();
            Context context4 = getContext();
            Intrinsics.checkNotNullExpressionValue(context4, "getContext(...)");
            float dp5 = rowTop2 - UiKt.dp(context4, 8);
            float pad2 = getPad() + (((float) timedCaption.getStartMs()) * pxPerMs);
            float pad3 = getPad() + (((float) timedCaption.getEndMs()) * pxPerMs);
            Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
            c.drawRoundRect(pad2, dp5, pad3, dp5 + UiKt.dp(getContext(), 4), 4.0f, 4.0f, this.p);
            i = i;
        }
        int i7 = i;
        this.p.setColor(C.INSTANCE.getTeal());
        this.p.setAlpha(200);
        Iterator<Long> it2 = this.beats.iterator();
        while (it2.hasNext()) {
            long longValue = it2.next().longValue();
            if (longValue > this.project.getTotalMs()) {
                break;
            }
            float pad4 = getPad() + (((float) longValue) * pxPerMs);
            int rowTop3 = getRowTop() + getRowH();
            Context context5 = getContext();
            Intrinsics.checkNotNullExpressionValue(context5, "getContext(...)");
            float dp6 = rowTop3 + UiKt.dp(context5, 10);
            Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
            c.drawCircle(pad4, dp6, UiKt.dp(getContext(), i7), this.p);
        }
        this.p.setAlpha(255);
        float pad5 = getPad() + (((float) this.playheadMs) * pxPerMs);
        this.p.setColor(-1);
        Paint paint4 = this.p;
        Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
        paint4.setStrokeWidth(UiKt.dp(getContext(), i7));
        Context context6 = getContext();
        Intrinsics.checkNotNullExpressionValue(context6, "getContext(...)");
        float dp7 = UiKt.dp(context6, 14);
        int rowTop4 = getRowTop() + getRowH();
        Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
        c.drawLine(pad5, dp7, pad5, rowTop4 + UiKt.dp(getContext(), 14), this.p);
        Context context7 = getContext();
        Intrinsics.checkNotNullExpressionValue(context7, "getContext(...)");
        float dp8 = UiKt.dp(context7, 14);
        Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
        c.drawCircle(pad5, dp8, UiKt.dp(getContext(), 5), this.p);
    }

    @Override
    protected void onMeasure(int w, int h) {
        long max = Math.max(this.project.getTotalMs(), 1000L);
        int i = this.minWidth;
        int pxPerMs = ((int) (((float) max) * pxPerMs())) + (getPad() * 2);
        Context context = getContext();
        Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
        int max2 = Math.max(i, pxPerMs + UiKt.dp(context, 40));
        Context context2 = getContext();
        Intrinsics.checkNotNullExpressionValue(context2, "getContext(...)");
        setMeasuredDimension(max2, UiKt.dp(context2, 110));
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        String str;
        Integer num;
        Intrinsics.checkNotNullParameter(e, "e");
        int action = e.getAction();
        if (action == 0) {
            this.downX = e.getX();
            return true;
        }
        if (action == 1 && Math.abs(e.getX() - this.downX) < this.slop) {
            long coerceIn = RangesKt.coerceIn((long) ((e.getX() - getPad()) / pxPerMs()), 0L, this.project.getTotalMs());
            List<Long> offsets = Timeline.INSTANCE.offsets(this.project.getClips());
            Iterator<Integer> it = CollectionsKt.getIndices(this.project.getClips()).iterator();
            while (true) {
                str = null;
                if (!it.hasNext()) {
                    num = null;
                    break;
                }
                num = it.next();
                int intValue = num.intValue();
                if (coerceIn >= offsets.get(intValue).longValue() && coerceIn < offsets.get(intValue).longValue() + this.project.getClips().get(intValue).getLengthMs()) {
                    break;
                }
            }
            Integer num2 = num;
            Function1<? super String, Unit> function1 = this.onSelect;
            if (num2 != null) {
                str = this.project.getClips().get(num2.intValue()).getId();
            }
            function1.invoke(str);
            this.onSeek.invoke(Long.valueOf(coerceIn));
            performClick();
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public final float playheadX() {
        return getPad() + (((float) this.playheadMs) * pxPerMs());
    }

    public final void setBeats(List<Long> v) {
        Intrinsics.checkNotNullParameter(v, "v");
        this.beats = v;
        invalidate();
    }

    public final void setMinWidth(int i) {
        this.minWidth = i;
    }

    public final void setOnSeek(Function1<? super Long, Unit> function1) {
        Intrinsics.checkNotNullParameter(function1, "<set-?>");
        this.onSeek = function1;
    }

    public final void setOnSelect(Function1<? super String, Unit> function1) {
        Intrinsics.checkNotNullParameter(function1, "<set-?>");
        this.onSelect = function1;
    }

    public final void setPlayheadMs(long j) {
        this.playheadMs = j;
        invalidate();
    }

    public final void setProject(Project v) {
        Intrinsics.checkNotNullParameter(v, "v");
        this.project = v;
        requestLayout();
        invalidate();
    }

    public final void setSelectedId(String str) {
        this.selectedId = str;
        invalidate();
    }
}
