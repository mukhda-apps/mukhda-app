package com.mukhda.editor;

import android.content.Context;
import android.net.Uri;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;

public final class Templates {
    public static final Templates INSTANCE;
    private static final List<Template> all;
    private static final List<String> categories;

    static {
        Templates templates = new Templates();
        INSTANCE = templates;
        all = CollectionsKt.listOf(new Template[]{templates.t("navratri", "Navratri Special", "Garba beat, punch cuts", "garba_beat", 2, 15, "diya", "garba", "નવરાત્રિની શુભકામના", 4294942510L, 4294921598L, "Gujarati,Festival", true, "punch"), templates.t("diwali", "Diwali Wishes", "Warm glow, soft beat", "diwali_glow", 4, 20, "diya", "diwali", "દિવાળીની શુભકામના", 4294948925L, 4294933053L, "Gujarati,Festival,Hindi", true, "pulse"), templates.t("holi", "Holi Dhamaal", "Colourful, fast", "holi_dhamaal", 2, 15, "vivid", "reels", "હોળીની શુભકામના", 4294921598L, 4286274559L, "Festival,Hindi", false, "punch"), templates.t("garba_night", "Garba Night", "Dhol rhythm", "dhol_dhamaka", 1, 15, "diya", "garba", "Garba Night", 4294942510L, 4286274559L, "Gujarati", false, "punch"), templates.t("gujju", "Gujju Swag", "Desi swag", "dhol_dhamaka", 2, 12, "vivid", "garba", "ગુજ્જુ સ્વેગ", 4280275378L, 4294942510L, "Gujarati,Attitude", false, "pulse"), templates.t("bhakti", "Jai Shree Krishna", "Calm bhajan", "bhajan_sur", 4, 20, "warm", "diwali", "જય શ્રી કૃષ્ણ", 4294948925L, 4281325240L, "Gujarati,Hindi", false, "none"), templates.t("bollywood", "Bollywood Dance", "Filmy dance cuts", "bollywood_dance", 2, 15, "vivid", "reels", "Bollywood", 4294921598L, 4294948925L, "Bollywood,Hindi", true, "punch"), templates.t("retro", "Retro Filmy", "90s romance", "retro_90s", 2, 15, "warm", "diwali", "90s Hits", 4290283819L, 4292886682L, "Bollywood,Hindi,Love", false, "pulse"), templates.t("dil_se", "Dil Se Love", "Soft romantic", "hindi_romance", 4, 20, "warm", "diwali", "Dil Se", 4292886682L, 4286274559L, "Hindi,Love", true, "pulse"), templates.t("judaai", "Judaai", "Sad slow cuts", "sad_acoustic", 4, 15, "bw", "reels", "Judaai", 4283276031L, 4282002002L, "Hindi,Love", false, "none"), templates.t("love_story", "Love Story", "Dreamy slow cuts", "romantic_piano", 4, 20, "warm", "diwali", "Love", 4294921598L, 4286274559L, "Love,Hindi", false, "pulse"), templates.t("bhangra", "Bhangra Beat", "Dhol and energy", "punjabi_bhangra", 2, 15, "vivid", "reels", "Bhangra", 4294942510L, 4292886682L, "Punjabi,Festival", true, "punch"), templates.t("punjabi_swag", "Punjabi Swag", "Attitude trap", "punjabi_swag", 2, 12, "bw", "reels", "Swag", 4286274559L, 4292886682L, "Punjabi,Attitude", true, "punch"), templates.t("attitude", "Attitude Status", "Hard cuts on the drop", "attitude_drop", 2, 10, "bw", "reels", "Attitude", 4286274559L, 4292886682L, "Attitude,Punjabi", true, "punch"), templates.t("party", "Party Mode", "Fast party cuts", "party_anthem", 2, 15, "vivid", "reels", "Party", 4294948925L, 4294921598L, "English,Birthday", true, "punch"), templates.t("birthday", "Happy Birthday", "Party beat", "party_anthem", 2, 15, "vivid", "reels", "Happy Birthday", 4294948925L, 4294921598L, "Birthday,English", true, "pulse"), templates.t("bday_bash", "Birthday Bash", "Confetti cuts", "edm_drop", 1, 12, "vivid", "reels", "Birthday Bash", 4286274559L, 4294921598L, "Birthday,English", false, "punch"), templates.t("edm", "EDM Drop", "Flash cuts on the drop", "edm_drop", 1, 12, "vivid", "reels", "DROP", 4280275378L, 4283276031L, "English,Attitude", false, "punch"), templates.t("chill", "Chill Vibes", "Soft lo-fi", "lofi_chill", 4, 15, "warm", "reels", "Chill", 4281325240L, 4286274559L, "English,Love", false, "none"), templates.t("reels", "Reels Pop", "Trending beat", "reels_pop", 2, 15, "vivid", "reels", "Reels", 4294921598L, 4286274559L, "English,Hindi", true, "pulse"), templates.t("royal", "Royal Look", "Slow dramatic", "epic_cinematic", 4, 15, "bw", "reels", "Royal", 4290283819L, 4282001938L, "English,Attitude", false, "pulse"), templates.t("south_mass", "South Mass", "Mass hero style", "south_mass", 2, 12, "vivid", "reels", "MASS", 4294933053L, 4282001938L, "South,Attitude", true, "punch"), templates.t("south_beat", "South Beat", "Dance beat", "south_beat", 2, 15, "vivid", "reels", "South Beat", 4280275378L, 4294921598L, "South", false, "punch"), templates.t("south_melody", "South Melody", "Melody and love", "south_melody", 4, 20, "warm", "diwali", "South Melody", 4281325240L, 4292886682L, "South,Love", false, "pulse"), templates.t("wedding", "Wedding Invite", "Slow, graceful", "shaadi_sur", 4, 30, "warm", "diwali", "શુભ લગ્ન", 4281325240L, 4283276031L, "Wedding,Gujarati", true, "none"), templates.t("shaadi_vibes", "Shaadi Vibes", "Sangeet energy", "dhol_dhamaka", 2, 20, "warm", "garba", "Shaadi", 4294921598L, 4294948925L, "Wedding,Hindi", false, "pulse"), templates.t("save_date", "Save the Date", "Soft and romantic", "hindi_romance", 4, 15, "warm", "diwali", "Save the Date", 4292886682L, 4294942510L, "Wedding,Love", false, "none"), templates.t("good_morning", "Good Morning", "Calm and warm", "shaadi_sur", 4, 12, "warm", "diwali", "Suprabhat", 4294948925L, 4281325240L, "Hindi,Gujarati", false, "none")});
        categories = CollectionsKt.listOf(new String[]{"Trending", "Hindi", "Gujarati", "Punjabi", "English", "Bollywood", "South", "Festival", "Wedding", "Love", "Birthday", "Attitude"});
    }

