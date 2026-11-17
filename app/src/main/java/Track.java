package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

public final class Track {
    public final int bpm;
    public final String file;
    public final List<String> langs;
    public final String mood;
    public final String title;

    public Track(String file, String title, List<String> langs, String mood, int i) {
        Intrinsics.checkNotNullParameter(file, "file");
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(langs, "langs");
        Intrinsics.checkNotNullParameter(mood, "mood");
        this.file = file;
        this.title = title;
        this.langs = langs;
        this.mood = mood;
        this.bpm = i;
    }

    public final int getBpm() {
        return this.bpm;
    }

    public final String getFile() {
        return this.file;
    }

    public final List<String> getLangs() {
        return this.langs;
    }

    public final String getMood() {
        return this.mood;
    }

    public final String getTitle() {
        return this.title;
    }
}
