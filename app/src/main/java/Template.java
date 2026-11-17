package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public final class Template {
    public final int beatsPerCut;
    public final int bpm;
    public final int c1;
    public final int c2;
    public final String captionStyle;
    public final List<String> cats;
    public final String filter;
    public final String fx;
    public final String id;
    public final String music;
    public final int seconds;
    public final String subtitle;
    public final String title;
    public final String titleText;
    public final boolean trending;

    public Template(String id, String title, String subtitle, int i, String music, int i2, int i3, String filter, String captionStyle, String titleText, int i4, int i5, List<String> cats, boolean z, String fx) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(title, "title");
        Intrinsics.checkNotNullParameter(subtitle, "subtitle");
        Intrinsics.checkNotNullParameter(music, "music");
        Intrinsics.checkNotNullParameter(filter, "filter");
        Intrinsics.checkNotNullParameter(captionStyle, "captionStyle");
        Intrinsics.checkNotNullParameter(titleText, "titleText");
        Intrinsics.checkNotNullParameter(cats, "cats");
        Intrinsics.checkNotNullParameter(fx, "fx");
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.bpm = i;
        this.music = music;
        this.beatsPerCut = i2;
        this.seconds = i3;
        this.filter = filter;
        this.captionStyle = captionStyle;
        this.titleText = titleText;
        this.c1 = i4;
        this.c2 = i5;
        this.cats = cats;
        this.trending = z;
        this.fx = fx;
    }

    public Template(String str, String str2, String str3, int i, String str4, int i2, int i3, String str5, String str6, String str7, int i4, int i5, List list, boolean z, String str8, int i6, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, str3, i, str4, i2, i3, str5, str6, str7, i4, i5, (i6 & 4096) != 0 ? CollectionsKt.listOf("Festival") : list, (i6 & 8192) != 0 ? false : z, (i6 & 16384) != 0 ? "none" : str8);
    }

    public final int getBeatsPerCut() {
        return this.beatsPerCut;
    }

    public final int getBpm() {
        return this.bpm;
    }

    public final int getC1() {
        return this.c1;
    }

    public final int getC2() {
        return this.c2;
    }

    public final String getCaptionStyle() {
        return this.captionStyle;
    }

    public final List<String> getCats() {
        return this.cats;
    }

    public final String getFilter() {
        return this.filter;
    }

    public final String getFx() {
        return this.fx;
    }

    public final String getId() {
        return this.id;
    }

    public final String getMusic() {
        return this.music;
    }

    public final int getSeconds() {
        return this.seconds;
    }

    public final String getSubtitle() {
        return this.subtitle;
    }

    public final String getTitle() {
        return this.title;
    }

    public final String getTitleText() {
        return this.titleText;
    }

    public final boolean getTrending() {
        return this.trending;
    }
}
