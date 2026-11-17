package com.mukhda.editor;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.view.Surface;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

public final class AudioDecoder {
    public static final AudioDecoder INSTANCE = new AudioDecoder();
    public static final int RATE = 16000;

    private AudioDecoder() {
    }

    public static boolean decode$default(AudioDecoder audioDecoder, Context context, Uri uri, long j, long j2, Function0 function0, Function2 function2, int i, Object obj) {
        return audioDecoder.decode(context, uri, j, j2, (i & 16) != 0 ? new Function0<Boolean>() {
            @Override
            public final Boolean invoke() {
                return false;
            }
        } : function0, function2);
    }

    public final boolean decode(Context ctx, Uri uri, long startMs, long endMs, Function0<Boolean> isCancelled, Function2<? super short[], ? super Integer, Unit> sink) {
        MediaExtractor mediaExtractor = null;
        boolean z;
        String str;
        String str2;
        int i;
        long j;
        int i2;
        Function2<? super short[], ? super Integer, Unit> function2;
        boolean z2;
        double d;
        short[] sArr;
        int i3;
        float f;
        int dequeueInputBuffer;
        String str3 = "pcm-encoding";
        String str4 = "sample-rate";
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(isCancelled, "isCancelled");
        Intrinsics.checkNotNullParameter(sink, "sink");
        MediaExtractor mediaExtractor2 = new MediaExtractor();
        try {
            mediaExtractor2.setDataSource(ctx, uri, (Map<String, String>) null);
            int trackCount = mediaExtractor2.getTrackCount();
            int i4 = 0;
            while (true) {
                if (i4 >= trackCount) {
                    i4 = -1;
                    break;
                }
                String string = mediaExtractor2.getTrackFormat(i4).getString("mime");
                if (string != null && StringsKt.startsWith(string, "audio/", false)) {
                    break;
                }
                i4++;
            }
            if (i4 < 0) {
                mediaExtractor2.release();
                return false;
            }
            mediaExtractor2.selectTrack(i4);
            MediaFormat trackFormat = mediaExtractor2.getTrackFormat(i4);
            Intrinsics.checkNotNullExpressionValue(trackFormat, "getTrackFormat(...)");
            String string2 = trackFormat.getString("mime");
            Intrinsics.checkNotNull(string2);
            if (startMs > 0) {
                mediaExtractor2.seekTo(1000 * startMs, 2);
            }
            MediaCodec createDecoderByType = MediaCodec.createDecoderByType(string2);
            Intrinsics.checkNotNullExpressionValue(createDecoderByType, "createDecoderByType(...)");
            createDecoderByType.configure(trackFormat, (Surface) null, (MediaCrypto) null, 0);
            createDecoderByType.start();
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            long j2 = 1000 * endMs;
            boolean z3 = false;
            boolean z4 = false;
            short[] sArr2 = new short[8192];
            double d2 = 0.0d;
            float f2 = 0.0f;
            int integer = trackFormat.getInteger("channel-count");
            int integer2 = trackFormat.getInteger("sample-rate");
            boolean z5 = false;
            while (!z3) {
                try {
                    try {
                        if (isCancelled.invoke().booleanValue()) {
                            break;
                        }
                        if (!z5 && (dequeueInputBuffer = createDecoderByType.dequeueInputBuffer(10000L)) >= 0) {
                            ByteBuffer inputBuffer = createDecoderByType.getInputBuffer(dequeueInputBuffer);
                            Intrinsics.checkNotNull(inputBuffer);
                            int readSampleData = mediaExtractor2.readSampleData(inputBuffer, 0);
                            long sampleTime = mediaExtractor2.getSampleTime();
                            if (readSampleData >= 0 && (endMs <= 0 || sampleTime <= j2)) {
                                createDecoderByType.queueInputBuffer(dequeueInputBuffer, 0, readSampleData, sampleTime, 0);
                                mediaExtractor2.advance();
                            }
                            createDecoderByType.queueInputBuffer(dequeueInputBuffer, 0, 0, 0L, 4);
                            z5 = true;
                        }
                        int dequeueOutputBuffer = createDecoderByType.dequeueOutputBuffer(bufferInfo, 10000L);
                        if (dequeueOutputBuffer >= 0) {
                            if (bufferInfo.size > 0) {
                                ByteBuffer outputBuffer = createDecoderByType.getOutputBuffer(dequeueOutputBuffer);
                                Intrinsics.checkNotNull(outputBuffer);
                                outputBuffer.position(bufferInfo.offset);
                                z = z5;
                                outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                                int i5 = bufferInfo.size / ((z4 ? 4 : 2) * integer);
                                j = j2;
                                mediaExtractor = mediaExtractor2;
                                double d3 = integer2 / 16000;
                                try {
                                    float[] fArr = new float[i5];
                                    ByteBuffer order = outputBuffer.order(ByteOrder.nativeOrder());
                                    int i6 = 0;
                                    while (i6 < i5) {
                                        int i7 = 0;
                                        float f3 = 0.0f;
                                        while (i7 < integer) {
                                            if (z4) {
                                                i3 = integer2;
                                                f = order.getFloat() * 32768.0f;
                                            } else {
                                                i3 = integer2;
                                                f = order.getShort();
                                            }
                                            f3 += f;
                                            i7++;
                                            integer2 = i3;
                                        }
                                        fArr[i6] = f3 / integer;
                                        i6++;
                                        integer2 = integer2;
                                    }
                                    i = integer2;
                                    int floor = i5 == 0 ? 0 : ((int) Math.floor(((i5 - 1) - d2) / d3)) + 1;
                                    if (floor > 0) {
                                        int length = sArr2.length;
                                        short[] sArr3 = sArr2;
                                        if (length < floor) {
                                            sArr3 = new short[floor];
                                        }
                                        int i8 = integer;
                                        double d4 = d2;
                                        int i9 = 0;
                                        while (i9 < floor) {
                                            int i10 = i8;
                                            int i11 = (int) d4;
                                            String str5 = str3;
                                            String str6 = str4;
                                            float f4 = (float) (d4 - i11);
                                            float f5 = i11 < 0 ? f2 : fArr[i11];
                                            sArr3[i9] = (short) RangesKt.coerceIn(f5 + (((i11 + 1 < i5 ? fArr[i11 + 1] : f5) - f5) * f4), -32768.0f, 32767.0f);
                                            d4 += d3;
                                            i8 = i10;
                                            str3 = str5;
                                            i9++;
                                            str4 = str6;
                                        }
                                        str = str3;
                                        str2 = str4;
                                        i2 = i8;
                                        d = d4 - i5;
                                        function2 = sink;
                                        function2.invoke(sArr3, Integer.valueOf(floor));
                                        sArr = sArr3;
                                    } else {
                                        str = str3;
                                        str2 = str4;
                                        i2 = integer;
                                        function2 = sink;
                                        d = d2 - i5;
                                        sArr = sArr2;
                                    }
                                    if (i5 > 0) {
                                        f2 = fArr[i5 - 1];
                                    }
                                    d2 = d;
                                    sArr2 = sArr;
                                } catch (Throwable th) {
                                    th = th;
                                    try {
                                        createDecoderByType.stop();
                                    } catch (Exception unused) {
                                    }
                                    createDecoderByType.release();
                                    throw th;
                                }
                            } else {
                                z = z5;
                                str = str3;
                                str2 = str4;
                                mediaExtractor = mediaExtractor2;
                                i = integer2;
                                j = j2;
                                i2 = integer;
                                function2 = sink;
                                sArr2 = sArr2;
                            }
                            if ((bufferInfo.flags & 4) != 0) {
                                z2 = false;
                                z3 = true;
                            } else {
                                z2 = false;
                            }
                            createDecoderByType.releaseOutputBuffer(dequeueOutputBuffer, z2);
                        } else {
                            z = z5;
                            str = str3;
                            str2 = str4;
                            mediaExtractor = mediaExtractor2;
                            i = integer2;
                            j = j2;
                            i2 = integer;
                            z2 = false;
                            function2 = sink;
                            if (dequeueOutputBuffer == -2) {
                                MediaFormat outputFormat = createDecoderByType.getOutputFormat();
                                Intrinsics.checkNotNullExpressionValue(outputFormat, "getOutputFormat(...)");
                                integer = outputFormat.getInteger("channel-count");
                                integer2 = outputFormat.getInteger(str2);
                                str3 = str;
                                z4 = outputFormat.containsKey(str3) && outputFormat.getInteger(str3) == 4;
                                z5 = z;
                                j2 = j;
                                mediaExtractor2 = mediaExtractor;
                                d2 = 0.0d;
                                str4 = str2;
                            }
                        }
                        z5 = z;
                        j2 = j;
                        mediaExtractor2 = mediaExtractor;
                        integer = i2;
                        integer2 = i;
                        str3 = str;
                        str4 = str2;
                    } catch (Throwable th2) {
                        throw new RuntimeException(th2);
                    }
                } catch (Throwable th3) {
                    mediaExtractor.release();
                    throw th3;
                }
            }
            MediaExtractor mediaExtractor3 = mediaExtractor2;
            try {
                createDecoderByType.stop();
            } catch (Exception unused2) {
            }
            createDecoderByType.release();
            mediaExtractor3.release();
            return true;
        } catch (Throwable th4) {
            return false;
        }
    }

