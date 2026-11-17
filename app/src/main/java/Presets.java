package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public final class Presets {
    public static final Presets INSTANCE = new Presets();
    private static final List<Preset> all = CollectionsKt.listOf(new Preset[]{new Preset("whatsapp", "WhatsApp", "orig", 720, 2200, "Small file, looks sharp on a phone"), new Preset("reel", "Instagram Reel", "9:16", 1080, 5000, "9:16 full screen"), new Preset("shorts", "YT Shorts", "9:16", 1080, 8000, "9:16 full screen"), new Preset("facebook", "Facebook", "orig", 720, 3500, "Keeps original shape")});

    private Presets() {
    }

    public final Preset get(String id) {
        Intrinsics.checkNotNullParameter(id, "id");
        for (Preset preset : all) {
            if (Intrinsics.areEqual(preset.getId(), id)) {
                return preset;
            }
        }
        throw new NoSuchElementException("Collection contains no element matching the predicate.");
    }

    public final List<Preset> getAll() {
        return all;
    }
}
