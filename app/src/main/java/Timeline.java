package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;

public final class Timeline {
    public static final Timeline INSTANCE = new Timeline();

    private Timeline() {
    }

    private final Project touch(Project project) {
        Project copy;
        copy = project.copy(project.id, project.name, project.clips, project.captions, project.captionStyle, project.captionLang, project.musicUri, project.originalVolume, project.filter, project.templateId, System.currentTimeMillis());
        return copy;
    }

    public final List<Long> offsets(List<Clip> clips) {
        Intrinsics.checkNotNullParameter(clips, "clips");
        List<Clip> list = clips;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator it = list.iterator();
        long j = 0;
        while (it.hasNext()) {
            long lengthMs = ((Clip) it.next()).getLengthMs() + j;
            arrayList.add(Long.valueOf(j));
            j = lengthMs;
        }
        return arrayList;
    }

    public final Project snapToBeats(Project p, List<Long> beatsMs) {
        Project copy;
        Object obj;
        long j;
        Clip copy2;
        Clip copy3;
        List<Long> beatsMs2 = beatsMs;
        Intrinsics.checkNotNullParameter(p, "p");
        Intrinsics.checkNotNullParameter(beatsMs2, "beatsMs");
        if (beatsMs.size() < 2) {
            return p;
        }
        long longValue = (((Number) CollectionsKt.last((List) beatsMs)).longValue() - ((Number) CollectionsKt.first((List) beatsMs)).longValue()) / (beatsMs.size() - 1);
        if (longValue < 200) {
            return p;
        }
        ArrayList arrayList = new ArrayList();
        long j2 = 0;
        for (Clip clip : p.getClips()) {
            long lengthMs = clip.getLengthMs() + j2;
            List<Long> list = beatsMs2;
            Iterator it = list.iterator();
            if (it.hasNext()) {
                Object next = it.next();
                if (it.hasNext()) {
                    long abs = Math.abs(((Number) next).longValue() - lengthMs);
                    do {
                        Object next2 = it.next();
                        long abs2 = Math.abs(((Number) next2).longValue() - lengthMs);
                        if (abs > abs2) {
                            next = next2;
                            abs = abs2;
                        }
                    } while (it.hasNext());
                }
                obj = next;
            } else {
                obj = null;
            }
            Long l = (Long) obj;
            if (l != null) {
                lengthMs = l.longValue();
            }
            long j3 = lengthMs - j2;
            long j4 = j3 < longValue ? longValue : j3;
            long startMs = clip.getStartMs() + ((long)(j4 * clip.getSpeed()));
            if (clip.isImage()) {
                j = j4;
                copy3 = clip.copy(clip.id, clip.uri, clip.isImage, clip.durationMs, clip.startMs, startMs, clip.speed, clip.zoom);
                arrayList.add(copy3);
            } else {
                j = j4;
                if (startMs > clip.getDurationMs()) {
                    long durationMs = (long) (((float) (clip.getDurationMs() - clip.getStartMs())) / clip.getSpeed());
                    ArrayList arrayList2 = new ArrayList();
                    for (Object obj2 : list) {
                        long longValue2 = ((Number) obj2).longValue() - j2;
                        if (longValue <= longValue2 && longValue2 <= durationMs) {
                            arrayList2.add(obj2);
                        }
                    }
                    Long l2 = (Long) CollectionsKt.maxOrNull((Iterable<Long>) arrayList2);
                    if (l2 == null) {
                        arrayList.add(clip);
                        j2 += clip.getLengthMs();
                        beatsMs2 = beatsMs;
                    } else {
                        long longValue3 = l2.longValue() - j2;
                        startMs = clip.getStartMs() + ((long)(longValue3 * clip.getSpeed()));
                        j = longValue3;
                    }
                }
                copy2 = clip.copy(clip.id, clip.uri, clip.isImage, clip.durationMs, clip.startMs, startMs, clip.speed, clip.zoom);
                arrayList.add(copy2);
            }
            j2 += j;
            beatsMs2 = beatsMs;
        }
        copy = p.copy(p.id, p.name, arrayList, p.captions, p.captionStyle, p.captionLang, p.musicUri, p.originalVolume, p.filter, p.templateId, p.updatedAt);
        return touch(copy);
    }

