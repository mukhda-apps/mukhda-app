package com.mukhda.editor;

import android.net.Uri;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.MimeTypes;
import com.mukhda.editor.BeatDetector;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

public final class EditorActivity$analyseBeats$1 extends Lambda<Unit> implements Function0<Unit> {
    final String $uri;
    final EditorActivity this$0;

    public EditorActivity$analyseBeats$1(EditorActivity editorActivity, String str) {
        super(0);
        this.this$0 = editorActivity;
        this.$uri = str;
    }

    public static final void invoke$lambda$1(BeatDetector.Result result, EditorActivity this$0) {
        TimelineView timelineView;
        List<Long> list;
        String str;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (result == null) {
            this$0.beats = CollectionsKt.emptyList();
            this$0.bpm = 0.0f;
            this$0.toast("Could not find a clear beat in this music");
        } else {
            this$0.beats = result.getBeatsMs();
            this$0.bpm = result.getBpm();
        }
        timelineView = this$0.timeline;
        if (timelineView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("timeline");
            timelineView = null;
        }
        list = this$0.beats;
        timelineView.setBeats(list);
        str = this$0.tool;
        if (Intrinsics.areEqual(str, MimeTypes.BASE_TYPE_AUDIO)) {
            this$0.showTool(MimeTypes.BASE_TYPE_AUDIO);
        }
    }

    @Override
    public Unit invoke() {
        invoke2();
        return Unit.INSTANCE;
    }

    public final void invoke2() {
        short[] sArr;
        Handler handler;
        try {
            AudioDecoder audioDecoder = AudioDecoder.INSTANCE;
            EditorActivity editorActivity = this.this$0;
            Uri parse = Uri.parse(this.$uri);
            Intrinsics.checkNotNullExpressionValue(parse, "parse(...)");
            sArr = audioDecoder.decodeAll(editorActivity, parse, 0L, 120000L);
        } catch (Throwable unused) {
            sArr = null;
        }
        final BeatDetector.Result detect$default = sArr != null ? BeatDetector.INSTANCE.detect(sArr, 0) : null;
        handler = this.this$0.main;
        final EditorActivity editorActivity2 = this.this$0;
        handler.post(new Runnable() {
            @Override
            public final void run() {
                EditorActivity$analyseBeats$1.invoke$lambda$1(detect$default, editorActivity2);
            }
        });
    }
}
