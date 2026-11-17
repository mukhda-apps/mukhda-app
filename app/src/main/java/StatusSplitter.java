package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.LongRange;
import kotlin.ranges.RangesKt;

public final class StatusSplitter {
    public static final StatusSplitter INSTANCE = new StatusSplitter();
    public static final int STEP_MS = 100;

    private StatusSplitter() {
    }

    public final List<Long> cuts(long durationMs, double[] loud, long maxMs, long searchMs, long minPartMs) {
        long j;
        long j2;
        Intrinsics.checkNotNullParameter(loud, "loud");
        ArrayList arrayList = new ArrayList();
        long j3 = 0;
        while (durationMs - j3 > maxMs) {
            long j4 = j3 + maxMs;
            long j5 = j4 - searchMs;
            long j6 = j3 + minPartMs;
            if (j5 < j6) {
                j5 = j6;
            }
            if (!(loud.length == 0)) {
                double d = Double.MAX_VALUE;
                j2 = j4;
                while (j5 <= j4) {
                    long j7 = 100;
                    int i = (int) (j5 / j7);
                    int i2 = i + 2;
                    long j8 = j4;
                    if (i2 < loud.length) {
                        double d2 = ((loud[i] + loud[i + 1]) + loud[i2]) / 3.0d;
                        if (d2 < d - 1.0E-9d || Math.abs(d2 - d) < 1.0E-9d) {
                            d = d2;
                            j2 = j5;
                        }
                    }
                    j5 += j7;
                    j4 = j8;
                }
                j = j4;
            } else {
                j = j4;
                j2 = j;
            }
            long j9 = durationMs - j2;
            if (1 <= j9 && j9 < minPartMs) {
                j2 = durationMs - minPartMs;
            }
            j3 = j2 <= j3 ? j : j2;
            arrayList.add(Long.valueOf(j3));
        }
        return arrayList;
    }

    public final double[] loudness(short[] pcm) {
        Intrinsics.checkNotNullParameter(pcm, "pcm");
        int length = pcm.length / 1600;
        double[] dArr = new double[length];
        for (int i = 0; i < length; i++) {
            double d = 0.0d;
            for (int i2 = 0; i2 < 1600; i2++) {
                double d2 = pcm[(i * 1600) + i2];
                d += d2 * d2;
            }
            dArr[i] = Math.sqrt(d / 1600);
        }
        return dArr;
    }

    public final List<LongRange> parts(long durationMs, List<Long> cuts) {
        Intrinsics.checkNotNullParameter(cuts, "cuts");
        List plus = CollectionsKt.plus((Collection<? extends Long>) CollectionsKt.plus((Collection) CollectionsKt.listOf(0L), (Iterable) cuts), Long.valueOf(durationMs));
        IntRange until = RangesKt.until(0, plus.size() - 1);
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(until, 10));
        Iterator<Integer> it = until.iterator();
        while (it.hasNext()) {
            int nextInt = ((IntIterator) it).nextInt();
            arrayList.add(new LongRange(((Number) plus.get(nextInt)).longValue(), ((Number) plus.get(nextInt + 1)).longValue()));
        }
        return arrayList;
    }
}