    public final Project split(Project p, long timelineMs) {
        Integer num;
        Clip copy;
        Clip copy2;
        Project copy3;
        long j;
        List emptyList;
        long j2;
        List emptyList2;
        List plus;
        Clip copy4;
        Clip copy5;
        Project copy6;
        Intrinsics.checkNotNullParameter(p, "p");
        List<Long> offsets = offsets(p.getClips());
        Iterator<Integer> it = CollectionsKt.getIndices(p.getClips()).iterator();
        while (true) {
            if (!it.hasNext()) {
                num = null;
                break;
            }
            num = it.next();
            int intValue = num.intValue();
            if (timelineMs > offsets.get(intValue).longValue() && timelineMs < offsets.get(intValue).longValue() + p.getClips().get(intValue).getLengthMs()) {
                break;
            }
        }
        Integer num2 = num;
        if (num2 != null) {
            int intValue2 = num2.intValue();
            Clip clip = p.getClips().get(intValue2);
            if (clip.isImage()) {
                long longValue = timelineMs - offsets.get(intValue2).longValue();
                if (longValue < 300 || clip.getLengthMs() - longValue < 300) {
                    return null;
                }
                String uuid = UUID.randomUUID().toString();
                Intrinsics.checkNotNullExpressionValue(uuid, "toString(...)");
                copy4 = clip.copy(uuid, clip.uri, clip.isImage, clip.durationMs, clip.startMs, clip.getStartMs() + longValue, clip.speed, clip.zoom);
                String uuid2 = UUID.randomUUID().toString();
                long startMs = clip.getStartMs() + ((clip.getEndMs() - clip.getStartMs()) - longValue);
                long startMs2 = clip.getStartMs();
                Intrinsics.checkNotNull(uuid2);
                copy5 = clip.copy(uuid2, clip.uri, clip.isImage, clip.durationMs, startMs2, startMs, clip.speed, clip.zoom);
                copy6 = p.copy(p.id, p.name, CollectionsKt.plus((Collection) CollectionsKt.plus((Collection<? extends Clip>) CollectionsKt.plus((Collection<? extends Clip>) CollectionsKt.take(p.getClips(), intValue2), copy4), copy5), (Iterable) CollectionsKt.drop(p.getClips(), intValue2 + 1)), p.captions, p.captionStyle, p.captionLang, p.musicUri, p.originalVolume, p.filter, p.templateId, p.updatedAt);
                return touch(copy6);
            }
            long startMs3 = clip.getStartMs() + ((long)((timelineMs - offsets.get(intValue2).longValue()) * clip.getSpeed()));
            if (startMs3 - clip.getStartMs() >= 300 && clip.getEndMs() - startMs3 >= 300) {
                String uuid3 = UUID.randomUUID().toString();
                Intrinsics.checkNotNullExpressionValue(uuid3, "toString(...)");
                long j3 = startMs3;
                copy = clip.copy(uuid3, clip.uri, clip.isImage, clip.durationMs, clip.startMs, j3, clip.speed, clip.zoom);
                String uuid4 = UUID.randomUUID().toString();
                Intrinsics.checkNotNullExpressionValue(uuid4, "toString(...)");
                copy2 = clip.copy(uuid4, clip.uri, clip.isImage, clip.durationMs, j3, clip.endMs, clip.speed, clip.zoom);
                List<Caption> captions = p.getCaptions();
                ArrayList arrayList = new ArrayList();
                for (Caption caption : captions) {
                    if (Intrinsics.areEqual(caption.getClipId(), clip.getId())) {
                        long j4 = j3;
                        if (caption.getSrcStartMs() < j4) {
                            j = j4;
                            emptyList = CollectionsKt.listOf(Caption.copy$default(caption, copy.getId(), 0L, Math.min(caption.getSrcEndMs(), j4), null, 10, null));
                        } else {
                            j = j4;
                            emptyList = CollectionsKt.emptyList();
                        }
                        List list = emptyList;
                        long j5 = j;
                        if (caption.getSrcEndMs() > j5) {
                            j2 = j5;
                            emptyList2 = CollectionsKt.listOf(Caption.copy$default(caption, copy2.getId(), Math.max(caption.getSrcStartMs(), j5), 0L, null, 12, null));
                        } else {
                            j2 = j5;
                            emptyList2 = CollectionsKt.emptyList();
                        }
                        plus = CollectionsKt.plus((Collection) list, (Iterable) emptyList2);
                    } else {
                        plus = CollectionsKt.listOf(caption);
                        j2 = j3;
                    }
                    CollectionsKt.addAll(arrayList, plus);
                    j3 = j2;
                }
                copy3 = p.copy(p.id, p.name, CollectionsKt.plus((Collection) CollectionsKt.plus((Collection<? extends Clip>) CollectionsKt.plus((Collection<? extends Clip>) CollectionsKt.take(p.getClips(), intValue2), copy), copy2), (Iterable) CollectionsKt.drop(p.getClips(), intValue2 + 1)), arrayList, p.captionStyle, p.captionLang, p.musicUri, p.originalVolume, p.filter, p.templateId, p.updatedAt);
                return touch(copy3);
            }
        }
        return null;
    }

    public final List<TimedCaption> timedCaptions(Project p) {
        Intrinsics.checkNotNullParameter(p, "p");
        List<Long> offsets = offsets(p.getClips());
        ArrayList arrayList = new ArrayList();
        for (Caption caption : p.getCaptions()) {
            Iterator<Clip> it = p.getClips().iterator();
            int i = 0;
            while (true) {
                if (!it.hasNext()) {
                    i = -1;
                    break;
                }
                if (Intrinsics.areEqual(it.next().getId(), caption.getClipId())) {
                    break;
                }
                i++;
            }
            if (i >= 0) {
                Clip clip = p.getClips().get(i);
                long max = Math.max(caption.getSrcStartMs(), clip.getStartMs());
                if (Math.min(caption.getSrcEndMs(), clip.getEndMs()) > max) {
                    long longValue = offsets.get(i).longValue() + ((long)(((float) (max - clip.getStartMs())) / clip.getSpeed()));
                    long longValue2 = offsets.get(i).longValue() + ((long)(((float) (Math.min(caption.getSrcEndMs(), clip.getEndMs()) - clip.getStartMs())) / clip.getSpeed()));
                    if (longValue2 > longValue) {
                        arrayList.add(new TimedCaption(longValue, longValue2, caption.getText()));
                    }
                }
            }
        }
        return CollectionsKt.sortedWith(arrayList, new Comparator() {
            @Override
            public final int compare(Object t, Object t2) {
                return ComparisonsKt.compareValues(Long.valueOf(((TimedCaption) t).getStartMs()), Long.valueOf(((TimedCaption) t2).getStartMs()));
            }
        });
    }
}
