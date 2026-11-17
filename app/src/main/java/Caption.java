package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONException;
import org.json.JSONObject;

public final class Caption {

    public static final Companion INSTANCE = new Companion(null);
    public final String clipId;
    public final long srcEndMs;
    public final long srcStartMs;
    public final String text;

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final Caption fromJson(JSONObject o) {
        try {
            Intrinsics.checkNotNullParameter(o, "o");
            String string = o.getString("c");
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            long j = o.getLong("s");
            long j2 = o.getLong("e");
            String string2 = o.getString("t");
            Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
            return new Caption(string, j, j2, string2);
        } catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    public Caption(String clipId, long j, long j2, String text) {
        Intrinsics.checkNotNullParameter(clipId, "clipId");
        Intrinsics.checkNotNullParameter(text, "text");
        this.clipId = clipId;
        this.srcStartMs = j;
        this.srcEndMs = j2;
        this.text = text;
    }

    public static Caption copy$default(Caption caption, String str, long j, long j2, String str2, int i, Object obj) {
    try {
        if ((i & 1) != 0) {
            str = caption.clipId;
        }
        if ((i & 2) != 0) {
            j = caption.srcStartMs;
        }
        long j3 = j;
        if ((i & 4) != 0) {
            j2 = caption.srcEndMs;
        }
        long j4 = j2;
        if ((i & 8) != 0) {
            str2 = caption.text;
        }
        return caption.copy(str, j3, j4, str2);
    } catch (Exception e) { throw new RuntimeException(e); }
    }

    public final String getClipId() {
        return this.clipId;
    }

    public final long getSrcStartMs() {
        return this.srcStartMs;
    }

    public final long getSrcEndMs() {
        return this.srcEndMs;
    }

    public final String getText() {
        return this.text;
    }

    public final Caption copy(String clipId, long srcStartMs, long srcEndMs, String text) {
        Intrinsics.checkNotNullParameter(clipId, "clipId");
        Intrinsics.checkNotNullParameter(text, "text");
        return new Caption(clipId, srcStartMs, srcEndMs, text);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Caption)) {
            return false;
        }
        Caption caption = (Caption) other;
        return Intrinsics.areEqual(this.clipId, caption.clipId) && this.srcStartMs == caption.srcStartMs && this.srcEndMs == caption.srcEndMs && Intrinsics.areEqual(this.text, caption.text);
    }

    public int hashCode() {
        return (((((this.clipId.hashCode() * 31) + Long.hashCode(this.srcStartMs)) * 31) + Long.hashCode(this.srcEndMs)) * 31) + this.text.hashCode();
    }

    public final JSONObject toJson() {
    try {
        return new JSONObject().put("c", this.clipId).put("s", this.srcStartMs).put("e", this.srcEndMs).put("t", this.text);
    } catch (Exception e) { throw new RuntimeException(e); }
    }

    public String toString() {
        return "Caption(clipId=" + this.clipId + ", srcStartMs=" + this.srcStartMs + ", srcEndMs=" + this.srcEndMs + ", text=" + this.text + ")";
    }
}
