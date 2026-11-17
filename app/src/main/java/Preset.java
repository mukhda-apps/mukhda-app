package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

public final class Preset {
    public final String aspect;
    public final int height;
    public final String id;
    public final String label;
    public final String note;
    public final int videoKbps;

    public Preset(String id, String label, String aspect, int i, int i2, String note) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(label, "label");
        Intrinsics.checkNotNullParameter(aspect, "aspect");
        Intrinsics.checkNotNullParameter(note, "note");
        this.id = id;
        this.label = label;
        this.aspect = aspect;
        this.height = i;
        this.videoKbps = i2;
        this.note = note;
    }

    public final String getAspect() {
        return this.aspect;
    }

    public final int getHeight() {
        return this.height;
    }

    public final String getId() {
        return this.id;
    }

    public final String getLabel() {
        return this.label;
    }

    public final String getNote() {
        return this.note;
    }

    public final int getVideoKbps() {
        return this.videoKbps;
    }
}