    private Templates() {
    }

    private final Template t(String id, String title, String sub, String music, int bpc, int sec, String filter, String cap, String txt, long c1, long c2, String cats, boolean trending, String fx) {
        Track byFile = Music.INSTANCE.byFile(music);
        return new Template(id, title, sub, byFile != null ? byFile.getBpm() : 110, music, bpc, sec, filter, cap, txt, (int) c1, (int) c2, StringsKt.split(cats, new String[]{","}, false, 0), trending, fx);
    }

    public final List<Long> beatsMs(Template t, final long totalMs) {
        Intrinsics.checkNotNullParameter(t, "t");
        final double bpm = 60000.0d / t.getBpm();
        return SequencesKt.toList(SequencesKt.map(SequencesKt.takeWhile(SequencesKt.generateSequence(Double.valueOf(0.0d), new Function1<Double, Double>() {

            public final Double invoke(double d) {
                return Double.valueOf(d + bpm);
            }

            @Override
            public Double invoke(Double d) {
                return invoke(d.doubleValue());
            }
        }), new Function1<Double, Boolean>() {

            public final Boolean invoke(double d) {
                return Boolean.valueOf(d <= ((double) totalMs) + bpm);
            }

            @Override
            public Boolean invoke(Double d) {
                return invoke(d.doubleValue());
            }
        }), new Function1<Double, Long>() {
            public final Long invoke(double d) {
                return Long.valueOf((long) d);
            }

            @Override
            public Long invoke(Double d) {
                return invoke(d.doubleValue());
            }
        }));
    }

