package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;

public final class AdConfig {
    public static final AdConfig INSTANCE = new AdConfig();

    private AdConfig() {
    }

    public final boolean getEnabled() {
        return false;
    }
}
