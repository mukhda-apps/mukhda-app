package com.mukhda.editor;

import org.json.JSONException;
import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONObject;

public final class Clip {

    public static final Companion INSTANCE = new Companion(null);
    public final long durationMs;
    public final long endMs;
    public final String id;
    public final boolean isImage;
    public final float speed;
    public final long startMs;
    public final String uri;
    public final float zoom;

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final Clip fromJson(JSONObject o) {
        try {
            Intrinsics.checkNotNullParameter(o, "o");
            String string = o.getString("id");
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            String string2 = o.getString(MusicActivity.EXTRA_URI);
            Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
            return new Clip(string, string2, o.optBoolean("img"), o.getLong("dur"), o.getLong("s"), o.getLong("e"), (float) o.optDouble("sp", 1.0d), (float) o.optDouble("z", 1.0d));
        } catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    public Clip(String id, String uri, boolean z, long j, long j2, long j3, float f, float f2) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(uri, "uri");
        this.id = id;
        this.uri = uri;
        this.isImage = z;
        this.durationMs = j;
        this.startMs = j2;
        this.endMs = j3;
        this.speed = f;
        this.zoom = f2;
    }

    public Clip(java.lang.String id, java.lang.String uri, boolean z, long j, long j2, long j3, float f, float f2, int mask, kotlin.jvm.internal.DefaultConstructorMarker marker) {
        this((mask & 1) != 0 ? java.util.UUID.randomUUID().toString() : id,
                uri, z, j, j2, j3,
                (mask & 64) != 0 ? 1f : f,
                (mask & 128) != 0 ? 1f : f2);
    }

    public final String getId() {
        return this.id;
    }

    public final String getUri() {
        return this.uri;
    }

    public final boolean getIsImage() {
        return this.isImage;
    }

    public final long getDurationMs() {
        return this.durationMs;
    }

    public final long getStartMs() {
        return this.startMs;
    }

    public final long getEndMs() {
        return this.endMs;
    }

    public final float getSpeed() {
        return this.speed;
    }

    public final float getZoom() {
        return this.zoom;
    }

    public final Clip copy(String id, String uri, boolean isImage, long durationMs, long startMs, long endMs, float speed, float zoom) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(uri, "uri");
        return new Clip(id, uri, isImage, durationMs, startMs, endMs, speed, zoom);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Clip)) {
            return false;
        }
        Clip clip = (Clip) other;
        return Intrinsics.areEqual(this.id, clip.id) && Intrinsics.areEqual(this.uri, clip.uri) && this.isImage == clip.isImage && this.durationMs == clip.durationMs && this.startMs == clip.startMs && this.endMs == clip.endMs && Float.compare(this.speed, clip.speed) == 0 && Float.compare(this.zoom, clip.zoom) == 0;
    }

    public final long getLengthMs() {
        return (long) (((float) (this.endMs - this.startMs)) / this.speed);
    }

    public int hashCode() {
        return (((((((((((((this.id.hashCode() * 31) + this.uri.hashCode()) * 31) + Boolean.hashCode(this.isImage)) * 31) + Long.hashCode(this.durationMs)) * 31) + Long.hashCode(this.startMs)) * 31) + Long.hashCode(this.endMs)) * 31) + Float.hashCode(this.speed)) * 31) + Float.hashCode(this.zoom);
    }

    public final boolean isImage() {
        return this.isImage;
    }

    public final JSONObject toJson() {
    try {
        return new JSONObject().put("id", this.id).put(MusicActivity.EXTRA_URI, this.uri).put("img", this.isImage).put("dur", this.durationMs).put("s", this.startMs).put("e", this.endMs).put("sp", this.speed).put("z", this.zoom);
    } catch (Exception e) { throw new RuntimeException(e); }
    }

    public String toString() {
        return "Clip(id=" + this.id + ", uri=" + this.uri + ", isImage=" + this.isImage + ", durationMs=" + this.durationMs + ", startMs=" + this.startMs + ", endMs=" + this.endMs + ", speed=" + this.speed + ", zoom=" + this.zoom + ")";
    }
}
