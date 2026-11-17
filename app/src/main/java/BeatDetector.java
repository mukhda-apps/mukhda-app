package com.mukhda.editor;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

public final class BeatDetector {
    private static final int HOP = 256;
    public static final BeatDetector INSTANCE = new BeatDetector();
    private static final int WIN = 512;

    public static final class Result {
        private final List<Long> beatsMs;
        private final float bpm;

        public Result(float f, List<Long> beatsMs) {
            Intrinsics.checkNotNullParameter(beatsMs, "beatsMs");
            this.bpm = f;
            this.beatsMs = beatsMs;
        }

        public final List<Long> getBeatsMs() {
            return this.beatsMs;
        }

        public final float getBpm() {
            return this.bpm;
        }
    }

    private BeatDetector() {
    }

    public static Result detect$default(BeatDetector beatDetector, short[] sArr, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 16000;
        }
        return beatDetector.detect(sArr, i);
    }

    public final Result detect(short[] pcm, int rate) {
        double d;
        int i;
        double d2;
        double d3;
        Intrinsics.checkNotNullParameter(pcm, "pcm");
        int length = (pcm.length - 512) / 256;
        if (length < 200) {
            return null;
        }
        double d4 = rate;
        double d5 = 256.0d / d4;
        double[] dArr = new double[length];
        int i2 = 0;
        while (true) {
            d = 0.0d;
            if (i2 >= length) {
                break;
            }
            int i3 = i2 * 256;
            for (int i4 = 0; i4 < 512; i4++) {
                double d6 = pcm[i3 + i4] / 32768.0d;
                d += d6 * d6;
            }
            dArr[i2] = Math.log(((d * 1000.0d) / 512) + 1.0d);
            i2++;
        }
        double[] dArr2 = new double[length];
        for (int i5 = 1; i5 < length; i5++) {
            dArr2[i5] = Math.max(0.0d, dArr[i5] - dArr[i5 - 1]);
        }
        int coerceAtLeast = RangesKt.coerceAtLeast((int) (0.5d / d5), 4);
        double[] dArr3 = new double[length];
        double d7 = 0.0d;
        for (int i6 = 0; i6 < length; i6++) {
            d7 += dArr2[i6];
            int i7 = 2 * coerceAtLeast;
            if (i6 >= i7) {
                d7 -= dArr2[i6 - i7];
            }
            dArr3[i6] = Math.max(0.0d, dArr2[i6] - (d7 / Math.min(i6 + 1, i7)));
        }
        int i8 = (int) (0.35294117647058826d / d5);
        int i9 = (int) (0.8571428571428571d / d5);
        double[] dArr4 = new double[i9 + 2];
        if (i8 <= i9) {
            int i10 = i8;
            while (true) {
                double d8 = d;
                for (int i11 = i10; i11 < length; i11++) {
                    d8 += dArr3[i11] * dArr3[i11 - i10];
                }
                dArr4[i10] = d8 / (length - i10);
                if (i10 == i9) {
                    break;
                }
                i10++;
                d = 0.0d;
            }
        }
        if (i8 <= i9) {
            int i12 = i8;
            i = 0;
            d2 = -1.0d;
            while (true) {
                int i13 = i12 * 2;
                double d9 = dArr4[i12] + (i13 <= i9 ? dArr4[i13] * 0.5d : 0.0d);
                if (d9 > d2) {
                    i = i12;
                    d2 = d9;
                }
                if (i12 == i9) {
                    break;
                }
                i12++;
            }
        } else {
            i = 0;
            d2 = -1.0d;
        }
        if (i == 0 || d2 <= 0.0d) {
            return null;
        }
        double d10 = i;
        if (i <= i8 || i >= i9) {
            d3 = 0.0d;
        } else {
            double d11 = dArr4[i - 1];
            double d12 = dArr4[i];
            double d13 = dArr4[i + 1];
            double d14 = (d11 - (2 * d12)) + d13;
            d3 = 0.0d;
            if (d14 != 0.0d) {
                d10 += ((d11 - d13) * 0.5d) / d14;
            }
        }
        int coerceAtLeast2 = RangesKt.coerceAtLeast((int) d10, 1);
        int i14 = 0;
        int i15 = 0;
        double d15 = -1.0d;
        while (i14 < coerceAtLeast2) {
            double d16 = d5;
            double d17 = d3;
            for (double d18 = i14; d18 < length; d18 += d10) {
                int i16 = (int) d18;
                double d19 = dArr3[i16] + ((i16 > 0 ? dArr3[i16 - 1] : d3) * 0.5d);
                int i17 = i16 + 1;
                d17 += d19 + ((i17 < length ? dArr3[i17] : d3) * 0.5d);
            }
            if (d17 > d15) {
                i15 = i14;
                d15 = d17;
            }
            i14++;
            d5 = d16;
        }
        double d20 = d5;
        ArrayList arrayList = new ArrayList();
        for (double d21 = i15; d21 < length; d21 += d10) {
            arrayList.add(Long.valueOf((long) ((((256 * d21) + 256.0d) / d4) * 1000)));
        }
        return new Result((float) (60.0d / (d10 * d20)), arrayList);
    }

    public final List<Long> every(List<Long> beats, int n) {
        Intrinsics.checkNotNullParameter(beats, "beats");
        ArrayList arrayList = new ArrayList();
        int i = 0;
        for (Object obj : beats) {
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            ((Number) obj).longValue();
            if (i % n == 0) {
                arrayList.add(obj);
            }
            i = i2;
        }
        return arrayList;
    }
}