    public final short[] decodeAll(Context ctx, Uri uri, long startMs, long endMs) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(uri, "uri");
        final Ref.ObjectRef<short[]> objectRef = new Ref.ObjectRef();
        objectRef.element = new short[480000];
        final Ref.IntRef intRef = new Ref.IntRef();
        if (!decode$default(this, ctx, uri, startMs, endMs, null, new Function2<short[], Integer, Unit>() {

            @Override
            public Unit invoke(short[] sArr, Integer num) {
                invoke(sArr, num.intValue());
                return Unit.INSTANCE;
            }

            public final void invoke(short[] chunk, int i) {
                Intrinsics.checkNotNullParameter(chunk, "chunk");
                if (intRef.element + i > objectRef.element.length) {
                    Ref.ObjectRef<short[]> objectRef2 = objectRef;
                    short[] copyOf = Arrays.copyOf(objectRef2.element, Math.max(objectRef.element.length * 2, intRef.element + i));
                    Intrinsics.checkNotNullExpressionValue(copyOf, "copyOf(...)");
                    objectRef2.element = copyOf;
                }
                System.arraycopy(chunk, 0, objectRef.element, intRef.element, i);
                intRef.element += i;
            }
        }, 16, null)) {
            return null;
        }
        short[] copyOf = Arrays.copyOf((short[]) objectRef.element, intRef.element);
        Intrinsics.checkNotNullExpressionValue(copyOf, "copyOf(...)");
        return copyOf;
    }
}
