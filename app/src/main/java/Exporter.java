package com.mukhda.editor;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.Effect;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.audio.AudioProcessor;
import androidx.media3.common.audio.ChannelMixingAudioProcessor;
import androidx.media3.common.audio.ChannelMixingMatrix;
import androidx.media3.common.audio.SonicAudioProcessor;
import androidx.media3.effect.Contrast;
import androidx.media3.effect.GlEffect;
import androidx.media3.effect.HslAdjustment;
import androidx.media3.effect.OverlayEffect;
import androidx.media3.effect.Presentation;
import androidx.media3.effect.RgbAdjustment;
import androidx.media3.effect.RgbFilter;
import androidx.media3.effect.RgbMatrix;
import androidx.media3.effect.ScaleAndRotateTransformation;
import androidx.media3.effect.SpeedChangeEffect;
import androidx.media3.transformer.Composition;
import androidx.media3.transformer.DefaultEncoderFactory;
import androidx.media3.transformer.EditedMediaItem;
import androidx.media3.transformer.EditedMediaItemSequence;
import androidx.media3.transformer.Effects;
import androidx.media3.transformer.Transformer;
import androidx.media3.transformer.VideoEncoderSettings;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

public final class Exporter {
    public static final Exporter INSTANCE = new Exporter();

    private Exporter() {
    }

    private final List<Effect> filterEffects(String name) {
        switch (name.hashCode()) {
            case 3157:
                if (name.equals("bw")) {
                    RgbFilter createGrayscaleFilter = RgbFilter.createGrayscaleFilter();
                    Intrinsics.checkNotNullExpressionValue(createGrayscaleFilter, "createGrayscaleFilter(...)");
                    return CollectionsKt.listOf(new RgbMatrix[]{createGrayscaleFilter, new Contrast(0.15f)});
                }
                return CollectionsKt.emptyList();
            case 3059529:
                if (name.equals("cool")) {
                    RgbAdjustment build = new RgbAdjustment.Builder().setRedScale(0.9f).setBlueScale(1.12f).build();
                    Intrinsics.checkNotNullExpressionValue(build, "build(...)");
                    return CollectionsKt.listOf(new RgbMatrix[]{build, new Contrast(0.05f)});
                }
                return CollectionsKt.emptyList();
            case 3083853:
                if (name.equals("diya")) {
                    RgbAdjustment build2 = new RgbAdjustment.Builder().setRedScale(1.12f).setGreenScale(1.0f).setBlueScale(0.82f).build();
                    Intrinsics.checkNotNullExpressionValue(build2, "build(...)");
                    HslAdjustment build3 = new HslAdjustment.Builder().adjustSaturation(25.0f).build();
                    Intrinsics.checkNotNullExpressionValue(build3, "build(...)");
                    return CollectionsKt.listOf(new GlEffect[]{build2, build3, new Contrast(0.1f)});
                }
                return CollectionsKt.emptyList();
            case 3641989:
                if (name.equals("warm")) {
                    RgbAdjustment build4 = new RgbAdjustment.Builder().setRedScale(1.1f).setGreenScale(1.02f).setBlueScale(0.88f).build();
                    Intrinsics.checkNotNullExpressionValue(build4, "build(...)");
                    return CollectionsKt.listOf(new RgbMatrix[]{build4, new Contrast(0.08f)});
                }
                return CollectionsKt.emptyList();
            case 112220286:
                if (name.equals("vivid")) {
                    HslAdjustment build5 = new HslAdjustment.Builder().adjustSaturation(35.0f).build();
                    Intrinsics.checkNotNullExpressionValue(build5, "build(...)");
                    return CollectionsKt.listOf(new GlEffect[]{build5, new Contrast(0.12f)});
                }
                return CollectionsKt.emptyList();
            default:
                return CollectionsKt.emptyList();
        }
    }

    public final int bitrateFor(int height, Preset preset) {
        Intrinsics.checkNotNullParameter(preset, "preset");
        return (height >= 2160 ? 25000 : height >= 1440 ? 12000 : height >= 1080 ? RangesKt.coerceAtMost(Math.max(preset.getVideoKbps(), 5000), RangesKt.coerceAtLeast(preset.getVideoKbps(), 5000)) : Math.min(preset.getVideoKbps(), 3500)) * 1000;
    }

