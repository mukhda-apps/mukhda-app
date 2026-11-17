package com.mukhda.editor;

import android.graphics.Color;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;

public final class C {
    public static final C INSTANCE = new C();
    private static final int bg = Color.parseColor("#0F0F14");
    private static final int card = Color.parseColor("#1A1A22");
    private static final int card2 = Color.parseColor("#23232D");
    private static final int stroke = Color.parseColor("#3A3A46");
    private static final int text = Color.parseColor("#F4F4F8");
    private static final int dim = Color.parseColor("#9A9AAA");
    private static final int saffron = Color.parseColor("#FF9F2E");
    private static final int orange = Color.parseColor("#FF7A3D");
    private static final int pink = Color.parseColor("#FF4D7E");
    private static final int green = Color.parseColor("#25D366");
    private static final int teal = Color.parseColor("#1FD1B2");

    private C() {
    }

    public final int getBg() {
        return bg;
    }

    public final int getCard() {
        return card;
    }

    public final int getCard2() {
        return card2;
    }

    public final int getDim() {
        return dim;
    }

    public final int getGreen() {
        return green;
    }

    public final int getOrange() {
        return orange;
    }

    public final int getPink() {
        return pink;
    }

    public final int getSaffron() {
        return saffron;
    }

    public final int getStroke() {
        return stroke;
    }

    public final int getTeal() {
        return teal;
    }

    public final int getText() {
        return text;
    }
}
