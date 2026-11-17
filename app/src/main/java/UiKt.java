package com.mukhda.editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

public final class UiKt {
    private static final int MATCH = -1;
    private static final int WRAP = -2;

    public static final TextView chip(Context context, String text, boolean z, final Function0<Unit> onClick) {
        Intrinsics.checkNotNullParameter(context, "<this>");
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(onClick, "onClick");
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(2, 13.0f);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(17);
        textView.setPadding(dp(context, 14), dp(context, 8), dp(context, 14), dp(context, 8));
        if (z) {
            textView.setTextColor(Color.parseColor("#1A1005"));
            textView.setBackground(gradient$default(C.INSTANCE.getSaffron(), C.INSTANCE.getOrange(), dp(context, 20), null, 8, null));
        } else {
            textView.setTextColor(C.INSTANCE.getText());
            textView.setBackground(solid(C.INSTANCE.getCard(), dp(context, 20), C.INSTANCE.getStroke(), dp(context, 1)));
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                UiKt.chip$lambda$6$lambda$5(onClick, view);
            }
        });
        return textView;
    }

    public static final void chip$lambda$6$lambda$5(Function0 onClick, View view) {
        Intrinsics.checkNotNullParameter(onClick, "$onClick");
        onClick.invoke();
    }

    public static final int dp(Context context, int i) {
        Intrinsics.checkNotNullParameter(context, "<this>");
        return (int) TypedValue.applyDimension(1, i, context.getResources().getDisplayMetrics());
    }

    public static final String fmtSize(long j) {
        double d = j;
        String format = j >= 1000000 ? String.format("%.0f MB", Arrays.copyOf(new Object[]{Double.valueOf(d / 1000000.0d)}, 1)) : String.format("%.0f KB", Arrays.copyOf(new Object[]{Double.valueOf(d / 1000.0d)}, 1));
        Intrinsics.checkNotNullExpressionValue(format, "format(...)");
        return format;
    }

    public static final String fmtTime(long j) {
        long j2 = j / 1000;
        long j3 = 60;
        String format = String.format("%d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(j2 / j3), Long.valueOf(j2 % j3)}, 2));
        Intrinsics.checkNotNullExpressionValue(format, "format(...)");
        return format;
    }

    public static final FrameLayout frame(Context ctx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        FrameLayout frameLayout = new FrameLayout(ctx);
        frameLayout.setBackgroundColor(C.INSTANCE.getBg());
        return frameLayout;
    }

    public static final int getMATCH() {
        return MATCH;
    }

    public static final int getWRAP() {
        return WRAP;
    }

    public static final GradientDrawable gradient(int i, int i2, float f, GradientDrawable.Orientation angle) {
        Intrinsics.checkNotNullParameter(angle, "angle");
        GradientDrawable gradientDrawable = new GradientDrawable(angle, new int[]{i, i2});
        gradientDrawable.setCornerRadius(f);
        return gradientDrawable;
    }

    public static GradientDrawable gradient$default(int i, int i2, float f, GradientDrawable.Orientation orientation, int i3, Object obj) {
        if ((i3 & 8) != 0) {
            orientation = GradientDrawable.Orientation.LEFT_RIGHT;
        }
        return gradient(i, i2, f, orientation);
    }

    public static final TextView label(Context context, String text, float f, int i, boolean z, int i2) {
        Intrinsics.checkNotNullParameter(context, "<this>");
        Intrinsics.checkNotNullParameter(text, "text");
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(2, f);
        textView.setTextColor(i);
        if (z) {
            textView.setTypeface(Typeface.DEFAULT_BOLD);
        }
        textView.setGravity(i2);
        textView.setIncludeFontPadding(false);
        return textView;
    }

    public static TextView label$default(Context context, String str, float f, int i, boolean z, int i2, int i3, Object obj) {
        if ((i3 & 2) != 0) {
            f = 14.0f;
        }
        float f2 = f;
        if ((i3 & 4) != 0) {
            i = C.INSTANCE.getText();
        }
        int i4 = i;
        if ((i3 & 8) != 0) {
            z = false;
        }
        boolean z2 = z;
        if ((i3 & 16) != 0) {
            i2 = GravityCompat.START;
        }
        return label(context, str, f2, i4, z2, i2);
    }

    public static final LinearLayout.LayoutParams lp(int i, int i2, float f) {
        return new LinearLayout.LayoutParams(i, i2, f);
    }

    public static LinearLayout.LayoutParams lp$default(int i, int i2, float f, int i3, Object obj) {
        if ((i3 & 4) != 0) {
            f = 0.0f;
        }
        return lp(i, i2, f);
    }

    public static final TextView pillButton(Context context, String text, int i, int i2, int i3, final Function0<Unit> onClick) {
        Intrinsics.checkNotNullParameter(context, "<this>");
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(onClick, "onClick");
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(2, 16.0f);
        textView.setTextColor(i3);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(17);
        textView.setBackground(gradient$default(i, i2, dp(context, 28), null, 8, null));
        textView.setPadding(dp(context, 20), dp(context, 15), dp(context, 20), dp(context, 15));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                UiKt.pillButton$lambda$4$lambda$3(onClick, view);
            }
        });
        return textView;
    }

    public static TextView pillButton$default(Context context, String str, int i, int i2, int i3, Function0 function0, int i4, Object obj) {
        if ((i4 & 2) != 0) {
            i = C.INSTANCE.getSaffron();
        }
        int i5 = i;
        if ((i4 & 4) != 0) {
            i2 = C.INSTANCE.getPink();
        }
        int i6 = i2;
        if ((i4 & 8) != 0) {
            i3 = -1;
        }
        return pillButton(context, str, i5, i6, i3, function0);
    }

    public static final void pillButton$lambda$4$lambda$3(Function0 onClick, View view) {
        Intrinsics.checkNotNullParameter(onClick, "$onClick");
        onClick.invoke();
    }

    public static final GradientDrawable solid(int i, float f, int i2, int i3) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(i);
        gradientDrawable.setCornerRadius(f);
        if (i3 > 0) {
            gradientDrawable.setStroke(i3, i2);
        }
        return gradientDrawable;
    }

    public static GradientDrawable solid$default(int i, float f, int i2, int i3, int i4, Object obj) {
        if ((i4 & 4) != 0) {
            i2 = 0;
        }
        if ((i4 & 8) != 0) {
            i3 = 0;
        }
        return solid(i, f, i2, i3);
    }

    public static final View spacer(Context context, int i) {
        Intrinsics.checkNotNullParameter(context, "<this>");
        View view = new View(context);
        view.setLayoutParams(lp$default(1, dp(context, i), 0.0f, 4, null));
        return view;
    }
}
