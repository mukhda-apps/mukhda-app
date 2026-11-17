package com.mukhda.editor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public final class PartsBar extends View {
    public List<Long> cuts;
    public long durationMs;
    public final Paint p;

    public PartsBar(Context ctx) {
        super(ctx);
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        this.durationMs = 1L;
        this.cuts = CollectionsKt.emptyList();
        this.p = new Paint(1);
    }

    public final List<Long> getCuts() {
        return this.cuts;
    }

    public final long getDurationMs() {
        return this.durationMs;
    }

    @Override
    protected void onDraw(Canvas c) {
        Intrinsics.checkNotNullParameter(c, "c");
        RectF rectF = new RectF(0.0f, getHeight() * 0.25f, getWidth(), getHeight() * 0.75f);
        this.p.setShader(new LinearGradient(0.0f, 0.0f, getWidth(), 0.0f, C.INSTANCE.getTeal(), Color.parseColor("#2A6FD6"), Shader.TileMode.CLAMP));
        c.drawRoundRect(rectF, 20.0f, 20.0f, this.p);
        this.p.setShader(null);
        this.p.setColor(-1);
        Paint paint = this.p;
        Intrinsics.checkNotNullExpressionValue(getContext(), "getContext(...)");
        paint.setStrokeWidth(UiKt.dp(getContext(), 2));
        Iterator<Long> it = this.cuts.iterator();
        while (it.hasNext()) {
            float width = (getWidth() * ((float) it.next().longValue())) / ((float) this.durationMs);
            c.drawLine(width, getHeight() * 0.1f, width, getHeight() * 0.9f, this.p);
        }
    }

    public final void setCuts(List<Long> list) {
        Intrinsics.checkNotNullParameter(list, "<set-?>");
        this.cuts = list;
    }

    public final void setDurationMs(long j) {
        this.durationMs = j;
    }
}