    public final Composition buildComposition(Context ctx, Project p, ExportOptions o) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(p, "p");
        Intrinsics.checkNotNullParameter(o, "o");
        Pair<Integer, Integer> outputSize = outputSize(ctx, p, o);
        int intValue = outputSize.component1().intValue();
        int intValue2 = outputSize.component2().intValue();
        ArrayList arrayList = new ArrayList();
        for (Clip clip : p.getClips()) {
            MediaItem build = clip.isImage() ? new MediaItem.Builder().setUri(clip.getUri()).setImageDurationMs(clip.getLengthMs()).build() : new MediaItem.Builder().setUri(clip.getUri()).setClippingConfiguration(new MediaItem.ClippingConfiguration.Builder().setStartPositionMs(clip.getStartMs()).setEndPositionMs(clip.getEndMs()).build()).build();
            Intrinsics.checkNotNull(build);
            EditedMediaItem.Builder builder = new EditedMediaItem.Builder(build);
            if (clip.isImage()) {
                builder.setDurationUs(clip.getLengthMs() * 1000).setFrameRate(30);
            }
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            if (!clip.isImage() && clip.getSpeed() != 1.0f) {
                SonicAudioProcessor sonicAudioProcessor = new SonicAudioProcessor();
                sonicAudioProcessor.setSpeed(clip.getSpeed());
                sonicAudioProcessor.setPitch(1.0f);
                arrayList2.add(sonicAudioProcessor);
                arrayList3.add(new SpeedChangeEffect(clip.getSpeed()));
            }
            if (clip.getZoom() != 1.0f) {
                arrayList3.add(new ScaleAndRotateTransformation.Builder().setScale(clip.getZoom(), clip.getZoom()).build());
            }
            if (!clip.isImage() && p.getOriginalVolume() != 1.0f) {
                arrayList2.add(volumeProcessor(p.getOriginalVolume()));
            }
            builder.setEffects(new Effects(arrayList2, arrayList3));
            arrayList.add(builder.build());
        }
        ArrayList arrayList4 = new ArrayList();
        arrayList4.add(new EditedMediaItemSequence(arrayList));
        if (p.getMusicUri() != null) {
            long totalMs = p.getTotalMs();
            Media media = Media.INSTANCE;
            Uri parse = Uri.parse(p.getMusicUri());
            Intrinsics.checkNotNullExpressionValue(parse, "parse(...)");
            long videoDuration = media.videoDuration(ctx, parse);
            boolean z = false;
            if (1 <= videoDuration && videoDuration < totalMs) {
                z = true;
            }
            MediaItem.Builder uri = new MediaItem.Builder().setUri(p.getMusicUri());
            Intrinsics.checkNotNullExpressionValue(uri, "setUri(...)");
            if (!z) {
                uri.setClippingConfiguration(new MediaItem.ClippingConfiguration.Builder().setStartPositionMs(0L).setEndPositionMs(totalMs).build());
            }
            EditedMediaItem build2 = new EditedMediaItem.Builder(uri.build()).setRemoveVideo(true).build();
            Intrinsics.checkNotNullExpressionValue(build2, "build(...)");
            arrayList4.add(new EditedMediaItemSequence((List<EditedMediaItem>) CollectionsKt.listOf(build2), z));
        }
        ArrayList arrayList5 = new ArrayList();
        arrayList5.add(Presentation.createForWidthAndHeight(intValue, intValue2, o.getFill() ? 1 : 0));
        arrayList5.addAll(filterEffects(p.getFilter()));
        List<TimedCaption> timedCaptions = Timeline.INSTANCE.timedCaptions(p);
        if (!timedCaptions.isEmpty()) {
            arrayList5.add(new OverlayEffect(ImmutableList.of(new CaptionOverlay(ctx, timedCaptions, p.getCaptionStyle(), intValue, intValue2))));
        }
        Composition build3 = new Composition.Builder(arrayList4).experimentalSetForceAudioTrack(true).setHdrMode(2).setEffects(new Effects(CollectionsKt.emptyList(), arrayList5)).build();
        Intrinsics.checkNotNullExpressionValue(build3, "build(...)");
        return build3;
    }

    public final int effectiveHeight(ExportOptions o) {
        Intrinsics.checkNotNullParameter(o, "o");
        int min = Math.min(o.getHeightCap(), maxEncoderHeight());
        return o.getLite() ? Math.min(min, 720) : min;
    }

    public final long estimateBytes(Project p, ExportOptions o) {
        Intrinsics.checkNotNullParameter(p, "p");
        Intrinsics.checkNotNullParameter(o, "o");
        return (long) ((((p.getTotalMs() / 1000.0d) * ((bitrateFor(effectiveHeight(o), o.getPreset()) / 1000) + 128)) * 1000) / 8);
    }

    public final int maxEncoderHeight() {
        try {
            MediaCodecInfo[] codecInfos = new MediaCodecList(0).getCodecInfos();
            Intrinsics.checkNotNull(codecInfos);
            int i = 720;
            for (MediaCodecInfo mediaCodecInfo : codecInfos) {
                if (mediaCodecInfo.isEncoder()) {
                    String[] supportedTypes = mediaCodecInfo.getSupportedTypes();
                    Intrinsics.checkNotNullExpressionValue(supportedTypes, "getSupportedTypes(...)");
                    String[] strArr = supportedTypes;
                    int length = strArr.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            break;
                        }
                        if (StringsKt.equals(strArr[i2], MimeTypes.VIDEO_H264, true)) {
                            MediaCodecInfo.VideoCapabilities videoCapabilities = mediaCodecInfo.getCapabilitiesForType(MimeTypes.VIDEO_H264).getVideoCapabilities();
                            Integer upper = videoCapabilities.getSupportedHeights().getUpper();
                            Intrinsics.checkNotNullExpressionValue(upper, "getUpper(...)");
                            int intValue = upper.intValue();
                            Integer upper2 = videoCapabilities.getSupportedWidths().getUpper();
                            Intrinsics.checkNotNullExpressionValue(upper2, "getUpper(...)");
                            Math.min(intValue, upper2.intValue());
                            if (videoCapabilities.areSizeAndRateSupported(1080, 1920, 30.0d)) {
                                i = Math.max(i, 1080);
                            }
                            if (videoCapabilities.areSizeAndRateSupported(1440, 2560, 30.0d)) {
                                i = Math.max(i, 1440);
                            }
                            if (videoCapabilities.areSizeAndRateSupported(2160, 3840, 30.0d)) {
                                i = Math.max(i, 2160);
                            }
                        } else {
                            i2++;
                        }
                    }
                }
            }
            return i;
        } catch (Exception unused) {
            return 1080;
        }
    }

    public final Transformer newTransformer(Context ctx, ExportOptions o, Transformer.Listener listener) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(o, "o");
        Intrinsics.checkNotNullParameter(listener, "listener");
        DefaultEncoderFactory build = new DefaultEncoderFactory.Builder(ctx).setRequestedVideoEncoderSettings(new VideoEncoderSettings.Builder().setBitrate(bitrateFor(effectiveHeight(o), o.getPreset())).build()).setEnableFallback(true).build();
        Intrinsics.checkNotNullExpressionValue(build, "build(...)");
        Transformer build2 = new Transformer.Builder(ctx).setVideoMimeType(MimeTypes.VIDEO_H264).setAudioMimeType(MimeTypes.AUDIO_AAC).setEncoderFactory(build).addListener(listener).build();
        Intrinsics.checkNotNullExpressionValue(build2, "build(...)");
        return build2;
    }

    public final Pair<Integer, Integer> outputSize(Context ctx, Project p, ExportOptions o) {
        float f;
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(p, "p");
        Intrinsics.checkNotNullParameter(o, "o");
        int effectiveHeight = effectiveHeight(o);
        String aspect = o.getAspect();
        int hashCode = aspect.hashCode();
        if (hashCode == 48936) {
            if (aspect.equals("1:1")) {
                f = 1.0f;
            }
            f = sourceAspect(ctx, p);
        } else if (hashCode != 1513508) {
            if (hashCode == 1755398 && aspect.equals("9:16")) {
                f = 0.5625f;
            }
            f = sourceAspect(ctx, p);
        } else {
            if (aspect.equals("16:9")) {
                f = 1.7777778f;
            }
            f = sourceAspect(ctx, p);
        }
        Pair pair = f >= 1.0f ? new Pair(Integer.valueOf((int) (effectiveHeight * f)), Integer.valueOf(effectiveHeight)) : new Pair(Integer.valueOf(effectiveHeight), Integer.valueOf((int) (effectiveHeight / f)));
        return new Pair<>(Integer.valueOf(((Number) pair.component1()).intValue() & (-2)), Integer.valueOf(((Number) pair.component2()).intValue() & (-2)));
    }

    public final Uri saveToGallery(Context ctx, File file, String displayName) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(file, "file");
        Intrinsics.checkNotNullParameter(displayName, "displayName");
        if (Build.VERSION.SDK_INT < 29) {
            File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Mukhda");
            file2.mkdirs();
            File file3 = new File(file2, displayName);
            FilesKt.copyTo(file, file3, true, 4096);
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", file3.getAbsolutePath());
            contentValues.put("_display_name", displayName);
            contentValues.put("mime_type", MimeTypes.VIDEO_MP4);
            return ctx.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        }
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put("_display_name", displayName);
        contentValues2.put("mime_type", MimeTypes.VIDEO_MP4);
        contentValues2.put("relative_path", Environment.DIRECTORY_MOVIES + "/Mukhda");
        contentValues2.put("is_pending", (Integer) 1);
        Uri insert = ctx.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues2);
        if (insert == null) {
            return null;
        }
        OutputStream openOutputStream;
        FileInputStream fileInputStream;
        try {
            openOutputStream = ctx.getContentResolver().openOutputStream(insert);
            Intrinsics.checkNotNull(openOutputStream);
            fileInputStream = new FileInputStream(file);
        } catch (Exception e) { throw new RuntimeException(e); }
        try {
            OutputStream outputStream = openOutputStream;
            try {
                ByteStreamsKt.copyTo(fileInputStream, outputStream, 8192);
            } finally {
                try { outputStream.close(); } catch (Exception ignored) {}
            }
        } finally {
            try { fileInputStream.close(); } catch (Exception ignored) {}
        }
        ContentValues contentValues3 = new ContentValues();
        contentValues3.put("is_pending", (Integer) 0);
        ctx.getContentResolver().update(insert, contentValues3, null, null);
        return insert;
    }

    public final float sourceAspect(Context ctx, Project p) {
        Integer intOrNull;
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(p, "p");
        Clip clip = (Clip) CollectionsKt.firstOrNull((List) p.getClips());
        if (clip == null) {
            return 0.5625f;
        }
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(ctx, Uri.parse(clip.getUri()));
            String extractMetadata = mediaMetadataRetriever.extractMetadata(18);
            Float floatOrNull = extractMetadata != null ? StringsKt.toFloatOrNull(extractMetadata) : null;
            String extractMetadata2 = mediaMetadataRetriever.extractMetadata(19);
            Float floatOrNull2 = extractMetadata2 != null ? StringsKt.toFloatOrNull(extractMetadata2) : null;
            String extractMetadata3 = mediaMetadataRetriever.extractMetadata(24);
            int intValue = (extractMetadata3 == null || (intOrNull = StringsKt.toIntOrNull(extractMetadata3)) == null) ? 0 : intOrNull.intValue();
            if (clip.isImage() || floatOrNull == null || floatOrNull2 == null) {
                InputStream openInputStream = ctx.getContentResolver().openInputStream(Uri.parse(clip.getUri()));
                Intrinsics.checkNotNull(openInputStream);
                InputStream inputStream = openInputStream;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inputStream, null, options);
                    CloseableKt.closeFinally(inputStream, null);
                    Float valueOf = Float.valueOf(options.outWidth);
                    floatOrNull2 = Float.valueOf(options.outHeight);
                    floatOrNull = valueOf;
                } finally {
                }
            }
            mediaMetadataRetriever.release();
            Pair pair = (intValue == 90 || intValue == 270) ? new Pair(floatOrNull2, floatOrNull) : new Pair(floatOrNull, floatOrNull2);
            float floatValue = ((Number) pair.component1()).floatValue();
            float floatValue2 = ((Number) pair.component2()).floatValue();
            if (floatValue <= 0.0f || floatValue2 <= 0.0f) {
                return 0.5625f;
            }
            return floatValue / floatValue2;
        } catch (Exception unused) {
            return 0.5625f;
        }
    }

    public final AudioProcessor volumeProcessor(float vol) {
        ChannelMixingAudioProcessor channelMixingAudioProcessor = new ChannelMixingAudioProcessor();
        for (int i = 1; i < 3; i++) {
            channelMixingAudioProcessor.putChannelMixingMatrix(ChannelMixingMatrix.create(i, i).scaleBy(vol));
        }
        return channelMixingAudioProcessor;
    }
}
