package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public final class Lang {
    private final String code;
    private final String label;
    private final String model;
    private final boolean roman;
    private final int sizeMb;

    public Lang(String code, String label, String model, int i, boolean z) {
        Intrinsics.checkNotNullParameter(code, "code");
        Intrinsics.checkNotNullParameter(label, "label");
        Intrinsics.checkNotNullParameter(model, "model");
        this.code = code;
        this.label = label;
        this.model = model;
        this.sizeMb = i;
        this.roman = z;
    }

    public Lang(String str, String str2, String str3, int i, boolean z, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, str3, i, (i2 & 16) != 0 ? false : z);
    }

    public final String getCode() {
        return this.code;
    }

    public final String getLabel() {
        return this.label;
    }

    public final String getModel() {
        return this.model;
    }

    public final boolean getRoman() {
        return this.roman;
    }

    public final int getSizeMb() {
        return this.sizeMb;
    }
}
