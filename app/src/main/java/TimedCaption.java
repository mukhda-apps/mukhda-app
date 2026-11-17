package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

public final class TimedCaption {
    public final long endMs;
    public final long startMs;
    public final String text;

    public TimedCaption(long j, long j2, String text) {
        Intrinsics.checkNotNullParameter(text, "text");
        this.startMs = j;
        this.endMs = j2;
        this.text = text;
    }

    public static TimedCaption copy$default(TimedCaption timedCaption, long j, long j2, String str, int i, Object obj) {
        if ((i & 1) != 0) {
            j = timedCaption.startMs;
        }
        long j3 = j;
        if ((i & 2) != 0) {
            j2 = timedCaption.endMs;
        }
        long j4 = j2;
        if ((i & 4) != 0) {
            str = timedCaption.text;
        }
        return timedCaption.copy(j3, j4, str);
    }

    public final long getStartMs() {
        return this.startMs;
    }

    public final long getEndMs() {
        return this.endMs;
    }

    public final String getText() {
        return this.text;
    }

    public final TimedCaption copy(long startMs, long endMs, String text) {
        Intrinsics.checkNotNullParameter(text, "text");
        return new TimedCaption(startMs, endMs, text);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TimedCaption)) {
            return false;
        }
        TimedCaption timedCaption = (TimedCaption) other;
        return this.startMs == timedCaption.startMs && this.endMs == timedCaption.endMs && Intrinsics.areEqual(this.text, timedCaption.text);
    }

    public int hashCode() {
        return (((Long.hashCode(this.startMs) * 31) + Long.hashCode(this.endMs)) * 31) + this.text.hashCode();
    }

    public String toString() {
        return "TimedCaption(startMs=" + this.startMs + ", endMs=" + this.endMs + ", text=" + this.text + ")";
    }
}
