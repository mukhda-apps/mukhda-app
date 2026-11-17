package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

public final class ExportOptions {
    public final String aspect;
    public final boolean fill;
    public final int heightCap;
    public final boolean lite;
    public final Preset preset;

    public ExportOptions(Preset preset, int i, String aspect, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(preset, "preset");
        Intrinsics.checkNotNullParameter(aspect, "aspect");
        this.preset = preset;
        this.heightCap = i;
        this.aspect = aspect;
        this.fill = z;
        this.lite = z2;
    }

    public final String getAspect() {
        return this.aspect;
    }

    public final boolean getFill() {
        return this.fill;
    }

    public final int getHeightCap() {
        return this.heightCap;
    }

    public final boolean getLite() {
        return this.lite;
    }

    public final Preset getPreset() {
        return this.preset;
    }
}
