package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import com.mukhda.editor.Caption;
import com.mukhda.editor.Clip;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Project {

    public static final Companion INSTANCE = new Companion(null);
    public final String captionLang;
    public final String captionStyle;
    public final List<Caption> captions;
    public final List<Clip> clips;
    public final String filter;
    public final String id;
    public final String musicUri;
    public final String name;
    public final float originalVolume;
    public final String templateId;
    public final long updatedAt;

    public static final class Companion {
        private Companion() {
        }

        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final Project fromJson(JSONObject o) {
        try {
            Intrinsics.checkNotNullParameter(o, "o");
            JSONArray jSONArray = o.getJSONArray("clips");
            JSONArray jSONArray2 = o.getJSONArray(EditorActivity.EXTRA_CAPTIONS);
            String string = o.getString("id");
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            String optString = o.optString("name", "Project");
            Intrinsics.checkNotNullExpressionValue(optString, "optString(...)");
            IntRange until = RangesKt.until(0, jSONArray.length());
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(until, 10));
            Iterator<Integer> it = until.iterator();
            while (it.hasNext()) {
                int nextInt = ((IntIterator) it).nextInt();
                Clip.Companion companion = Clip.INSTANCE;
                JSONObject jSONObject = jSONArray.getJSONObject(nextInt);
                Intrinsics.checkNotNullExpressionValue(jSONObject, "getJSONObject(...)");
                arrayList.add(companion.fromJson(jSONObject));
            }
            ArrayList arrayList2 = arrayList;
            IntRange until2 = RangesKt.until(0, jSONArray2.length());
            ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(until2, 10));
            Iterator<Integer> it2 = until2.iterator();
            while (it2.hasNext()) {
                int nextInt2 = ((IntIterator) it2).nextInt();
                Caption.Companion companion2 = Caption.INSTANCE;
                JSONObject jSONObject2 = jSONArray2.getJSONObject(nextInt2);
                Intrinsics.checkNotNullExpressionValue(jSONObject2, "getJSONObject(...)");
                arrayList3.add(companion2.fromJson(jSONObject2));
            }
            ArrayList arrayList4 = arrayList3;
            String optString2 = o.optString("cs", "diwali");
            Intrinsics.checkNotNullExpressionValue(optString2, "optString(...)");
            String optString3 = o.optString("cl", "gu");
            Intrinsics.checkNotNullExpressionValue(optString3, "optString(...)");
            String string2 = o.isNull("music") ? null : o.getString("music");
            float optDouble = (float) o.optDouble("ov", 1.0d);
            String optString4 = o.optString("filter", "none");
            Intrinsics.checkNotNullExpressionValue(optString4, "optString(...)");
            return new Project(string, optString, arrayList2, arrayList4, optString2, optString3, string2, optDouble, optString4, o.isNull("tpl") ? null : o.getString("tpl"), o.optLong("u", 0L));
        } catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    public Project() {
        this(null, null, null, null, null, null, null, 0.0f, null, null, 0L, 2047, null);
    }

    public Project(String id, String name, List<Clip> clips, List<Caption> captions, String captionStyle, String captionLang, String str, float f, String filter, String str2, long j) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(clips, "clips");
        Intrinsics.checkNotNullParameter(captions, "captions");
        Intrinsics.checkNotNullParameter(captionStyle, "captionStyle");
        Intrinsics.checkNotNullParameter(captionLang, "captionLang");
        Intrinsics.checkNotNullParameter(filter, "filter");
        this.id = id;
        this.name = name;
        this.clips = clips;
        this.captions = captions;
        this.captionStyle = captionStyle;
        this.captionLang = captionLang;
        this.musicUri = str;
        this.originalVolume = f;
        this.filter = filter;
        this.templateId = str2;
        this.updatedAt = j;
    }

    public Project(java.lang.String r14, java.lang.String r15, java.util.List r16, java.util.List r17, java.lang.String r18, java.lang.String r19, java.lang.String r20, float r21, java.lang.String r22, java.lang.String r23, long r24, int r26, kotlin.jvm.internal.DefaultConstructorMarker r27) {
        throw new UnsupportedOperationException("Method not decompiled: com.mukhda.editor.Project.<init>(java.lang.String, java.lang.String, java.util.List, java.util.List, java.lang.String, java.lang.String, java.lang.String, float, java.lang.String, java.lang.String, long, int, kotlin.jvm.internal.DefaultConstructorMarker):void");
    }

    public final String getId() {
        return this.id;
    }

    public final String getTemplateId() {
        return this.templateId;
    }

    public final long getUpdatedAt() {
        return this.updatedAt;
    }

    public final String getName() {
        return this.name;
    }

    public final List<Clip> component3() {
        return this.clips;
    }

    public final List<Caption> component4() {
        return this.captions;
    }

    public final String getCaptionStyle() {
        return this.captionStyle;
    }

    public final String getCaptionLang() {
        return this.captionLang;
    }

    public final String getMusicUri() {
        return this.musicUri;
    }

    public final float getOriginalVolume() {
        return this.originalVolume;
    }

    public final String getFilter() {
        return this.filter;
    }

    public final Project copy(String id, String name, List<Clip> clips, List<Caption> captions, String captionStyle, String captionLang, String musicUri, float originalVolume, String filter, String templateId, long updatedAt) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(clips, "clips");
        Intrinsics.checkNotNullParameter(captions, "captions");
        Intrinsics.checkNotNullParameter(captionStyle, "captionStyle");
        Intrinsics.checkNotNullParameter(captionLang, "captionLang");
        Intrinsics.checkNotNullParameter(filter, "filter");
        return new Project(id, name, clips, captions, captionStyle, captionLang, musicUri, originalVolume, filter, templateId, updatedAt);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Project)) {
            return false;
        }
        Project project = (Project) other;
        return Intrinsics.areEqual(this.id, project.id) && Intrinsics.areEqual(this.name, project.name) && Intrinsics.areEqual(this.clips, project.clips) && Intrinsics.areEqual(this.captions, project.captions) && Intrinsics.areEqual(this.captionStyle, project.captionStyle) && Intrinsics.areEqual(this.captionLang, project.captionLang) && Intrinsics.areEqual(this.musicUri, project.musicUri) && Float.compare(this.originalVolume, project.originalVolume) == 0 && Intrinsics.areEqual(this.filter, project.filter) && Intrinsics.areEqual(this.templateId, project.templateId) && this.updatedAt == project.updatedAt;
    }

    public final List<Caption> getCaptions() {
        return this.captions;
    }

    public final List<Clip> getClips() {
        return this.clips;
    }

    public final long getTotalMs() {
        Iterator it = this.clips.iterator();
        long j = 0;
        while (it.hasNext()) {
            j += ((Clip) it.next()).getLengthMs();
        }
        return j;
    }

    public int hashCode() {
        int hashCode = ((((((((((this.id.hashCode() * 31) + this.name.hashCode()) * 31) + this.clips.hashCode()) * 31) + this.captions.hashCode()) * 31) + this.captionStyle.hashCode()) * 31) + this.captionLang.hashCode()) * 31;
        String str = this.musicUri;
        int hashCode2 = (((((hashCode + (str == null ? 0 : str.hashCode())) * 31) + Float.hashCode(this.originalVolume)) * 31) + this.filter.hashCode()) * 31;
        String str2 = this.templateId;
        return ((hashCode2 + (str2 != null ? str2.hashCode() : 0)) * 31) + Long.hashCode(this.updatedAt);
    }

    public final JSONObject toJson() {
    try {
        JSONObject put = new JSONObject().put("id", this.id).put("name", this.name);
        List<Clip> list = this.clips;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(((Clip) it.next()).toJson());
        }
        JSONObject put2 = put.put("clips", new JSONArray((Collection) arrayList));
        List<Caption> list2 = this.captions;
        ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
        Iterator it2 = list2.iterator();
        while (it2.hasNext()) {
            arrayList2.add(((Caption) it2.next()).toJson());
        }
        JSONObject put3 = put2.put(EditorActivity.EXTRA_CAPTIONS, new JSONArray((Collection) arrayList2)).put("cs", this.captionStyle).put("cl", this.captionLang);
        Object obj = this.musicUri;
        if (obj == null) {
            obj = JSONObject.NULL;
        }
        JSONObject put4 = put3.put("music", obj).put("ov", this.originalVolume).put("filter", this.filter);
        Object obj2 = this.templateId;
        if (obj2 == null) {
            obj2 = JSONObject.NULL;
        }
        JSONObject put5 = put4.put("tpl", obj2).put("u", this.updatedAt);
        Intrinsics.checkNotNullExpressionValue(put5, "put(...)");
        return put5;
    } catch (Exception e) { throw new RuntimeException(e); }
    }

    public String toString() {
        return "Project(id=" + this.id + ", name=" + this.name + ", clips=" + this.clips + ", captions=" + this.captions + ", captionStyle=" + this.captionStyle + ", captionLang=" + this.captionLang + ", musicUri=" + this.musicUri + ", originalVolume=" + this.originalVolume + ", filter=" + this.filter + ", templateId=" + this.templateId + ", updatedAt=" + this.updatedAt + ")";
    }
}