    public final Project build(Context ctx, Template t, List<Clip> sources) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(t, "t");
        Intrinsics.checkNotNullParameter(sources, "sources");
        List<Clip> layout = layout(t, sources);
        Clip clip = (Clip) CollectionsKt.firstOrNull((List) layout);
        return new Project(null, t.getTitle(), layout, clip == null ? CollectionsKt.emptyList() : CollectionsKt.listOf(new Caption(clip.getId(), clip.getStartMs(), clip.getStartMs() + Math.min(2500L, clip.getEndMs() - clip.getStartMs()), t.getTitleText())), t.getCaptionStyle(), null, musicUri(ctx, t).toString(), 0.35f, t.getFilter(), t.getId(), 0L, 1057, null);
    }

    public final Template get(String id) {
        Object obj;
        Intrinsics.checkNotNullParameter(id, "id");
        Iterator it = all.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (Intrinsics.areEqual(((Template) obj).getId(), id)) {
                break;
            }
        }
        return (Template) obj;
    }

    public final List<Template> getAll() {
        return all;
    }

    public final List<String> getCategories() {
        return categories;
    }

    public final List<Template> inCategory(String c) {
        Intrinsics.checkNotNullParameter(c, "c");
        if (Intrinsics.areEqual(c, "Trending")) {
            List<Template> list = all;
            ArrayList arrayList = new ArrayList();
            for (Object obj : list) {
                if (((Template) obj).getTrending()) {
                    arrayList.add(obj);
                }
            }
            return arrayList;
        }
        List<Template> list2 = all;
        ArrayList arrayList2 = new ArrayList();
        for (Object obj2 : list2) {
            if (((Template) obj2).getCats().contains(c)) {
                arrayList2.add(obj2);
            }
        }
        return arrayList2;
    }

    public final List<Clip> layout(Template t, List<Clip> sources) {
        float f;
        float f2;
        Clip copy;
        int i;
        Clip copy2;
        Clip copy3;
        Clip copy4;
        Intrinsics.checkNotNullParameter(t, "t");
        Intrinsics.checkNotNullParameter(sources, "sources");
        if (sources.isEmpty()) {
            return CollectionsKt.emptyList();
        }
        long bpm = (long) ((60000.0d / t.getBpm()) * t.getBeatsPerCut());
        int coerceAtLeast = RangesKt.coerceAtLeast((int) ((t.getSeconds() * 1000) / bpm), 1);
        HashMap hashMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        int i3 = 0;
        while (i3 < coerceAtLeast) {
            Clip clip = sources.get(i3 % sources.size());
            Object orDefault = hashMap.getOrDefault(clip.getId(), 0);
            Intrinsics.checkNotNullExpressionValue(orDefault, "getOrDefault(...)");
            int intValue = ((Number) orDefault).intValue();
            hashMap.put(clip.getId(), Integer.valueOf(intValue + 1));
            if (clip.isImage()) {
                String uuid = UUID.randomUUID().toString();
                long max = Math.max(clip.getDurationMs(), bpm);
                Intrinsics.checkNotNull(uuid);
                i = i3;
                copy4 = clip.copy(uuid, clip.uri, clip.isImage, max, clip.startMs, bpm, clip.speed, clip.zoom);
                arrayList.add(copy4);
            } else {
                i = i3;
                long durationMs = clip.getDurationMs();
                if (durationMs >= bpm) {
                    long j = durationMs - bpm;
                    long j2 = j <= 0 ? 0L : (intValue * bpm) % (j + 1);
                    String uuid2 = UUID.randomUUID().toString();
                    Intrinsics.checkNotNullExpressionValue(uuid2, "toString(...)");
                    copy3 = clip.copy(uuid2, clip.uri, clip.isImage, clip.durationMs, j2, j2 + bpm, clip.speed, clip.zoom);
                    arrayList.add(copy3);
                } else {
                    float coerceIn = RangesKt.coerceIn(((float) durationMs) / ((float) bpm), 0.5f, 1.0f);
                    String uuid3 = UUID.randomUUID().toString();
                    Intrinsics.checkNotNullExpressionValue(uuid3, "toString(...)");
                    copy2 = clip.copy(uuid3, clip.uri, clip.isImage, clip.durationMs, clip.startMs, durationMs, coerceIn, clip.zoom);
                    arrayList.add(copy2);
                }
            }
            i3 = i + 1;
        }
        ArrayList arrayList2 = arrayList;
        ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(arrayList2, 10));
        for (Object obj : arrayList2) {
            int i4 = i2 + 1;
            if (i2 < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            Clip clip2 = (Clip) obj;
            String fx = t.getFx();
            if (Intrinsics.areEqual(fx, "punch")) {
                if (i2 % 2 != 0) {
                    f = 1.2f;
                    f2 = f;
                }
                f2 = 1.0f;
            } else {
                if (Intrinsics.areEqual(fx, "pulse")) {
                    f = 1.1f;
                    f2 = f;
                }
                f2 = 1.0f;
            }
            copy = clip2.copy(clip2.id, clip2.uri, clip2.isImage, clip2.durationMs, clip2.startMs, clip2.endMs, clip2.speed, f2);
            arrayList3.add(copy);
            i2 = i4;
        }
        return arrayList3;
    }

    public final Uri musicUri(Context ctx, Template t) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(t, "t");
        return Music.INSTANCE.uri(ctx, t.getMusic());
    }

    public final Template withTrack(Template t, Track tr) {
        Intrinsics.checkNotNullParameter(t, "t");
        Intrinsics.checkNotNullParameter(tr, "tr");
        return new Template(t.getId(), t.getTitle(), t.getSubtitle(), tr.getBpm(), tr.getFile(), t.getBeatsPerCut(), t.getSeconds(), t.getFilter(), t.getCaptionStyle(), t.getTitleText(), t.getC1(), t.getC2(), t.getCats(), t.getTrending(), t.getFx());
    }
}
